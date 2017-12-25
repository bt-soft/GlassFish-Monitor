/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RequestCollector.java
 *  Created: 2017.12.24. 17:21:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.httpservice;

import hu.btsoft.gfmon.engine.measure.collector.CollectorBase;

/**
 * JVM/Memory adatok gyűjtése
 *
 * @author BT
 */
public class RequestCollector extends CollectorBase {

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     * pl.: "server/http-service/server/request"
     *
     * @return uri
     */
    @Override
    protected String getUri() {
        return "server/http-service/server/request";
    }

    /**
     * Az adatgyűjtó melyik modul kategóriát figyeli?
     * Ezzel azt vizsgáljuk, hogy az adott adatgyűjtőt kell-e futtatni egyáltalán. Ha a GF példányban a modul monitorozása nincs engedélyezve, akkor
     * nem is futtatjuk rá az adatgyűjtést
     *
     * http://localhost:4848/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels
     *
     * @return modul neve
     */
    @Override
    public String getMonitoringServiceModuleName() {
        return "http-service";
    }
}
