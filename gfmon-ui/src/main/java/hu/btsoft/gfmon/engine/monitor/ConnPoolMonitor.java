/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolMonitor.java
 *  Created: 2018.01.28. 10:44:19
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.config.PropertiesConfig;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.connpool.snapshot.ConnPoolStat;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ConnPoolCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.ConnPoolService;
import hu.btsoft.gfmon.engine.model.service.JdbcResourcesSnapshotService;
import hu.btsoft.gfmon.engine.monitor.management.ConnPoolDiscoverer;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
@Stateless
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class ConnPoolMonitor extends MonitorsBase {

    private static final String DB_MODIFICATOR_USER = "connpool-mon-ctrl";

    @Inject
    private PropertiesConfig propertiesConfig;

    @Inject
    private ConnPoolDiscoverer connPoolDiscoverer;

    @EJB
    private ConnPoolService connPoolService;

    @EJB
    private JdbcResourcesSnapshotService jdbcResourcesSnapshotService;

    @Inject
    private ConnPoolSnapshotProvider connPoolSnapshotProvider;

    @EJB
    private ConnPoolCollectorDataUnitService connPoolCollectorDataUnitService;

    @Override
    protected String getDbModificationUser() {
        return DB_MODIFICATOR_USER;
    }

    @Override
    protected String getControllerName() {
        return "Resrc-Monitor";
    }

    @Override
    public void beforeStartTimer() {
        //Alkalmazások lekérdezése és felépítése
        this.manageAllActiverServerConnPools();
    }

    /**
     * Egy szerver JDBC erőforrásainak feltérképezése !!!! ÉS az adatbázisban történő frissítése !!!
     * <p>
     * - megnézi, hogy az jdbc resources szerepel-e az adatbázisban
     *
     * @param server vizsgálandó szerver
     */
    public void maintenanceConnPoolsInDataBase(Server server) {

        //A szerver aktuális alkalmazás listája
        List<ConnPool> runtimeConnPools = this.getConnPools(server);

        //A szerver eltárolt alkalmazás listája
        List<ConnPool> dbConnPools = connPoolService.findByServer(server.getId());

        //Ha a szerveren nincs alkalmazás de az adatbázisban mégis van, akkor töröljük az adatbázis beli adatokat
        if ((runtimeConnPools == null || runtimeConnPools.isEmpty()) && (dbConnPools != null && !dbConnPools.isEmpty())) {
            dbConnPools.forEach((dbJdbcConnectionPool) -> {
                log.warn("Server: {} -> már nem létező JDBC ConnectionPool törlése az adatbázisból", server.getSimpleUrl());
                connPoolService.remove(dbJdbcConnectionPool);
            });
            return;
        }

        //Hasem a szerveren, sem az adatbázisban nincs alkalmazás, akkor nem megyünk tovább
        if (runtimeConnPools == null) {
            return;
        }

        //Végigmegyünk a runtime JDBC erőforrások listáján
        runtimeConnPools.forEach((runtimeConnPool) -> {

            boolean needPersistNewEntity = true; //Kell új entitást felvenni?
            boolean existDbConnPoolActiveStatus = false;

            //A poolName alapján kikeressük az adatbázis listából az jdbcPool-t
            if (dbConnPools != null) {
                for (ConnPool dbConnPool : dbConnPools) {
                    //Ha névre megvan, akkor tételesen összehasonlítjuk a két objektumot
                    if (Objects.equals(dbConnPool.getPoolName(), runtimeConnPool.getPoolName())) { //név egyezik?

                        //Ha tételesen már NEM egyezik a két objektum, akkor az adatbázisbelit töröljük, de az 'active' státuszát megőrizzük!
                        //A monitoring státusz nem része az @EquaslAndhashCode()-nak!
                        if (Objects.equals(dbConnPool, runtimeConnPool)) {

                            //Elmentjük a státuszt
                            existDbConnPoolActiveStatus = dbConnPool.getActive();

                            //Beállítjuk, hogy kell menteni az új entitást
                            needPersistNewEntity = true;

                            //töröljük az adatbázisból!
                            log.info("Server: {} -> a(z) '{}' JDBC ConnectionPool törlése az adatbázisból", server.getSimpleUrl(), dbConnPool.getPoolName());
                            connPoolService.remove(dbConnPool);

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
                runtimeConnPool.setActive(existDbConnPoolActiveStatus); //beállítjuk a korábbi monitoring státuszt
                connPoolService.save(runtimeConnPool, DB_MODIFICATOR_USER);
                log.info("Server: {} -> a(z) '{}' új JDBC ConnectionPool felvétele az adatbázisba", server.getSimpleUrl(), runtimeConnPool.getPoolName());

                //CDU összerendelést is elvégezzük
                connPoolService.assignConnPoolToCduIntoDb(runtimeConnPool, DB_MODIFICATOR_USER);
            }
        });
    }

    /**
     * Alkalmazások listájának felépítése, ha szükséges
     */
    public void manageAllActiverServerConnPools() {

        //Végigmegyünk az összes szerveren
        serverService.findAllActiveServer().stream()
                .filter((server) -> super.acquireSessionToken(server)) // ha nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                .forEachOrdered((server) -> {
                    this.maintenanceConnPoolsInDataBase(server);
                });
    }

    /**
     * Adott szerver resources összegyűjtése
     * Adatbázidban nem frissít
     *
     * @param server kiválasztott szerver
     *
     * @return resources listája vagy null
     */
    public List<ConnPool> getConnPools(Server server) {

        //Runime lekérjük a szervertől az alkalmazások listáját
        List<ConnPool> connPools = connPoolDiscoverer.disover(server);

        return connPools;
    }

    /**
     * Kell a CDU-kat gyűjteni?
     *
     * @return true -> igen
     */
    private boolean wantCollectCDU() {

        //Adatnevek táblájának felépítése, ha szükséges
        if ("runtime".equalsIgnoreCase(propertiesConfig.getConfig().getString(PropertiesConfig.STARTUP_JPA_CDU_BUILD_MODE))) {
            if (connPoolCollectorDataUnitService.count() < 1) {
                log.info("A JDBC ConnectionPool 'adatnevek' táblájának feltöltése az első mérése alatt szükséges!");
                return true;
            }
        }
        return false;
    }

    /**
     * JDBC erőforrások monitorozása
     */
    @Override
    public void startMonitoring() {

        long start = Elapsed.nowNano();

        //Hibára futott mérési oldalak, automatikusan tiltjuk őket
        //Itt FULL URL-eket kapunk vissza
        Set<String> fullUrlErroredPaths = new HashSet<>();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            fullUrlErroredPaths.clear();

            //Kell gyűjteni a mértékegységeket?
            Set<DataUnitDto> dataUnits = null;
            if (this.wantCollectCDU()) {
                dataUnits = new LinkedHashSet<>();
            }

            Set<ConnPoolStat> connPoolStats = connPoolSnapshotProvider.fetchSnapshot(server, dataUnits, fullUrlErroredPaths);

            //Kellett gyűjteni a mértékegységeket?
            if (dataUnits != null && !dataUnits.isEmpty()) {
                //Elmentjük a CDU-kat az adatbázisba
                connPoolCollectorDataUnitService.saveCollectedDataUnits(dataUnits, DB_MODIFICATOR_USER);

                //ConnPool <-> Cdu összerendelés
                server.getConnPools().forEach((connPool) -> {
                    connPoolService.assignConnPoolToCduIntoDb(connPool, DB_MODIFICATOR_USER);
                });
            }

            //letiltjuk az alkalmazás gyűjtendő adat path-ját, ha nem sikerült elérni
            if (!fullUrlErroredPaths.isEmpty()) {

                String serverPathBegin = server.getUrl();

                for (String fullUrlErroredPath : fullUrlErroredPaths) {

                    //A Full URL-t lecseréljük
                    String erroredPath = fullUrlErroredPath.replace(serverPathBegin, "");

                    //Megkeressük a CDU-ban, és letiltjuk
                    for (ConnPool connPool : server.getConnPools()) {
                        connPool.getJoiners().stream()
                                .filter((joiner) -> (Objects.equals(joiner.getConnPoolCollDataUnit().getRestPathMask(), erroredPath)))
                                .map((joiner) -> {
                                    joiner.setActive(false);
                                    joiner.setAdditionalMessage("A path nem érhető el, az adatgyűjtés letiltva");
                                    return joiner;
                                }).forEachOrdered((joiner) -> {
                            connPoolService.save(connPool, DB_MODIFICATOR_USER);
                        });

                    }
                }
            }

            measuredServerCnt++;
            if (connPoolStats == null || connPoolStats.isEmpty()) {
                log.warn("Szerver: {} -> Nincsenek menthető JDBC erőforrás pillanatfelvételek!", server.getSimpleUrl());
                return;
            }

            //JPA mentés
            connPoolStats.stream()
                    //.parallel()  nem jó ötlet a paralel -> lock hiba lesz tőle
                    .map((snapshot) -> {
                        //lementjük az adatbázisba
                        jdbcResourcesSnapshotService.save(snapshot, DB_MODIFICATOR_USER);
                        return snapshot;
                    }).forEachOrdered((snapshot) -> {
                //log.trace("Application Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
            jdbcResourcesSnapshotService.flush();
            log.trace("Connection Pool Stat: server url: {}, JDBC Connection Pool snapshots: {}, elapsed: {}", server.getUrl(), connPoolStats.size(), Elapsed.getElapsedNanoStr(start));
        }

        log.trace("Connection Pool Stat összesen: szerver: {}db, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));

    }

    /**
     * Napi pucolás
     */
    @Override
    public void dailyJob() {
        long start = Elapsed.nowNano();

        //JDBC erőforrások lekérdezése és felépítése
        this.manageAllActiverServerConnPools();
        log.info("JDBC erőforrások automatikus karbantartása OK, elapsed: {}", Elapsed.getElapsedNanoStr(start));

        //Megőrzendő napok száma
        start = Elapsed.nowNano();
        Integer keepDays = configService.getInteger(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        log.info("JDBC erőforrások mérési adatok pucolása indul, keepDays: {}", keepDays);

        //Összes régi rekord törlése
        int deletedRecords = jdbcResourcesSnapshotService.deleteOldRecords(keepDays);
        log.info("JDBC erőforrások mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }

}
