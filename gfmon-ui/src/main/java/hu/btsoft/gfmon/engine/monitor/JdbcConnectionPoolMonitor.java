/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPoolMonitor.java
 *  Created: 2018.01.28. 10:44:19
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPoolCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnectionPoolStatistic;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.JdbcConnectionPoolCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.JdbcConnectionPoolService;
import hu.btsoft.gfmon.engine.model.service.JdbcResourcesSnapshotService;
import hu.btsoft.gfmon.engine.monitor.management.JdbcConnectionPoolDiscoverer;
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
public class JdbcConnectionPoolMonitor extends MonitorsBase {

    private static final String DB_MODIFICATOR_USER = "jdbc-mon-ctrl";

    @Inject
    private JdbcConnectionPoolDiscoverer jdbcConnectionPoolDiscoverer;

    @EJB
    private JdbcConnectionPoolService jdbcConnectionPoolService;

    @EJB
    private JdbcResourcesSnapshotService jdbcResourcesSnapshotService;

    @Inject
    private JdbcConnectionPoolSnapshotProvider jdbcConnectionPoolSnapshotProvider;

    @EJB
    private JdbcConnectionPoolCollectorDataUnitService jdbcConnectionPoolCollectorDataUnitService;

    @Override
    protected String getDbModificationUser() {
        return DB_MODIFICATOR_USER;
    }

    @Override
    protected String getControllerName() {
        return "Res-Monitor";
    }

    @Override
    public void beforeStartTimer() {
        //Alkalmazások lekérdezése és felépítése
        this.manageAllActiverServerJdbcResources();
    }

