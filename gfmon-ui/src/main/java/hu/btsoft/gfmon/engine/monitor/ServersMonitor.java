/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    GFMonitorController.java
 *  Created: 2017.12.23. 11:55:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.config.PropertiesConfig;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.model.service.SvrCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.SvrSnapshotService;
import hu.btsoft.gfmon.engine.monitor.management.ServerMonitoringServiceStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * GF szerver adatokat összegyűjtő SLSB
 *
 * @author BT
 */
@Stateless
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class ServersMonitor extends MonitorsBase {

    private static final String DB_MODIFICATOR_USER = "svr-mon-ctrl";

    @Inject
    private PropertiesConfig propertiesConfig;

    @EJB
    private ServerService serverService;

    @EJB
    private SvrCollectorDataUnitService svrCollectorDataUnitService;

    @Inject
    private ServerSnapshotProvider serverSnapshotProvider;

    @EJB
    private SvrSnapshotService svrSnapshotService;

    @Inject
    private ServerMonitoringServiceStatus serverMonitoringServiceStatus;

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    @Override
    protected String getDbModificationUser() {
        return DB_MODIFICATOR_USER;
    }

    /**
     * A monitor kontrol Modul neve
     *
     * @return név
     */
    @Override
    protected String getControllerName() {
        return "Server-Monitor";
    }

    /**
     * Szerver runtime adatok (sessiontoken, monabla, stb) törlése
     */
    public void clearAllServersRuntimeValues() {

        //Van egyáltalán monitorozható szerver?
        List<Server> allServers = serverService.findAll();
        if (allServers == null || allServers.isEmpty()) {
            return;
        }

        allServers.stream()
                .forEach((server) -> {
                    //Runtime értékek törlése az adatbázisból
                    log.trace("Szerver : {} runtime adatok törlése", server.getSimpleUrl());
                    server.setSessionToken(null);
                    server.setMonitoringServiceReady(null);
                    server.setRuntimeSeqId(null);
                    serverService.save(server, DB_MODIFICATOR_USER);
                });
    }

    /**
     * Végigmegy az összes szerveren és megnézi, hogy:
     * - be lehet-e jelentkezni?
     * - - Ha nem akkor letiltja
     * - Be van-e kapcsolva a module-monitoring-levels szolgáltatása?
     * - - Ha nem, akkor letiltja
     * - Ha nincs a szerverhez rendel CDU, akkor most megteszi
     */
    public void checkAndPrepareServers() {

        List<Server> allActiveServers = serverService.findAllActiveServer();
        if (allActiveServers == null || allActiveServers.isEmpty()) {
            return;
        }

        allActiveServers.stream()
                .filter((server) -> !(!super.acquireSessionToken(server))) // Ha nem sikerül bejelentkezni, akkor letiltjuk és jöhet a következő szerver
                .filter((server) -> (server.getMonitoringServiceReady() == null || !server.getMonitoringServiceReady())) //Csak az aktív szerevekkel foglalkozunk
                .map((server) -> {

                    // A monitorozandó GF példányok MonitoringService (module-monitoring-levels) ellenőrzése
                    Set<String> monitorableModules = serverMonitoringServiceStatus.checkMonitorStatus(server.getSimpleUrl(), server.getUserName(), server.getSessionToken());

                    // Amely szervernek nincs engedélyezve egyetlen monitorozható modulja sem, azt jól inaktívvá tesszük
                    if (monitorableModules == null) {

                        //letiltjuk
                        server.setActive(false);

                        //Beírjuk a letiltó üzenetet a szerver példányba
                        String kiegInfo = "A szerver MonitoringService szolgáltatása nincs engedélyezve, emiatt a monitorozása le lett tiltva!";
                        server.setAdditionalInformation(kiegInfo);

                        //logot is írunk
                        log.warn("{}: {}", server.getUrl(), kiegInfo);

                    } else {
                        //Megjegyezzük, hogy a szerver moitorozható
                        server.setMonitoringServiceReady(true);
                        log.trace("A(z) {} szerver monitorozható moduljai: {}", server.getUrl(), monitorableModules);
                    }

                    return server;
                }).map((server) -> {
            //Az első indításkort még nem tudjuk, hogy a GF példányról milyen path-on milyen adatneveket lehet gyűjteni
            //Emiatt a DefaultConfigCreator-ban létrehozott szervereknél itt kapcsoljuk be a gyűjtendő adatneveket
            if (server.getJoiners().isEmpty()) {
                serverService.assignServerToCdu(server, DB_MODIFICATOR_USER);
            }
            return server;
        }).forEachOrdered((server) -> {
            //lementjük az adatbázisba a szerver megváltozott állapotát
            serverService.save(server, DB_MODIFICATOR_USER);
        });

        //Le is mentjük a változást az adatbázisba
        serverService.flush();
    }

    /**
     * Timer indítása előtti lépések
     */
    @Override
    public void beforeStartTimer() {

        //Van egyáltalán monitorizható szerver?
        List<Server> allServers = serverService.findAll();
        if (allServers == null || allServers.isEmpty()) {
            log.warn("Nincs monitorozható szerver definiálva!");
            return;
        }

        //Adatnevek táblájának felépítése, ha szükséges
        if ("runtime".equalsIgnoreCase(propertiesConfig.getConfig().getString(PropertiesConfig.STARTUP_JPA_CDU_BUILD_MODE))) {
            this.checkCollectorDataUnits();
        }
    }

    /**
     * Ellenőrzi és szükség esetén felépíti az adatnevek tábláját
     */
    private void checkCollectorDataUnits() {

        if (svrCollectorDataUnitService.count() > 1) {
            log.info("A szerver 'adatnevek' tábla már OK");
            return;
        }

        log.info("Szerver monitor adatnevek táblájának felépítése indul");
        long start = Elapsed.nowNano();

        List<DataUnitDto> dataUnits = null;
        for (Server server : serverService.findAllActiveServer()) {
            if (!super.acquireSessionToken(server)) {
                // nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                continue;
            }

            dataUnits = serverSnapshotProvider.fetchDataUnits(server);
            if (dataUnits != null) {
                break;
            }
        }

        //Ha nem sikerült semelyik szervertől sem kigyűjteni az adatokat
        if (dataUnits == null) {
            log.warn("Nem gyűjthető ki a szerver monitor adatnevek!");
            return;
        }

        //Végigmegyünk az összes adatneven és jól beírjuk az adatbázisba őket
        dataUnits.stream()
                .map((dto) -> new SvrCollectorDataUnit(dto.getRestPath(), dto.getEntityName(), dto.getDataName(), dto.getUnit(), dto.getDescription()))
                .forEachOrdered((cdu) -> {
                    svrCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
                });

        log.info("Szerver monitor adatnevek felépítése OK, adatnevek: {}db, elapsed: {}", dataUnits.size(), Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Szerver adatok monitorozása
     *
     * @return - csak az aszinkron hívás miatt
     */
    @Override
    @Asynchronous
    public Future<Void> startMonitoring() {

        long start = Elapsed.nowNano();

        //Hibára futott mérési oldalak, automatikusan tiltjuk őket
        Set<String> erroredPaths = new HashSet<>();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            //Ha nincs mit monitorozini rajta, akkor már nem foglalkozunk vele tovább,
            // majd visszabillenthető a státusza a UI felületről
            if (server.getMonitoringServiceReady() != null && !server.getMonitoringServiceReady()) {
                continue;
            }

            long svrStart = Elapsed.nowNano();

            erroredPaths.clear();
            Set<SnapshotBase> serverSnapshots = serverSnapshotProvider.fetchSnapshot(server, erroredPaths);

            //letiltjuk a gyűjtendő adat path-ját, ha nem sikerült elérni
            if (!erroredPaths.isEmpty()) {
                for (String erroredPath : erroredPaths) {
                    server.getJoiners().stream()
                            .filter((joiner) -> (joiner.getSvrCollectorDataUnit().getRestPath().equals(erroredPath)))
                            .map((joiner) -> {
                                joiner.setActive(false);
                                return joiner;
                            }).map((joiner) -> {
                        joiner.setModifiedBy(DB_MODIFICATOR_USER);
                        return joiner;
                    }).forEachOrdered((joiner) -> {
                        joiner.setAdditionalMessage("A path nem érhető el, az adatgyűjtés letiltva");
                    });
                }
                serverService.save(server, DB_MODIFICATOR_USER);
            }

            measuredServerCnt++;

            //Sikerült a bejelentkezés -> töröljük a kieginfót, ha van
            serverService.clearAdditionalMessage(server, DB_MODIFICATOR_USER);

            if (serverSnapshots == null || serverSnapshots.isEmpty()) {
                log.warn("Szerver Stat: Nincsenek menthető szerver pillanatfelvételek, szerver: {}!", server.getSimpleUrl());
                return new AsyncResult<>(null);
            }

            //JPA mentés
            serverSnapshots.stream()
                    //.parallel()  nem jó ötlet a paralel -> lock hiba lesz tőle
                    .map((snapshot) -> {
                        //Beállítjuk, hogy melyik szerver mérési ereménye ez a pillanatfelvétel
                        snapshot.setServer(server);
                        return snapshot;
                    })
                    .map((snapshot) -> {
                        //lementjük az adatbázisba
                        svrSnapshotService.save(snapshot, DB_MODIFICATOR_USER);
                        return snapshot;
                    }).forEachOrdered((snapshot) -> {
                ///////////////////////////////////////////////////log.trace("Server Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
//            svrSnapshotService.flush();
            log.trace("Szerver Stat: szerver url: {}, snapshots: {}, elapsed: {}", server.getUrl(), serverSnapshots.size(), Elapsed.getElapsedNanoStr(svrStart));
        }

        log.trace("Szerver Stat összesen: szerver: {}db, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));
        return new AsyncResult<>(null);
    }

    /**
     * Rendszeres napi karbantartás az adatbázisban
     */
    @Override
    public void dailyJob() {
        long start = Elapsed.nowNano();

        //Megőrzendő napok száma
        Integer keepDays = configService.getInteger(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        log.info("Szerver Stat: Szerver mérési adatok pucolása indul, keepDays: {}", keepDays);
        //Összes régi rekord törlése
        int deletedRecords = svrSnapshotService.deleteOldRecords(keepDays);
        log.info("Szerver Stat: Szerver mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }
}
