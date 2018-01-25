/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IServerCollector.java
 *  Created: 2017.12.25. 11:02:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.ICollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.List;
import java.util.Set;

/**
 * GF REST Szerver adatgyűjtés interfész
 *
 * @author BT
 */
public interface IServerCollector extends ICollectorBase {

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector  REST Data Collector példány
     * @param simpleUrl          A GF szerver url-je
     * @param userName           REST hívás usere
     * @param sessionToken       GF session token
     * @param collectedDataNames kigyűjtendő adatnevek halmaza
     * @param erroredPaths       hibára futott URL-ek halmaza
     *
     * @return adatgyűjtés eredménye (JSon entitás - értékek Lista)
     */
    List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken, Set<String> collectedDataNames, Set<String> erroredPaths);

}
