/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IAppServerCollector.java
 *  Created: 2017.12.25. 11:02:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.ICollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.List;
import java.util.Map;

/**
 * GF REST Alkalmazás adatgyűjtés interfész
 *
 * @author BT
 */
public interface IAppServerCollector extends ICollectorBase {

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param appRealName       az alkalmazás igazi nevével
     * @param subPath           appRealName-t követő subpath (server, server/jsp, server/Faces Servlet, ...
     * @param sessionToken      GF session token
     *
     * @return application új entitás snapshotok listája
     *
     */
    Map<String, List<CollectedValueDto>> execute(RestDataCollector restDataCollector, String simpleUrl, String appRealName, String subPath, String sessionToken);

}
