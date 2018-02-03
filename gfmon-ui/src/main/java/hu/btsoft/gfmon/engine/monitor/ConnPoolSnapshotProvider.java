/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolSnapshotProvider.java
 *  Created: 2018.01.21. 10:52:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.jdbc.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnPoolAppStat;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnPoolStat;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.connpool.ConnPoolAppCollector;
import hu.btsoft.gfmon.engine.monitor.collector.connpool.ConnPoolCollector;
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
public class ConnPoolSnapshotProvider {

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private ConnPoolCollector connPoolCollector;

    @Inject
    private ConnPoolAppCollector connPoolAppCollector;

    @Inject
    private JSonEntityToSnapshotEntityMapper jSonEntityToSnapshotEntityMapper;

    private Set<DataUnitDto> collectDataUnits;

    /**
     * Gyűjtendő path-ok és az azalatti adatnevek
     */
    private Map<String/*path*/, Set<String> /*dataNames*/> collectedDatatNamesMap;

    /**
     * Az adatgyűjtés közben hibára futott path-ek, automatikusan tiltjuk őket
     */
    private Set<String> fullUrlErroredPaths;

    /**
     *
     * @param simpleUrl
     * @param userName
     * @param sessionToken
     * @param poolName
     *
     * @return
     */
    private ConnPoolStat start(Server server, String poolName) {

        List<Application> applications = server.getApplications();
        String protocol = server.getProtocol();
        String simpleUrl = server.getSimpleUrl();
        String userName = server.getUserName();
        String sessionToken = server.getSessionToken();

        String resourceUri = restDataCollector.getSubUri() + "resources/" + poolName;
        String fullUrl = protocol + simpleUrl + resourceUri;

        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(fullUrl, userName, sessionToken, fullUrlErroredPaths);

        List<CollectedValueDto> valuesList = connPoolCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

        //Ha kell dataUnitokat is gyűjteni
        if (collectDataUnits != null) {
            List<DataUnitDto> dataUnits = connPoolCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
            if (dataUnits != null && !dataUnits.isEmpty()) {
                collectDataUnits.addAll(dataUnits);
            }
        }

        ConnPoolStat connectionPoolStatistic = (ConnPoolStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
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

                rootJsonObject = restDataCollector.getRootJsonObject(connPoolFullUrl, userName, sessionToken, fullUrlErroredPaths);
                valuesList = connPoolAppCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                //Ha kell dataUnitokat is gyűjteni
                if (collectDataUnits != null) {
                    List<DataUnitDto> dataUnits = connPoolAppCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                    if (dataUnits != null && !dataUnits.isEmpty()) {
                        collectDataUnits.addAll(dataUnits);
                    }
                }

                ConnPoolAppStat connPoolAppStat = (ConnPoolAppStat) jSonEntityToSnapshotEntityMapper.map(valuesList);

                if (connPoolAppStat != null) {
                    connPoolAppStat.setAppName(appname); //Az alkalmazás neve
                    connPoolAppStat.setConnPoolStat(connectionPoolStatistic); //melyik connectionPool statisztikához tartozik

                    //Hozzáadjuk a Connectionpool statisztikáhozs az connectionPoolAppStatistic statisztikát is
                    if (connectionPoolStatistic.getConnPoolAppStats() == null) {
                        connectionPoolStatistic.setConnPoolAppStats(new LinkedList<>());
                    }
                    connectionPoolStatistic.getConnPoolAppStats().add(connPoolAppStat);
                }
            }
        }

        return connectionPoolStatistic;
    }

    /**
     * Az összes JDBC resource kollektor adatait összegyűjti, majd egy új Jdbcresource Snapshot entitásba rakja az eredményeket
     *
     * @param server              a monitorozandó Server entitása
     * @param collectDataUnits    ha nem null, akkor ebbe kell gyűjteni az adtneveket
     * @param fullUrlErroredPaths a mérés közben hibára futott oldalak, automatikusan letiltjuk őket
     *
     * @return JDBC resource Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<ConnPoolStat> fetchSnapshot(Server server, Set<DataUnitDto> collectDataUnits, Set<String> fullUrlErroredPaths) {

        long start = Elapsed.nowNano();

        this.collectDataUnits = collectDataUnits;
        this.fullUrlErroredPaths = fullUrlErroredPaths;

        Set<ConnPoolStat> snapshots = null;

        //Véégigmegyünk a szerver alkalmazásain
        for (ConnPool connPool : server.getConnPools()) {

            //Ha monitorozásra aktív, akkor meghívjuk rá az adatgyűjtőt
            if (connPool.getActive() != null && Objects.equals(connPool.getActive(), Boolean.TRUE)) {

                ConnPoolStat connPoolStat = this.start(server, connPool.getPoolName());

                if (connPoolStat == null) {
                    continue;
                }

                //Beállítjuk, hog ymelyik conectionPool-hoz tartozik a mérés
                connPoolStat.setConnPool(connPool);

                //Ha a connectionPool->ConnPoolAppStat nem null, akkor megkeresük, és beállítjuk az Application <-> ConnPoolAppStat relációkat is
                if (connPoolStat.getConnPoolAppStats() != null && !connPoolStat.getConnPoolAppStats().isEmpty()) {
                    for (ConnPoolAppStat conAppStat : connPoolStat.getConnPoolAppStats()) {

                        server.getApplications().stream()
                                .filter((app) -> (Objects.equals(app.getAppRealName(), conAppStat.getAppName()))) //Kikeressük a  névre azonosságot
                                .map((app) -> {
//
// TODO: Ezt nem szabad beállítani, mert nem lehet menteni a ConnectionPool CDU-kat, ha magunk gyűjtjük össze
// Majd fixálni!!!!
//
//                                    if (app.getConnPoolAppStats() == null) {
//                                        app.setConnPoolAppStats(new LinkedList<>());
//                                    }
//                                    app.getConnPoolAppStats().add(conAppStat);   //beállítjuk az alkalmazásnak, hogy van ConnectionPool statisztikája

                                    return app;
                                }).forEachOrdered((app) -> {
                            conAppStat.setApplication(app);                                 //beállítjuk a COnnectionPoolApplStatnak is az allamazást
                        });
                    }
                }

                if (snapshots == null) {
                    snapshots = new LinkedHashSet<>();
                }
                snapshots.add(connPoolStat);

            }
        }

        log.info("JDBC ConnectionPool statisztika kigyűjtése elapsed: {}", Elapsed.getElapsedNanoStr(start));
        return snapshots;
    }

}
