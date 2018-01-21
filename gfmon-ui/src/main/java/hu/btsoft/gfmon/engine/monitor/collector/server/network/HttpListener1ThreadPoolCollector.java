/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener1ThreadPoolCollector.java
 *  Created: 2017.12.26. 11:17:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server.network;

import hu.btsoft.gfmon.engine.monitor.collector.server.ServerCollectorBase;

/**
 * HTTP Listener (network/http-listener-1) thread-pool adatok gyűjtése
 *
 * @author BT
 */
public class HttpListener1ThreadPoolCollector extends ServerCollectorBase {

    public static final String PATH = "network/http-listener-1/thread-pool";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return public static final String URI =
     */
    @Override
    public String getPath() {
        return PATH;
    }

}
