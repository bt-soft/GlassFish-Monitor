/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener2KeepAliveCollector.java
 *  Created: 2017.12.26. 11:17:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.network;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * HTTP Listener (network/http-listener-1) keep-alive adatok gyűjtése
 *
 * @author BT
 */
public class HttpListener2KeepAliveCollector extends CollectorBase {

    public static final String PATH = "network/http-listener-2/keep-alive";

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
