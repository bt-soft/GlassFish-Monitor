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
package hu.btsoft.gfmon.engine.monitor.collector.network;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 *
 * @author BT
 */
public class ConnectionQueueCollector extends CollectorBase {

    public static final String URI = "network/connection-queue";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return uri
     */
    @Override
    protected String getUri() {
        return URI;
    }
}