    /**
     * Egy szerver JDBC erőforrásainak feltérképezése !!!! ÉS az adatbázisban történő frissítése !!!
     * <p>
     * - megnézi, hogy az jdbc resources szerepel-e az adatbázisban
     *
     * @param server vizsgálandó szerver
     */
    public void maintenanceServerJdbcResourcesInDataBase(Server server) {

        //A szerver aktuális alkalmazás listája
        List<JdbcConnectionPool> runtimeJdbcConnectionPools = this.getJdbcConnectionPoolsList(server);

        //A szerver eltárolt alkalmazás listája
        List<JdbcConnectionPool> dbJdbcConnectionPools = jdbcConnectionPoolService.findByServer(server.getId());

        //Ha a szerveren nincs alkalmazás de az adatbázisban mégis van, akkor töröljük az adatbázis beli adatokat
        if ((runtimeJdbcConnectionPools == null || runtimeJdbcConnectionPools.isEmpty()) && (dbJdbcConnectionPools != null && !dbJdbcConnectionPools.isEmpty())) {
            dbJdbcConnectionPools.forEach((dbJdbcConnectionPool) -> {
                log.warn("Server: {} -> már nem létező JDBC ConnectionPool törlése az adatbázisból", server.getSimpleUrl());
                jdbcConnectionPoolService.remove(dbJdbcConnectionPool);
            });
            return;
        }

        //Hasem a szerveren, sem az adatbázisban nincs alkalmazás, akkor nem megyünk tovább
        if (runtimeJdbcConnectionPools == null) {
            return;
        }

        //Végigmegyünk a runtime JDBC erőforrások listáján
        runtimeJdbcConnectionPools.forEach((runtimeJdbcConnectionPool) -> {

            boolean needPersistNewEntity = true; //Kell új entitást felvenni?
            boolean existDbJdbcConnectionPoolActiveStatus = false;

            //A poolName alapján kikeressük az adatbázis listából az jdbcPool-t
            if (dbJdbcConnectionPools != null) {
                for (JdbcConnectionPool dbJdbcConnectionPool : dbJdbcConnectionPools) {
                    //Ha névre megvan, akkor tételesen összehasonlítjuk a két objektumot
                    if (Objects.equals(dbJdbcConnectionPool.getPoolName(), runtimeJdbcConnectionPool.getPoolName())) { //név egyezik?

                        //Ha tételesen már NEM egyezik a két objektum, akkor az adatbázisbelit töröljük, de az 'active' státuszát megőrizzük!
                        //A monitoring státusz nem része az @EquaslAndhashCode()-nak!
                        if (Objects.equals(dbJdbcConnectionPool, runtimeJdbcConnectionPool)) {

                            //Elmentjük a státuszt
                            existDbJdbcConnectionPoolActiveStatus = dbJdbcConnectionPool.getActive();

                            //Beállítjuk, hogy kell menteni az új entitást
                            needPersistNewEntity = true;

                            //töröljük az adatbázisból!
                            log.info("Server: {} -> a(z) '{}' JDBC ConnectionPool törlése az adatbázisból", server.getSimpleUrl(), dbJdbcConnectionPool.getPoolName());
                            jdbcConnectionPoolService.remove(dbJdbcConnectionPool);

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
                runtimeJdbcConnectionPool.setActive(existDbJdbcConnectionPoolActiveStatus); //beállítjuk a korábbi monitoring státuszt
                jdbcConnectionPoolService.save(runtimeJdbcConnectionPool, DB_MODIFICATOR_USER);
                log.info("Server: {} -> a(z) '{}' új JDBC ConnectionPool felvétele az adatbázisba", server.getSimpleUrl(), runtimeJdbcConnectionPool.getPoolName());
            }
        });
    }

    /**
     * Alkalmazások listájának felépítése, ha szükséges
     */
    public void manageAllActiverServerJdbcResources() {

        //Végigmegyünk az összes szerveren
        serverService.findAllActiveServer().stream()
                .filter((server) -> super.acquireSessionToken(server)) // ha nem sikerült bejelentkezni -> letiltjuk és jöhet a következő szerver
                .forEachOrdered((server) -> {
                    this.maintenanceServerJdbcResourcesInDataBase(server);
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
    public List<JdbcConnectionPool> getJdbcConnectionPoolsList(Server server) {

        //Runime lekérjük a szervertől az alkalmazások listáját
        List<JdbcConnectionPool> jdbcResources = jdbcConnectionPoolDiscoverer.getJdbcresourcess(server);

        return jdbcResources;
    }

    /**
     * Összegyűjtött adatnevek mentése
     *
     * @param dataUnits összegyűjtött adatnevek halmaza
     */
    private void processCollectedDataUnits(Set<DataUnitDto> dataUnits) {

        log.info("JDBC ConnectionPool monitor adatnevek táblájának felépítése indul");
        long start = Elapsed.nowNano();

        //Végigmegyünk az összes adatneven és jól beírjuk az adatbázisba őket
        dataUnits.stream()
                .map((dto) -> new JdbcConnectionPoolCollectorDataUnit(dto.getRestPath(), dto.getEntityName(), dto.getDataName(), dto.getUnit(), dto.getDescription()))
                .forEachOrdered((cdu) -> {
                    jdbcConnectionPoolCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
                });

        log.info("JDBC ConnectionPool monitor adatnevek felépítése OK, adatnevek: {}db, elapsed: {}", dataUnits.size(), Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Kell a CDU-kat gyűjteni?
     *
     * @return true -> igen
     */
    private boolean wantCollectCDU() {

        if (jdbcConnectionPoolCollectorDataUnitService.count() < 1) {
            log.info("A JDBC ConnectionPool 'adatnevek' táblájának felépítése szükséges!");
            return true;
        }
        return false;
    }

    /**
     * JDBC erőforrások monitorozása
     */
    @Override
    public void startMonitoring() {

        long start = Elapsed.nowNano();

        int measuredServerCnt = 0;
        for (Server server : serverService.findAllActiveServer()) {

            //Kell gyűjteni a mértékegységeket?
            Set<DataUnitDto> dataUnits = null;
            if (this.wantCollectCDU()) {
                dataUnits = new LinkedHashSet<>();
            }

            Set<ConnectionPoolStatistic> jdbcConnectionPoolSnashots = jdbcConnectionPoolSnapshotProvider.fetchSnapshot(server, dataUnits);
            measuredServerCnt++;

            //Kellett gyűjteni a mértékegységeket?
            if (dataUnits != null && !dataUnits.isEmpty()) {
                this.processCollectedDataUnits(dataUnits);
            }

            if (jdbcConnectionPoolSnashots == null || jdbcConnectionPoolSnashots.isEmpty()) {
                log.warn("Szerver: {} -> Nincsenek menthető JDBC erőforrás pillanatfelvételek!", server.getSimpleUrl());
                return;
            }

            //JPA mentés
            jdbcConnectionPoolSnashots.stream()
                    //.parallel()  nem jó ötlet a paralel -> lock hiba lesz tőle
                    .map((snapshot) -> {
                        //lementjük az adatbázisba
                        jdbcResourcesSnapshotService.save(snapshot);
                        return snapshot;
                    }).forEachOrdered((snapshot) -> {
                //log.trace("Application Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
            jdbcResourcesSnapshotService.flush();
            log.trace("server url: {}, JDBC snapshots: {}, elapsed: {}", server.getUrl(), jdbcConnectionPoolSnashots.size(), Elapsed.getElapsedNanoStr(start));
        }

        log.trace("JDBC erőforrás adatok kigyűjtve {} db szerverre, elapsed: {}", measuredServerCnt, Elapsed.getElapsedNanoStr(start));

    }

    /**
     * Napi pucolás
     */
    @Override
    public void dailyJob() {
        long start = Elapsed.nowNano();

        //JDBC erőforrások lekérdezése és felépítése
        this.manageAllActiverServerJdbcResources();
        log.info("JDBC erőforrások automatikus karbantartása OK, elapsed: {}", Elapsed.getElapsedNanoStr(start));

        //Megőrzendő napok száma
        start = Elapsed.nowNano();
        Integer keepDays = configService.getInteger(IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        log.info("JDBC erőforrások mérési adatok pucolása indul, keepDays: {}", keepDays);
        //Összes régi rekord törlése
        int deletedRecords = jdbcResourcesSnapshotService.deleteOldRecords(keepDays);
        log.info("JDBC erőforrások mérési adatok pucolása OK, törölt rekord: {}, elapsed: {}", deletedRecords, Elapsed.getElapsedNanoStr(start));
    }

}
