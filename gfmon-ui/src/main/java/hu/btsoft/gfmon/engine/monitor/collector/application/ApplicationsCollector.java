/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationsCollector.java
 *  Created: 2018.01.21. 13:32:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application;

import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.monitor.JSonEntityToApplicationSnapshotEntityMapper;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.server.AppServerCollector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * CollectorBase leszármazottja, hogy tudjon REST hívásoka küldözgetni a szerver felé,
 * hogy meg tudja állapítani, hogy milyen kollektort is kellene/lehet indítani
 *
 * @author BT
 */
@Slf4j
public class ApplicationsCollector {

    public static final String APP_SERVER_TOKENIZED_PATH = "/applications/{appRealName}/server";
    public static final String APP_SERVER_CHILDRESOURCES_TOKENIZED_PATH = "/applications/{appRealName}/server/{childResourcesPath}";

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JSonEntityToApplicationSnapshotEntityMapper jSonEntityToApplicationSnapshotEntityMapper;

    @Inject
    private Instance<AppServerCollector> appServerCollectors;

    private String appRealName;

    /**
     * Egy allamzás adatainak összegyűjtése
     *
     * @param server
     * @param appRealName
     *
     * @return
     */
    private Set<AppSnapshotBase> startCollectors(String simpleUrl, String sessionToken) {

        //Lekérdezzük az alkalmazás 'childResources'-ét
        String resourceUri = String.format("/applications/%s", appRealName);
        Response response = restDataCollector.getMonitorResponse(resourceUri, simpleUrl, sessionToken);
        Set<String> childResourcesKeys = restDataCollector.getChildResourcesKeys(response);

        if (childResourcesKeys == null) {
            return null;
        }

        Set<AppSnapshotBase> snapshots = null;

        List<CollectedValueDto> valuesList = null;

        Map<String, String> uriParams = new HashMap<>();

        for (String key : childResourcesKeys) {

            if ("server".equals(key)) {
                //Server path cuccok
                uriParams.clear();
                uriParams.put("{appRealName}", appRealName);
                valuesList = appServerCollectors
                        .get()
                        .execute(restDataCollector, simpleUrl, sessionToken, APP_SERVER_TOKENIZED_PATH, uriParams);

                //Megnézzük, hogy vannak-e gyermek objektumok, és jól lekérdezzük őket
                resourceUri = String.format("/applications/%s/server", appRealName);
                response = restDataCollector.getMonitorResponse(resourceUri, simpleUrl, sessionToken);
                childResourcesKeys = restDataCollector.getChildResourcesKeys(response);
                if (childResourcesKeys != null && !childResourcesKeys.isEmpty()) {
                    for (String childResourcesPath : childResourcesKeys) {
                        uriParams.clear();
                        uriParams.put("{appRealName}", appRealName);
                        uriParams.put("{childResourcesPath}", childResourcesPath);
                        valuesList.addAll(appServerCollectors
                                .get()
                                .execute(restDataCollector, simpleUrl, sessionToken, APP_SERVER_CHILDRESOURCES_TOKENIZED_PATH, uriParams));
                    }
                }
            } else {
                //EJB cuccok

            }

            //Üres a mért eredmények Map-je
            if (valuesList == null || valuesList.isEmpty()) {
                log.warn("A(z) '{}' szerver '{}' alkalmazásának '{}' mérési eredményei üresek!", simpleUrl, appRealName, key);
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            if (snapshots == null) {
                snapshots = new HashSet<>();
            }

            jSonEntityToApplicationSnapshotEntityMapper.map(valuesList, snapshots);
        }

        return snapshots;
    }

    /**
     * Egy szerver egy alkalmazásadatainak a kigyűjtése
     *
     * @param simpleUrl    szevrer url
     * @param sessionToken session token
     * @param appRealName  akalmazás igazi neve
     *
     * @return kigyűjtött adatok
     */
    public Set<AppSnapshotBase> start(String simpleUrl, String sessionToken, String appRealName) {

        this.appRealName = appRealName;

        Set<AppSnapshotBase> snapshots = this.startCollectors(simpleUrl, sessionToken);
        return snapshots;
    }

}
