/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpServiceRequestCollector.java
 *  Created: 2017.12.24. 17:21:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.httpservice;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * http request adatok gyűjtése
 *
 * @author BT
 */
public class HttpServiceRequestCollector extends CollectorBase {

    public static final String PATH = "server/http-service/server/request";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     * pl.: "server/http-service/server/request"
     *
     * @return uri
     */
    @Override
    public String getPath() {
        return PATH;
    }
}
