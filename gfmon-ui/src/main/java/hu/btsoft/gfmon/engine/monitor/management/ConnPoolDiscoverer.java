/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolDiscoverer.java
 *  Created: 2018.01.28. 9:23:20
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.management;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.corelib.string.StrUtils;
import hu.btsoft.gfmon.engine.model.entity.jdbc.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcResource;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.rest.RestClientBase;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * A szerver JDBC resources adatainak összegyűjtése
 * A) ConnectionPool:
 * 1) Lekérjük a JDBC ConnectionPool listáját: http://localhost:4848/management/domain/resources/jdbc-connection-pool - child resources
 * 2) Lekérjük a ConnectionPool tulajdonságait: http://localhost:4848/management/domain/resources/jdbc-connection-pool/{poolName} - extraproperties/properties
 *
 * B)
 * 1) Lekérjük a JDBC ConnectionPool listáját: http://localhost:4848/management/domain/resources/jdbc-resource - child resources
 * 2) Lekérjük a ConnectionPool tulajdonságait:http://localhost:4848/management/domain/resources/jdbc-resource/{resourcename}
 *
 *
 * @author BT
 */
@Slf4j
public class ConnPoolDiscoverer extends RestClientBase {

    private static final String SUB_URL_CONNECTION_POOL = "/management/domain/resources/jdbc-connection-pool";
    private static final String SUB_URL_JDBC_RESOURCE = "/management/domain/resources/jdbc-resource";

    /**
     * JDBC resources kigyűjtése
     *
     * @param urlMapEntry
     * @param userName
     * @param sessionToken
     *
     * @return
     */
    private JdbcResource createJdbcResource(Map.Entry<String /* JDBC ConnectionPoolName */, String /* url */> urlMapEntry, String userName, String sessionToken) {

        JsonObject rootJsonObject = super.getRootJsonObject(urlMapEntry.getValue(), userName, sessionToken);
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);
        if (entities == null) {
            return null;
        }

        JdbcResource res = new JdbcResource();
        res.setJndiName(StrUtils.deQuote(entities.get("jndiName").toString()));
        String description = StrUtils.deQuote(entities.get("description").toString());
        description = "null".equals(description) ? null : description;
        res.setDescription(description);
        res.setEnabled(Boolean.parseBoolean(StrUtils.deQuote(entities.get("enabled").toString())));

        return res;

    }

    /**
     * JDBC Connection Poool létrehozása
     *
     * @param urlMapEntry  [conpool neve, full URL]
     * @param userName     user név
     * @param sessionToken session token
     *
     * @return új JPA ConnPool példány
     */
    private ConnPool createConnPool(Map.Entry<String /* JDBC ConnectionPoolName */, String /* url */> urlMapEntry, String userName, String sessionToken) {

        JsonObject rootJsonObject = super.getRootJsonObject(urlMapEntry.getValue(), userName, sessionToken);
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);
        if (entities == null) {
            return null;
        }

        ConnPool cp = new ConnPool();

        cp.setPoolName(StrUtils.deNull(StrUtils.deQuote(entities.get("name").toString())));
        cp.setDescription(StrUtils.deNull(StrUtils.deQuote(entities.get("description").toString())));
        cp.setDatasourceClassname(StrUtils.deNull(StrUtils.deQuote(entities.get("datasourceClassname").toString())));
//        cp.setDriverClassname(StrUtils.deNull(StrUtils.deQuote(entities.get("driverClassname").toString())));
        cp.setIdleTimeoutInSeconds(StrUtils.deNull(StrUtils.deQuote(entities.get("idleTimeoutInSeconds").toString())));
        cp.setInitSql(StrUtils.deNull(StrUtils.deQuote(entities.get("initSql").toString())));
        cp.setMaxConnectionUsageCount(StrUtils.deNull(StrUtils.deQuote(entities.get("maxConnectionUsageCount").toString())));
        cp.setMaxPoolSize(StrUtils.deNull(StrUtils.deQuote(entities.get("maxPoolSize").toString())));
        cp.setMaxWaitTimeInMillis(StrUtils.deNull(StrUtils.deQuote(entities.get("maxWaitTimeInMillis").toString())));
        cp.setPoolResizeQuantity(StrUtils.deNull(StrUtils.deQuote(entities.get("poolResizeQuantity").toString())));
        cp.setPooling(Boolean.parseBoolean(StrUtils.deNull(StrUtils.deQuote(entities.get("pooling").toString()))));
        cp.setResType(StrUtils.deNull(StrUtils.deQuote(entities.get("resType").toString())));
        cp.setStatementCacheSize(StrUtils.deNull(StrUtils.deQuote(entities.get("statementCacheSize").toString())));
        cp.setSteadyPoolSize(StrUtils.deNull(StrUtils.deQuote(entities.get("steadyPoolSize").toString())));
        cp.setStatementTimeoutInSeconds(StrUtils.deNull(StrUtils.deQuote(entities.get("statementTimeoutInSeconds").toString())));

        //A Property leszedésével most nem foglalkozunk...
        return cp;

    }

    /**
     * Az adott szerver JDBC erőforrásainak kigyűjtése
     *
     * @param server kiválaszott szerver
     *
     * @return JDBC erőforrások JPA entitás listája
     */
    public List<ConnPool> disover(Server server) {

        String simpleUrl = server.getSimpleUrl();
        String userName = server.getUserName();
        String sessionToken = server.getSessionToken();

        //leszedjük a JDBC ConnectionPool neveinek listáját
        JsonObject rootJsonObject = super.getRootJsonObject(simpleUrl, SUB_URL_CONNECTION_POOL, userName, sessionToken);
        Map<String /* name */, String /* url */> urlMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        if (urlMap == null) {
            return null;
        }

        List<ConnPool> connPools = new LinkedList<>();
        urlMap.entrySet().stream()
                .map((jdbcConnectionPoolUrlMapEntry) -> createConnPool(jdbcConnectionPoolUrlMapEntry, userName, sessionToken))
                .map((jdbcConnectionPool) -> {
                    jdbcConnectionPool.setServer(server); //beállítjuk, hogy a connection pool melyik szerveren van
                    jdbcConnectionPool.setActive(null);  //MÉG nem döntöttünk a monitorozható állapotról!
                    return jdbcConnectionPool;
                }).forEachOrdered((jdbcConnectionPool) -> {
            connPools.add(jdbcConnectionPool);
        });

        //Kigyűjtjük a jdbc-resources-eket
        rootJsonObject = super.getRootJsonObject(simpleUrl, SUB_URL_JDBC_RESOURCE, userName, sessionToken);
        urlMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        for (Map.Entry<String /* name */, String /* url */> mapEntry : urlMap.entrySet()) {

            JdbcResource jdbcResource = createJdbcResource(mapEntry, userName, sessionToken);

            if (jdbcResource != null) {
                //Megkeressük, hogy melyik connectionPool-t használja és beállítjuk mindkét oldalon
                connPools.stream()
                        .filter((cp) -> (cp.getPoolName().equals(jdbcResource.getPoolName()))) //Ugyan az a connection pool neve?
                        .map((cp) -> {
                            jdbcResource.setConnPool(cp); //beállítjuk a resource-nak, a connectionpool-t
                            return cp;
                        }).map((cp) -> {
                    if (cp.getJdbcResources() == null) {
                        cp.setJdbcResources(new LinkedList<>());
                    }
                    return cp;
                }).forEachOrdered((cp) -> {
                    cp.getJdbcResources().add(jdbcResource);  //hozzáadjuk a connectionPool-hoz a resources-t
                });
            }
        }

        return connPools;
    }
}
