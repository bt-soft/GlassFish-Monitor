/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationsMonitor.java
 *  Created: 2018.01.21. 9:55:13
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.config.PropertiesConfig;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ApplicationCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.ApplicationService;
import hu.btsoft.gfmon.engine.model.service.ApplicationSnapshotService;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.monitor.management.ApplicationsDiscoverer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
 *
 * @author BT
 */
/**
 * Alkalmazás adatokat összegyűjtő SLSB
 *
 * @author BT
 */
@Stateless
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class ApplicationsMonitor extends MonitorsBase {

    private static final String DB_MODIFICATOR_USER = "app-mon-ctrl";

    @Inject
    private PropertiesConfig propertiesConfig;

    @EJB
    private ServerService serverService;

    @EJB
    private ApplicationService applicationService;

    @Inject
    private ApplicationsDiscoverer applicationsDiscoverer;

    @Inject
    private ApplicationSnapshotProvider applicationSnapshotProvider;

    @EJB
    private ApplicationSnapshotService applicationSnapshotService;

    @EJB
    private ApplicationCollectorDataUnitService applicationCollectorDataUnitService;

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
        return "App-Monitor";
    }

    @Override
    public void beforeStartTimer() {
        //Alkalmazások lekérdezése és felépítése
        this.manageAllActiverServerApplications();
    }

    /**
     * Adott szerver alkalmazásainak összegyűjtése
     * Adatbázidban nem frissít
     *
     * @param server kiválasztott szerver
     *
     * @return alkalmazások listája vagy null
     */
    public List<Application> getApplicationsList(Server server) {

        //Runime lekérjük a szervertől az alkalmazások listáját
        List<Application> aplications = applicationsDiscoverer.getAplications(server);

        return aplications;
    }

    /**
     * Egy szerver alkalmazásainak feltérképezése !!!! ÉS az adatbázisban történő frissítése !!!
     * <p>
     * - megnézi, hogy az App szerepel-e az adatbázisban (rövid név + hosszú név azonos-e a db-belivel)
     * <p>
     * - Ha nem szerepel az adatbázisban:
     * -- felveszi az alkalmazást az adatbázisba, de még nincs engedélyetve a monitorozása
     * <p>
     * - Ha szerepel az adatbázisban:
     * -- ellenőrzi, hogy a hosszú név azonos-e?
     * --- ha azonos a hosszú név, akkor nem bántja, nem történt változás
     * --- ha nem azonos a hosszú név, akkor törli a db-belit (mert új deploy történt) és felveszi az újat, a státusza azonos lesz a korábbi db-belivel
     *
     * @param server vizsgálandó szerver
     */
    public void maintenanceAplicationsInDataBase(Server server) {
        //A szerver aktuális alkalmazás listája
        List<Application> runtimeApplications = this.getApplicationsList(server);

        //A szerver eltárolt alkalmazás listája
        List<Application> dbApplications = applicationService.findByServer(server.getId());

        //Ha a szerveren nincs alkalmazás de az adatbázisban mégis van, akkor töröljük az adatbázis beli adatokat
        if ((runtimeApplications == null || runtimeApplications.isEmpty()) && (dbApplications != null && !dbApplications.isEmpty())) {
            dbApplications.forEach((dbApp) -> {
                log.warn("Server: {} -> már nem létező alkalmazások törlése az adatbázisból", server.getSimpleUrl());
                applicationService.remove(dbApp);
            });
            return;
        }

        //Hasem a szerveren, sem az adatbázisban nincs alkalmazás, akkor nem megyünk tovább
        if (runtimeApplications == null) {
            return;
        }

        //Végigmegyünk a runtime Alkalmazások listáján
        runtimeApplications.forEach((Application runtimeApplication) -> {
            boolean needPersistNewEntity = true; //Kell új entitást felvenni?
            boolean existDbAppActiveStatus = false;

            //A poolName alapján kikeressük az adatbázis listából az jdbcPool-t
            if (dbApplications != null) {
                for (Application dbApplication : dbApplications) {
                    //Ha névre megvan, akkor tételesen összehasonlítjuk a két objektumot
                    if (Objects.equals(dbApplication.getAppShortName(), runtimeApplication.getAppShortName()) //app rövid név egyezik?
                            && Objects.equals(dbApplication.getModuleShortName(), runtimeApplication.getModuleShortName())) {  // ÉS az app modul rövid név egyezik?

                        //Ha tételesen már NEM egyezik a két objektum, akkor az adatbázisbelit töröljük, de az 'active' státuszát megőrizzük!
                        //A monitoring státusz nem része az @EquaslAndhashCode()-nak!
                        if (!Objects.equals(dbApplication, runtimeApplication)) {

                            //Elmentjük a státuszt
                            existDbAppActiveStatus = dbApplication.getActive();

                            //Beállítjuk, hogy kell menteni az új entitást
                            needPersistNewEntity = true;

                            //töröljük az adatbázisból!
                            log.info("Server: {} -> a(z) '{}-{}' alkalmazás törlése az adatbázisból", server.getSimpleUrl(), dbApplication.getAppRealName(), dbApplication.getModuleRealName());
                            applicationService.remove(dbApplication);

                        } else {

                            //tételesen is egyezik -> nem kell menteni
                            needPersistNewEntity = false;
                        }

                        break;
                    }
                }
            }

            if (needPersistNewEntity) {
                //Új JDBC erőforrás felvétele

                runtimeApplication.setActive(existDbAppActiveStatus); //beállítjuk a korábbi monitoring státuszt

                //CDU összerendelést is elvégezzük, egyelőre a memóriában
                applicationService.assignApplicationToCdu(runtimeApplication, DB_MODIFICATOR_USER);

                //Mindenestűl lementjük
                applicationService.save(runtimeApplication, DB_MODIFICATOR_USER);
                log.info("Server: {} -> a(z) '{} - {}' új alkalmazás felvétele az adatbázisba", server.getSimpleUrl(), runtimeApplication.getAppRealName(), runtimeApplication.getModuleRealName());

            }
        });
    }

    /**
     * Alkalmazások listájának felépítése, ha szükséges
     */
    public void manageAllActiverServerApplications() {

        //Végigmegyünk az összes szerveren
        serverService.findAllActiveServer().stream()
                .filter((server) -> super.acquireSessionToken(server)) // ha nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                .forEachOrdered((server) -> {
                    this.maintenanceAplicationsInDataBase(server);
                });
    }

    /**
     * Kell a CDU-kat gyűjteni?
     *
     * @return true -> igen
     */
    private boolean wantCollectCDU() {

        //Adatnevek táblájának felépítése, ha szükséges
        if ("runtime".equalsIgnoreCase(propertiesConfig.getConfig().getString(PropertiesConfig.STARTUP_JPA_CDU_BUILD_MODE))) {

            if (applicationCollectorDataUnitService.count() < 1) {
                log.info("Az alkalmazás 'adatnevek' táblájának feltöltése az első mérés alatt szükséges!");
                return true;
            }
        }
        return false;
    }

    /**
     * Alkalmazások Mérése
     *
     * @return - csak az aszinkron hívás miatt
     */
    @Override
    @Asynchronous
    public Future<Void> startMonitoring() {

        long start = Elapsed.nowNano();

        //Hibára futott mérési oldalak, automatikusan tiltjuk őket
        //Itt FULL URL-eket kapunk vissza
        Set<String> fullUrlErroredPaths = new HashSet<>();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            long appStart = Elapsed.nowNano();

            fullUrlErroredPaths.clear();

            //Kell gyűjteni a mértékegységeket?
            Set<DataUnitDto> dataUnits = null;
            if (wantCollectCDU()) {
                dataUnits = new LinkedHashSet<>();
            }
            Set<AppSnapshotBase> applicationSnapshots = applicationSnapshotProvider.fetchSnapshot(server, dataUnits, fullUrlErroredPaths);

            //Kellett gyűjteni a mértékegységeket?
            if (dataUnits != null && !dataUnits.isEmpty()) {
                //Elmentjük a CDU-kat az adatbázisba
                applicationCollectorDataUnitService.saveCollectedDataUnits(dataUnits, DB_MODIFICATOR_USER);

                //Alkalmazás <-> Cdu összerendelés, ha az alkalmazásnak még nincs CDU-ja
                for (Application app : server.getApplications()) {
                    if (app.getJoiners().isEmpty()) {
                        applicationService.assignApplicationToCduIntoDb(app, DB_MODIFICATOR_USER);
                    }
                }
            }

            //Letiltjuk az alkalmazás gyűjtendő adat path-ját, ha nem sikerült elérni
            if (!fullUrlErroredPaths.isEmpty()) {

                String serverPathBegin = server.getUrl();

                for (String fullUrlErroredPath : fullUrlErroredPaths) {

                    //A Full URL-t lecseréljük
                    String erroredPath = fullUrlErroredPath.replace(serverPathBegin, "");
                    //Megkeressük a CDU-ban, és letiltjuk
                    for (Application app : server.getApplications()) {
                        app.getJoiners().stream()
                                .filter((joiner) -> (Objects.equals(joiner.getAppCollectorDataUnit().getRestPathMask(), erroredPath)))
                                .map((joiner) -> {
                                    joiner.setActive(false);
                                    joiner.setAdditionalMessage("A path nem érhető el, az adatgyűjtés letiltva");
                                    return joiner;
                                }).forEachOrdered((joiner) -> {
                            applicationService.save(app, DB_MODIFICATOR_USER);
                        });

                    }
                }
            }

            measuredServerCnt++;
            if (applicationSnapshots == null || applicationSnapshots.isEmpty()) {
                log.warn("Application Stat: Nincsenek menthető alkalmazás pillanatfelvételek, Szerver: {}", server.getSimpleUrl());
                return new AsyncResult<>(null);
            }

            //JPA mentés
            applicationSnapshots.forEach((snapshot) -> {
                applicationSnapshotService.save(snapshot, DB_MODIFICATOR_USER);
            });

            log.trace("Application Stat: server url: {}, snapshots: {}, elapsed: {}", server.getUrl(), applicationSnapshots.size(), Elapsed.getElapsedNanoStr(appStart));
        }

        log.trace("Application Stat összesen: szerver: {}db, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));

        return new AsyncResult<>(null);
    }

    /**
     * Napi pucolás
     */
    @Override
    public void dailyJob() {
        long start = Elapsed.nowNano();

        //Alkalmazások lekérdezése és felépítése
        this.manageAllActiverServerApplications();
        log.info("Application Stat: Alkalmazások automatikus karbantartása OK, elapsed: {}", Elapsed.getElapsedNanoStr(start));

        //Megőrzendő napok száma
        start = Elapsed.nowNano();
        Integer keepDays = configService.getInteger(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        log.info("Application Stat: Alkalmazás mérési adatok pucolása indul, keepDays: {}", keepDays);
        //Összes régi rekord törlése
        int deletedRecords = applicationSnapshotService.deleteOldRecords(keepDays);
        log.info("Application Stat: Alkalmazás mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }

}
