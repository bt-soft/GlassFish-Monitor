/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener1KeepAliveCollector.java
 *  Created: 2017.12.26. 11:17:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server.network;

import hu.btsoft.gfmon.engine.monitor.collector.ServerCollectorBase;

/**
 * HTTP Listener (network/http-listener-1) keep-alive adatok gyűjtése
 *
 * @author BT
 */
public class HttpListener1KeepAliveCollector extends ServerCollectorBase {

    public static final String PATH = "network/http-listener-1/keep-alive";

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
