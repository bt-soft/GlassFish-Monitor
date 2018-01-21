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
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SvrSnapshotBase;
import hu.btsoft.gfmon.engine.model.service.ApplicationService;
import hu.btsoft.gfmon.engine.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.SnapshotService;
import hu.btsoft.gfmon.engine.model.service.SvrCollectorDataUnitService;
import hu.btsoft.gfmon.engine.monitor.management.ServerApplications;
import hu.btsoft.gfmon.engine.monitor.management.ServerMonitoringServiceStatus;
import java.util.List;
import java.util.Objects;
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

    private static final String DB_MODIFICATOR_USER = "server-monitor-controller";

    @EJB
    private SvrCollectorDataUnitService svrCollectorDataUnitService;

    @EJB
    private SnapshotService snapshotService;

    @EJB
    private ApplicationService applicationService;

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
    protected void beforeStartTimer() {

        //Runtime értékek törlése az adatbázisból
        serverService.clearRuntimeValuesAndSave(DB_MODIFICATOR_USER);

        //Van egyáltalán monitorizható szerver?
        List<Server> allServers = serverService.findAll();
        if (allServers == null || allServers.isEmpty()) {
            log.warn("Nincs monitorozható szerver definiálva!");
            return;
        }

        //Adatnevek táblájának felépítése
        this.checkCollectorDataUnits();

        //Alkalmazások lekérdezése és felépítése
        this.manageApplications();
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
            if (!super.acquireSessionToken(server)) {
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
            svrCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
        }

        log.info("Adatnevek felépítése OK, adatnevek: {}db, elapsed: {}", dataUnits.size(), Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Alkalmazások listájának felépítése, ha szükséges
     * - megnézi, hogy az App szerepel-e az adatbázisban (rövid név + hosszú név azonos-e a db-belivel)
     *
     * - Ha nem szerepel az adatbázisban:
     * -- felveszi az alkalmazást az adatbázisba, de még nincs engedélyetve a monitorozása
     *
     * - Ha szerepel az adatbázisban:
     * -- ellenőrzi, hogy a hosszú név azonos-e?
     * --- ha azonos a hosszú név, akkor nem bántja, nem történt változás
     * --- ha nem azonos a hosszú név, akkor törli a db-belit (mert új deploy történt) és felveszi az újat, a státusza azonos lesz a korábbi db-belivel
     */
    private void manageApplications() {

        ServerApplications serverApplications = CdiUtils.lookupOne(ServerApplications.class);

        //Végigmegyünk az összes szerveren
        for (Server server : serverService.findAllActiveServer()) {

            if (!super.acquireSessionToken(server)) {
                // nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                continue;
            }

            //A szerver aktuális alkalmazás listája
            List<Application> runtimeApps = serverApplications.getServerAplications(server.getSimpleUrl(), server.getSessionToken());

            //A szerver eltárolt alkalmazás listája
            List<Application> dbAppList = applicationService.findByServer(server.getId());

            //Ha a szerveren nincs alkalmazás de az adatbázisban mégis van, akkor töröljük az adatbázsi beli adatokat
            if ((runtimeApps == null || runtimeApps.isEmpty()) && (dbAppList != null && !dbAppList.isEmpty())) {
                dbAppList.forEach((dbApp) -> {
                    applicationService.remove(dbApp);
                });
                continue;
            }

            //Hasem a szerveren, sem az adatbázisban nincs alklamazás, akkor nem megyünk tovább
            if (runtimeApps == null) {
                continue;
            }

            //Végigmegyünk a jelenlegi alkalmazások listáján
            for (Application runtimeApp : runtimeApps) {

                //A rövid név alapján kikeressük az adatbázis listából az alkalmazást
                Application existDbApp = null;
                if (dbAppList != null) {
                    for (Application dbApp : dbAppList) {
                        if (dbApp.getAppShortName().equals(runtimeApp.getAppShortName())) {
                            existDbApp = dbApp;
                            break;
                        }
                    }
                }

                //Még azonos a hosszú név?
                Boolean existDbAppActiveStatus = false;
                if (existDbApp != null && !Objects.equals(existDbApp.getAppRealName(), runtimeApp.getAppRealName())) {
                    //nem azonos!

                    //Eltesszük az eredeti active státuszát, amjd ezzel hozunk létre új bejegyzést az új hosszú névvel
                    existDbAppActiveStatus = existDbApp.getActive();
                    if (existDbAppActiveStatus == null) {
                        existDbAppActiveStatus = false;
                    }

                    //töröljük az adatbázisból!
                    applicationService.remove(existDbApp);
                    existDbApp = null;
                }

                //létező és nem változott a neve, nem érdekel tovább
                if (existDbApp != null) {
                    continue;
                }

                //Új az alkalmazás (vagy a réginek megváltozott a hosszú neve,  felvesszük az adatbázisba!
                //Az új entitás alapja a runtime listából jövő alkalmazás adatai lesznek
                Application newApp = new Application(runtimeApp.getAppShortName(), runtimeApp.getAppRealName(), existDbAppActiveStatus, server);
                newApp.setContextRoot(runtimeApp.getContextRoot());
                newApp.setDescription(runtimeApp.getDescription());
                newApp.setEnabled(runtimeApp.isEnabled());

                applicationService.save(newApp, DB_MODIFICATOR_USER);
            }
        }
    }

    /**
     * A monitorozási mintavétel indítása
     */
    @Override
    protected void startMonitoring() {

        long start = Elapsed.nowNano();

        //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookupOne-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
        ServerSnapshotProvider serverSnapshotProvider = CdiUtils.lookupOne(ServerSnapshotProvider.class);

        int checkedServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            if (!super.acquireSessionToken(server)) {
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
                    serverService.updateAdditionalMessage(server, DB_MODIFICATOR_USER, kieginfo);

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
                    serverService.addDefaultAllCollectorDataUnits(server, DB_MODIFICATOR_USER);
                }

                //lementjük az adatbázisba a szerver megváltozott állapotát
                serverService.save(server);

                //Ha incs mit monitorozini rajta, akkor már nem foglalkozunk vele tovább, majd visszabillenthető a státusza a UI felületről
                if (!server.getMonitoringServiceReady()) {
                    continue;
                }
            }

            log.trace("Adatgyűjtés indul: {}", server.getUrl());

            Set<SvrSnapshotBase> serverSnapshots = serverSnapshotProvider.fetchSnapshot(server);
            checkedServerCnt++;

            //Töröljük a kieginfót, ha van
            serverService.clearAdditionalMessage(server, DB_MODIFICATOR_USER);

            if (serverSnapshots == null || serverSnapshots.isEmpty()) {
                log.warn("Nincsenek menthető pillanatfelvételek!");
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
     * Régi rekordok törlése
     */
    private void cleanOldRecords() {
        log.info("Szerver mérési adatok pucolása indul");

        long start = Elapsed.nowNano();

        //Megőrzendő napok száma
        Integer keepDays = configService.getInteger(IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);

        //Összes régi rekord törlése
        int deletedRecords = snapshotService.deleteOldRecords(keepDays);

        log.info("Szerver mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Rendszeres napi karbantartás az adatbázisban
     */
    @Override
    protected void dailyJob() {

        //Töröljük a régi rekordokat
        cleanOldRecords();

        //Körülnézük az alkalmazások környékén
        manageApplications();
    }
}
