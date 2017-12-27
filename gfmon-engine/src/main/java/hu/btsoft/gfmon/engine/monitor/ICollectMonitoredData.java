/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ICollectMonitoredData.java
 *  Created: 2017.12.25. 11:02:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.monitor.collector.MonitorValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.List;

/**
 * GF REST adatgyűjtés funkcionális interfész
 *
 * @author BT
 */
public interface ICollectMonitoredData {

    /**
     * Adatgyűjtés végrahajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param sessionToken      GF session token
     *
     * @return adatgyűjtés eredménye (JSon entitás - értékek Lista)
     */
    List<MonitorValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken);

}
