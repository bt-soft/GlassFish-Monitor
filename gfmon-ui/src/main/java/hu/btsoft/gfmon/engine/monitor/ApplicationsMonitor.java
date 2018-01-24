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
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ApplicationService;
import hu.btsoft.gfmon.engine.model.service.ApplicationSnapshotService;
import hu.btsoft.gfmon.engine.monitor.management.ApplicationsDiscoverer;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

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

    @EJB
    private ApplicationService applicationService;

    @Inject
    private ApplicationsDiscoverer applicationsDiscoverer;

    @Inject
    private ApplicationSnapshotProvider applicationSnapshotProvider;

    @EJB
    private ApplicationSnapshotService applicationSnapshotService;

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
     * Egy szerver alkalmazásainak feltérképezése
     *
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
    public void manageServerAplication(Server server) {
        //A szerver aktuális alkalmazás listája
        List<Application> runtimeApps = applicationsDiscoverer.getServerAplications(server.getSimpleUrl(), server.getSessionToken());

        //A szerver eltárolt alkalmazás listája
        List<Application> dbAppList = applicationService.findByServer(server.getId());

        //Ha a szerveren nincs alkalmazás de az adatbázisban mégis van, akkor töröljük az adatbázsi beli adatokat
        if ((runtimeApps == null || runtimeApps.isEmpty()) && (dbAppList != null && !dbAppList.isEmpty())) {
            dbAppList.forEach((dbApp) -> {
                applicationService.remove(dbApp);
            });
            return;
        }

        //Hasem a szerveren, sem az adatbázisban nincs alkalmazás, akkor nem megyünk tovább
        if (runtimeApps == null) {
            return;
        }

        ModelMapper modelMapper = new ModelMapper();

        //Végigmegyünk a jelenlegi alkalmazások listáján
        for (Application runtimeApp : runtimeApps) {

            //A rövid név alapján kikeressük az adatbázis listából az alkalmazást
            Application existDbApp = null;
            if (dbAppList != null) {
                for (Application dbApp : dbAppList) {
                    if (Objects.equals(dbApp.getAppShortName(), runtimeApp.getAppShortName()) //alkalmazás hosszú név egyezik?
                            && Objects.equals(dbApp.getModuleShortName(), runtimeApp.getModuleShortName()) //modul rövid név egyezik?
                            ) {
                        existDbApp = dbApp;
                        break;
                    }
                }
            }

            //Összehasonlítjuk az adatokat, ha van változás, akkor töröljük a DB-beli adatokat
            Boolean existDbAppActiveStatus = false;
            if (existDbApp != null) {

                if (!Objects.equals(existDbApp.getAppRealName(), runtimeApp.getAppRealName()) //igazi neve változott? Pl. verzió váltás volt?
                        || !Objects.equals(existDbApp.getModuleRealName(), runtimeApp.getModuleRealName()) //modul neve változott?
                        || !Objects.equals(existDbApp.getModuleEngines(), runtimeApp.getModuleEngines()) // motorok változtak?
                        || existDbApp.isEnabled() != runtimeApp.isEnabled() // engedélyezett állapot változott?
                        || !Objects.equals(existDbApp.getContextRoot(), runtimeApp.getContextRoot()) // contextRoot változott?
                        || !Objects.equals(existDbApp.getDescription(), runtimeApp.getDescription()) // leírás változott?
                        ) {
                    //Változás van!!
                    //Eltesszük az eredeti active státuszát, majd ezzel hozunk létre új bejegyzést az új adatokkal
                    existDbAppActiveStatus = existDbApp.getActive();

                    if (existDbAppActiveStatus == null) {
                        existDbAppActiveStatus = false;
                    }

                    //töröljük az adatbázisból!
                    applicationService.remove(existDbApp);
                    existDbApp = null;
                }
            }

            //létező és nem változott a neve, nem érdekel tovább
            if (existDbApp != null) {
                continue;
            }

            //Új az alkalmazás (vagy a réginek megváltozott a hosszú neve,  felvesszük az adatbázisba!
            //Az új entitás alapja a runtime listából jövő alkalmazás adatai lesznek
            Application newApp = new Application();
            modelMapper.map(runtimeApp, newApp); //mindent átmásolunk
            newApp.setServer(server);
            newApp.setActive(existDbAppActiveStatus); //beállítjuk a mentett monitoring státuszt

            applicationService.save(newApp, DB_MODIFICATOR_USER);
        }

    }

    /**
     * Alkalmazások listájának felépítése, ha szükséges
     */
    public void manageAllActiverServerApplications() {

        //Végigmegyünk az összes szerveren
        serverService.findAllActiveServer().stream()
                .filter((server) -> super.acquireSessionToken(server)) // ha nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                .forEachOrdered((server) -> {
                    this.manageServerAplication(server);
                });
    }

    /**
     * Mérés
     */
    @Override
    public void startMonitoring() {
        long start = Elapsed.nowNano();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            Set<AppSnapshotBase> applicationSnapshots = applicationSnapshotProvider.fetchSnapshot(server);
            measuredServerCnt++;

            if (applicationSnapshots == null || applicationSnapshots.isEmpty()) {
                log.warn("Nincsenek menthető alkalmazás pillanatfelvételek!");
                return;
            }

            //JPA mentés
            applicationSnapshots.stream()
                    //.parallel()  nem jó ötlet a paralel -> lock hiba lesz tőle
                    .map((snapshot) -> {
                        //lementjük az adatbázisba
                        applicationSnapshotService.save(snapshot);
                        return snapshot;
                    }).forEachOrdered((snapshot) -> {
                log.trace("Application Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
            applicationSnapshotService.flush();

        }

        log.trace("Alkalmazás adatok kigyűjtve {} db szerverre, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Napi pucolás
     */
    @Override
    public void dailyJob() {
        //Alkalmazások lekérdezése és felépítése
        this.manageAllActiverServerApplications();
    }

}
