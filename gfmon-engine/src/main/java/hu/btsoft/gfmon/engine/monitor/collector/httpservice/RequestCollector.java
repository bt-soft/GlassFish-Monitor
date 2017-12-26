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
package hu.btsoft.gfmon.engine.monitor.collector.httpservice;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * http request adatok gyűjtése
 *
 * @author BT
 */
public class RequestCollector extends CollectorBase {

    public static final String URI = "server/http-service/server/request";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     * pl.: "server/http-service/server/request"
     *
     * @return uri
     */
    @Override
    protected String getUri() {
        return URI;
    }
}
