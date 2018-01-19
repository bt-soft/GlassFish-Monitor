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

import hu.btsoft.gfmon.corelib.cdi.CdiUtils;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SvrSnapshotBase;
import hu.btsoft.gfmon.engine.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.SnapshotService;
import hu.btsoft.gfmon.engine.model.service.SvrCollectorDataUnitService;
import hu.btsoft.gfmon.engine.monitor.management.ServerMonitoringServiceStatus;
import java.util.List;
import java.util.Set;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import lombok.extern.slf4j.Slf4j;

/**
 * GF monitor vezérlő CDI bean
 *
 * @author BT
 */
@Singleton
@Startup
@DependsOn("Bootstrapper")
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class ServerMonitorController extends MonitorControllerBase {

    private static final String DB_MODIFICATORY_USER = "server-monitor-controller";

    @EJB
    private SvrCollectorDataUnitService svrCollectorDataUnitService;

    @EJB
    private SnapshotService snapshotService;

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    @Override
    public String getDbModificationUser() {
        return DB_MODIFICATORY_USER;
    }

    /**
     * A monitor kontrol Modul neve
     *
     * @return név
     */
    @Override
    public String getControllerName() {
        return "Server-Monitor";
    }

    /**
     * Timer indítása előtti lépések
     */
    @Override
    protected void beforeStartTimer() {

        //Runtime értékek törlése az adatbázisból
        serverService.clearRuntimeValuesAndSave(DB_MODIFICATORY_USER);

        //Van egyáltalán monitorizható szerver?
        List<Server> allServers = serverService.findAll();
        if (allServers == null || allServers.isEmpty()) {
            log.warn("Nincs monitorozható szerver definiálva!");
            return;
        }

        //Adatnevek táblájának felépítése
        this.checkCollectorDataUnits();
    }

    /**
     * Ellenőrzi és szükség esetén felépíti az adatnevek tábláját
     */
    private void checkCollectorDataUnits() {

        if (svrCollectorDataUnitService.count() > 1) {
            log.info("Az 'adatnevek' tábla már OK");
            return;
        }

        log.info("Adatnevek táblájának felépítése indul");
        long start = Elapsed.nowNano();

        //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookupOne-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
        ServerSnapshotProvider snapshotProvider = CdiUtils.lookupOne(ServerSnapshotProvider.class);

        List<DataUnitDto> dataUnits = null;
        for (Server server : serverService.findAllActiveServer()) {
            if (!acquireSessionToken(server)) {
                // nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                continue;
            }

            dataUnits = snapshotProvider.fetchDataUnits(server);
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
        for (DataUnitDto dto : dataUnits) {
            SvrCollectorDataUnit cdu = new SvrCollectorDataUnit(dto.getRestPath(), dto.getEntityName(), dto.getDataName(), dto.getUnit(), dto.getDescription());
            svrCollectorDataUnitService.save(cdu, DB_MODIFICATORY_USER);
        }

        log.info("Adatnevek felépítése OK, adatnevek: {}db, elapsed: {}", dataUnits.size(), Elapsed.getElapsedNanoStr(start));
    }

    /**
     * A monitorozási mintavétel indítása
     */
    @Override
    protected void startMonitoring() {

        long start = Elapsed.nowNano();

        //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookupOne-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
        ServerSnapshotProvider snapshotProvider = CdiUtils.lookupOne(ServerSnapshotProvider.class);

        int checkedServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            if (!acquireSessionToken(server)) {
                // nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                continue;
            }

            //Ha még nem tudjuk, hogy az adott szerveren be van-e kapcsolva a MonitoringService
            if (server.getMonitoringServiceReady() == null || !server.getMonitoringServiceReady()) {

                //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookupOne-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
                ServerMonitoringServiceStatus serverMonitoringServiceStatus = CdiUtils.lookupOne(ServerMonitoringServiceStatus.class);

                // A monitorozandó GF példányok MonitoringService (module-monitoring-levels) ellenőrzése
                Set<String> monitorableModules = serverMonitoringServiceStatus.checkMonitorStatus(server.getSimpleUrl(), server.getSessionToken());

                // Amely szervernek nincs engedélyezve egyetlen monitorozható modulja sem, azt jól inaktívvá tesszük
                if (monitorableModules == null) {

                    //letiltjuk
                    server.setActive(false);

                    //Beírjuk az üzenetet az adatbázisba is
                    String kieginfo = "A szerver MonitoringService szolgáltatása nincs engedélyezve, emiatt a monitorozása le lett tiltva!";
                    serverService.updateAdditionalMessage(server, DB_MODIFICATORY_USER, kieginfo);

                    //logot is írunk
                    log.warn("{}: {}", server.getUrl(), kieginfo);

                } else {
                    //Megjegyezzük, hogy a szerver moitorozható
                    server.setMonitoringServiceReady(true);
                    log.trace("A(z) {} szerver monitorozható moduljai: {}", server.getUrl(), monitorableModules);
                }

                //Az első indításkort még nem tudjuk, hogy a GF példányról milyen patháon milyen adatneveket lehet gyűjteni
                //Emiatt a DefaultConfigCreator-ban létrehozott szervereknél itt kapcsoljuk be a gyűjtendő adatneveket
                if (server.getJoiners() == null || server.getJoiners().isEmpty()) {
                    //Mindent mérjünk rajta!
                    serverService.addDefaultAllCollectorDataUnits(server, DB_MODIFICATORY_USER);
                }

                //lementjük az adatbázisba a szerver megváltozott állapotát
                serverService.save(server);

                //Ha incs mit monitorozini rajta, akkor már nem foglalkozunk vele tovább, majd visszabillenthető a státusza a UI felületről
                if (!server.getMonitoringServiceReady()) {
                    continue;
                }
            }

            log.trace("Adatgyűjtés indul: {}", server.getUrl());

            Set<SvrSnapshotBase> snapshots = snapshotProvider.fetchSnapshot(server);
            checkedServerCnt++;

            //Töröljük a kieginfót, ha van
            serverService.clearAdditionalMessage(server, DB_MODIFICATORY_USER);

            if (snapshots == null || snapshots.isEmpty()) {
                log.warn("Nincsenek menthető pillanatfelvételek!");
                return;
            }

            //JPA mentés
            snapshots.stream()
                    //.parallel()  nem jó ötlet a paralel -> lock hiba lesz tőle
                    .map((snapshot) -> {
                        //Beállítjuk, hogy melyik szerver mérési ereménye ez a pillanatfelvétel
                        snapshot.setServer(server);
                        return snapshot;
                    })
                    .map((snapshot) -> {
                        //lementjük az adatbázisba
                        snapshotService.save(snapshot);
                        return snapshot;
                    }).forEachOrdered((snapshot) -> {
                ///////////////////////////////////////////////////log.trace("Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
            snapshotService.flush();
        }

        log.trace("Monitor {} db szerverre, elapsed: {}", checkedServerCnt, Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Rendszeres napi tisztítás az adatbázisban
     */
    @Override
    protected void dailyCleanUp() {
        log.info("Szerver mérési adatok pucolása indul");

        long start = Elapsed.nowNano();

        //Megőrzendő napok száma
        Integer keepDays = configService.getInteger(IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);

        //Összes régi rekord törlése
        int deletedRecords = snapshotService.deleteOldRecords(keepDays);

        log.info("Szerver mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }

}
