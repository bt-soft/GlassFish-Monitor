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
package hu.btsoft.gfmon.engine.monitor.collector.server.network;

import hu.btsoft.gfmon.engine.monitor.collector.server.ServerCollectorBase;

/**
 * Network Connection Queue adatok gyűjtése
 *
 * @author BT
 */
public class ConnectionQueueCollector extends ServerCollectorBase {

    public static final String PATH = "network/connection-queue";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return path
     */
    @Override
    public String getPath() {
        return PATH;
    }
}
