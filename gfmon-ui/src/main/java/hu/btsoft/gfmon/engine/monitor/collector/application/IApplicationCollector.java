/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IApplicationCollector.java
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
public interface IApplicationCollector extends ICollectorBase {

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param fullUrl           teljes URL
     * @param sessionToken      GF session token
     * @param uriParams         maszk paraméterek
     *
     * @return application új entitás snapshotok listája
     */
    List<CollectedValueDto> execute(RestDataCollector restDataCollector, String fullUrl, String sessionToken, Map<String, String> uriParams);

}
