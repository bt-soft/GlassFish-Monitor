/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ConnectionQueueCollector.java
 *  Created: 2017.12.25. 15:51:19
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.network;

import hu.btsoft.gfmon.engine.measure.collector.CollectorBase;

/**
 *
 * @author BT
 */
public class ConnectionQueueCollector extends CollectorBase {

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return uri
     */
    @Override
    protected String getUri() {
        return "network/connection-queue";
    }

    /**
     * Az adatgyűjtó melyik modul kategóriát figyeli?
     * Ezzel azt vizsgáljuk, hogy az adott adatgyűjtőt kell-e futtatni egyáltalán. Ha a GF példányban a modul monitorozása nincs engedélyezve, akkor
     * nem is futtatjuk rá az adatgyűjtést
     *
     * @return modul neve
     */
    @Override
    public String getMonitoringServiceModuleName() {
        return "network";
    }
}
