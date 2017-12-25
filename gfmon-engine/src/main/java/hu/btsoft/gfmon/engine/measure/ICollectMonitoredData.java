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
package hu.btsoft.gfmon.engine.measure;

import hu.btsoft.gfmon.engine.measure.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import java.util.HashMap;

/**
 * GF REST adatgyűjtés funkcionális interfész
 *
 * @author BT
 */
public interface ICollectMonitoredData {

    /**
     * Az adatgyűjtó melyik MonitordServices modul kategóriát figyeli?
     * Ezzel azt vizsgáljuk, hogy az adott adatgyűjtőt kell-e futtatni egyáltalán. Ha a GF példányban a modul monitorozása nincs engedélyezve, akkor
     * nem is futtatjuk rá az adatgyűjtést
     *
     * http://localhost:4848/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels
     *
     * @return modul neve
     */
    String getMonitoringServiceModuleName();

    /**
     * Adatgyűjtés végrahajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param sessionToken      GF session token
     *
     * @return adatgyűjtés eredménye (JSon entitás - értékek Map)
     */
    HashMap<String/*Json entityName*/, ValueBaseDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken);

}
