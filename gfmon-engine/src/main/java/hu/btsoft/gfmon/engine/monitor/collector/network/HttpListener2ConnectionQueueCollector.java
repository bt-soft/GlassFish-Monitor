/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener2ConnectionQueueCollector.java
 *  Created: 2017.12.26. 11:17:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.network;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * HTTP Listener (network/http-listener-1) connection-queue adatok gyűjtése
 *
 * @author BT
 */
public class HttpListener2ConnectionQueueCollector extends CollectorBase {

    public static final String URI = "network/http-listener-2/connection-queue";

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
