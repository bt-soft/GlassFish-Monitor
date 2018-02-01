/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPoolSnapshotProvider.java
 *  Created: 2018.01.21. 10:52:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnectionPoolAppStatistic;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnectionPoolStatistic;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.jdbcconpool.JdbcConnectionPoolAppCollector;
import hu.btsoft.gfmon.engine.monitor.collector.jdbcconpool.JdbcConnectionPoolCollector;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy szerver összes alkalmazásainak monitoradat begyűjtését végző CDI bean
 *
 * @author BT
 */
@Slf4j
public class JdbcConnectionPoolSnapshotProvider {

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JdbcConnectionPoolCollector jdbcConnectionPoolCollector;

    @Inject
    private JdbcConnectionPoolAppCollector jdbcConnectionPooApplCollector;

    @Inject
    private JSonEntityToSnapshotEntityMapper jSonEntityToSnapshotEntityMapper;

    private Set<DataUnitDto> collectDataUnits;

    /**
     *
     * @param simpleUrl
     * @param userName
     * @param sessionToken
     * @param poolName
     *
     * @return
     */
    private ConnectionPoolStatistic start(Server server, String poolName) {

        List<Application> applications = server.getApplications();
        String simpleUrl = server.getSimpleUrl();
        String userName = server.getUserName();
        String sessionToken = server.getSessionToken();

        String resourceUri = restDataCollector.getSubUri() + "resources/" + poolName;
        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(simpleUrl, resourceUri, userName, sessionToken);
        List<CollectedValueDto> valuesList = jdbcConnectionPoolCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

        //Ha kell dataUnitokat is gyűjteni
        if (collectDataUnits != null) {
            List<DataUnitDto> dataUnits = jdbcConnectionPoolCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
            if (dataUnits != null && !dataUnits.isEmpty()) {
                collectDataUnits.addAll(dataUnits);
            }
        }

        ConnectionPoolStatistic connectionPoolStatistic = (ConnectionPoolStatistic) jSonEntityToSnapshotEntityMapper.map(valuesList);
        if (connectionPoolStatistic == null) {
            //log.warn("Null az '{}' szerver '{}' JDBC Connection Pool statisztikása!", server.getSimpleUrl(), poolName);
            //Nincs róla statisztika, nem érdekes
            return null;
        }

        //Milyen alkalmazások használják ezt a ConnectionPool-t?
        Map<String, String> applicationsMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        if (applicationsMap != null && !applicationsMap.isEmpty()) {
            for (String appname : applicationsMap.keySet()) {

                //Ha a pool statisztika egy már nem élő alkalmazáshoz tartozik, akkor nem gyűjtjük róla az adatokat
                boolean isLiveApplicationsConnectionPool = false;
                for (Application app : applications) {
                    if (app.getAppRealName().equals(appname)) {
                        isLiveApplicationsConnectionPool = true;
                        break;
                    }
                }
                if (!isLiveApplicationsConnectionPool) {
                    continue;
                }

                String connPoolFullUrl = applicationsMap.get(appname);

                rootJsonObject = restDataCollector.getRootJsonObject(connPoolFullUrl, userName, sessionToken);
                valuesList = jdbcConnectionPooApplCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                //Ha kell dataUnitokat is gyűjteni
                if (collectDataUnits != null) {
                    List<DataUnitDto> dataUnits = jdbcConnectionPooApplCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                    if (dataUnits != null && !dataUnits.isEmpty()) {
                        collectDataUnits.addAll(dataUnits);
                    }
                }

                ConnectionPoolAppStatistic connectionPoolAppStatistic = (ConnectionPoolAppStatistic) jSonEntityToSnapshotEntityMapper.map(valuesList);

                if (connectionPoolAppStatistic != null) {
                    connectionPoolAppStatistic.setAppName(appname); //Az alkalmazás neve
                    connectionPoolAppStatistic.setConnectionPoolStatistic(connectionPoolStatistic); //melyik connectionPool statisztikához tartozik

                    //Hozzáadjuk a Connectionpool statisztikáhozs az connectionPoolAppStatistic statisztikát is
                    if (connectionPoolStatistic.getConnectionPoolAppStatistic() == null) {
                        connectionPoolStatistic.setConnectionPoolAppStatistic(new LinkedList<>());
                    }
                    connectionPoolStatistic.getConnectionPoolAppStatistic().add(connectionPoolAppStatistic);
                }
            }
        }

        return connectionPoolStatistic;
    }

    /**
     * Az összes JDBC resource kollektor adatait összegyűjti, majd egy új Jdbcresource Snapshot entitásba rakja az eredményeket
     *
     * @param server    a monitorozandó Server entitása
     * @param dataUnits ha nem null, akkor ebbe kell gyűjteni az adtneveket
     *
     * @return JDBC resource Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<ConnectionPoolStatistic> fetchSnapshot(Server server, Set<DataUnitDto> dataUnits) {

        this.collectDataUnits = dataUnits;

        long start = Elapsed.nowNano();

        Set<ConnectionPoolStatistic> snapshots = null;

        //Véégigmegyünk a szerver alkalmazásain
        for (JdbcConnectionPool jdbcConnectionPool : server.getJdbcConnectionPools()) {

            //Ha monitorozásra aktív, akkor meghívjuk rá az adatgyűjtőt
            if (jdbcConnectionPool.getActive() != null && Objects.equals(jdbcConnectionPool.getActive(), Boolean.TRUE)) {

                ConnectionPoolStatistic connectionPoolStatisticSnapshot = this.start(server, jdbcConnectionPool.getPoolName());

                if (connectionPoolStatisticSnapshot == null) {
                    continue;
                }

                //Beállítjuk, hog ymelyik conectionPool-hoz tartozik a mérés
                connectionPoolStatisticSnapshot.setJdbcConnectionPool(jdbcConnectionPool);

                //Ha a connectionPool->ConnectionPoolAppStatistic nem null, akkor megkeresük, és beállítjuk az Application <-> ConnectionPoolAppStatistic relációkat is
                if (connectionPoolStatisticSnapshot.getConnectionPoolAppStatistic() != null && !connectionPoolStatisticSnapshot.getConnectionPoolAppStatistic().isEmpty()) {
                    for (ConnectionPoolAppStatistic conAppStat : connectionPoolStatisticSnapshot.getConnectionPoolAppStatistic()) {

                        server.getApplications().stream()
                                .filter((app) -> (Objects.equals(app.getAppRealName(), conAppStat.getAppName()))) //Kikeressük a  névre azonosságot
                                .map((app) -> {
                                    if (app.getConnectionPoolAppStatistics() == null) {
                                        app.setConnectionPoolAppStatistics(new LinkedList<>());
                                    }
                                    app.getConnectionPoolAppStatistics().add(conAppStat);   //beállítjuk az alkalmazásnak, hogy van ConnectionPool statisztikája

                                    return app;
                                }).forEachOrdered((app) -> {
                            conAppStat.setApplication(app);                                 //beállítjuk a COnnectionPoolApplStatnak is az allamazást
                        });
                    }
                }

                if (snapshots == null) {
                    snapshots = new LinkedHashSet<>();
                }
                snapshots.add(connectionPoolStatisticSnapshot);

            }
        }

        log.info("JDBC ConnectionPool statisztika kigyűjtése elapsed: {}", Elapsed.getElapsedNanoStr(start));
        return snapshots;
    }

}
