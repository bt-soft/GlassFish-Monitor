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
import hu.btsoft.gfmon.engine.model.service.SvrCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.SvrSnapshotService;
import hu.btsoft.gfmon.engine.monitor.management.ServerMonitoringServiceStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
     * Timer indítása előtti lépések
     */
    @Override
    public void beforeStartTimer() {

        //Runtime értékek törlése az adatbázisból
        log.trace("Szerver runtime adatok törlése");
        serverService.clearRuntimeValuesAndSave(DB_MODIFICATOR_USER);

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
     * Végigmegy az összes szerveren és megnézi, hogy:
     * - be lehet-e jelentkezni?
     * - - Ha nem akkor letiltja
     * - Be van-e kapcsolva a module-monitoring-levels szolgáltatása?
     * - - Ha nem, akkor letiltja
     * - Ha nincs a szerverhez rendel CDU, akkor most megteszi
     */
    private void checkAndPrepareServers() {

        serverService.findAllActiveServer().stream()
                .filter((server) -> !(!super.acquireSessionToken(server))) // Ha nem sikerül bejelentkezni, akkor letiltjuk és jöhet a következő szerver
                .filter((server) -> (server.getMonitoringServiceReady() == null || !server.getMonitoringServiceReady())) //Csak az aktív szerevekkel foglalkozunk
                .map((server) -> {

                    // A monitorozandó GF példányok MonitoringService (module-monitoring-levels) ellenőrzése
                    Set<String> monitorableModules = serverMonitoringServiceStatus.checkMonitorStatus(server.getSimpleUrl(), server.getUserName(), server.getSessionToken());

                    // Amely szervernek nincs engedélyezve egyetlen monitorozható modulja sem, azt jól inaktívvá tesszük
                    if (monitorableModules == null) {

                        //letiltjuk
                        server.setActive(false);

                        //Beírjuk az üzenetet az adatbázisba is
                        String kieginfo = "A szerver MonitoringService szolgáltatása nincs engedélyezve, emiatt a monitorozása le lett tiltva!";
                        serverService.updateAdditionalMessage(server, DB_MODIFICATOR_USER, kieginfo);

                        //logot is írunk
                        log.warn("{}: {}", server.getUrl(), kieginfo);

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
                //Mindent mérjünk rajta!
                serverService.assignServerToCduIntoDb(server, DB_MODIFICATOR_USER);
            }
            return server;
        }).forEachOrdered((server) -> {
            //lementjük az adatbázisba a szerver megváltozott állapotát
            serverService.save(server, DB_MODIFICATOR_USER);
        });
    }

    /**
     * Mérés
     */
    @Asynchronous
    @Override
    public void startMonitoring() {

        long start = Elapsed.nowNano();

        //Szerverek ellenőrzése
        this.checkAndPrepareServers();

        //Hibára futott mérési oldalak, automatikusan tiltjuk őket
        Set<String> erroredPaths = new HashSet<>();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            //Ha nincs mit monitorozini rajta, akkor már nem foglalkozunk vele tovább,
            // majd visszabillenthető a státusza a UI felületről
            if (!server.getMonitoringServiceReady()) {
                continue;
            }

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
                log.warn("Nincsenek menthető szerver pillanatfelvételek, szerver: {}!", server.getSimpleUrl());
                return;
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
            svrSnapshotService.flush();

            log.trace("Szerver Stat: szerver url: {}, Server snapshots: {}, elapsed: {}", server.getUrl(), serverSnapshots.size(), Elapsed.getElapsedNanoStr(start));
        }

        log.trace("Szerver Stat összesen: szerver: {}db, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Rendszeres napi karbantartás az adatbázisban
     */
    @Override
    public void dailyJob() {
        long start = Elapsed.nowNano();

        //Megőrzendő napok száma
        Integer keepDays = configService.getInteger(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        log.info("Szerver mérési adatok pucolása indul, keepDays: {}", keepDays);
        //Összes régi rekord törlése
        int deletedRecords = svrSnapshotService.deleteOldRecords(keepDays);
        log.info("Szerver mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }
}
