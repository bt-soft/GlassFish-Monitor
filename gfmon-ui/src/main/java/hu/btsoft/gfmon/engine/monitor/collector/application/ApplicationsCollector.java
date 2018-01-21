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

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

/**
 *
 * @author BT
 */
public class ApplicationsCollector extends CollectorBase {

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private Instance<IAppServerCollector> appServerCollectors;

    @Override
    public String getPath() {
        return null;
    }

    /**
     *
     * @param server
     * @param appRealName
     *
     * @return
     */
    private List<CollectedValueDto> startCollectors(String simpleUrl, String sessionToken, String appRealName) {

        String appPath = String.format("/applications/%s", appRealName);
        Response response = restDataCollector.getMonitorResponse(appPath, simpleUrl, sessionToken);
        Set<String> childResourcesKeys = restDataCollector.getChildResourcesKeys(response);

        //Van "server" child?
        List<CollectedValueDto> result = null;

        if (childResourcesKeys != null && childResourcesKeys.contains("server")) {
            result = appServerCollectors.get().execute(restDataCollector, simpleUrl, appRealName, sessionToken);
        }

        return result;
    }

    /**
     *
     * @param simpleUrl
     * @param sessionToken
     * @param collectedAppRealNames
     *
     * @return
     */
    public List<CollectedValueDto> execute(String simpleUrl, String sessionToken, List<String> collectedAppRealNames) {

        List<CollectedValueDto> result = null;

        //Végigmegyünk a szerver összes alkalmazásán
        for (String appRealName : collectedAppRealNames) {

            List<CollectedValueDto> snapshots = this.startCollectors(simpleUrl, sessionToken, appRealName);
            if (snapshots != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(snapshots);
            }
        }

        return result;
    }

}
