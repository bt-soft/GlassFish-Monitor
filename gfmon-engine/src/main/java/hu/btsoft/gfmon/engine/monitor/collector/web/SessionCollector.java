/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SessionCollector.java
 *  Created: 2017.12.26. 9:13:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.web;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * WEB/session adatok gyűjtése
 *
 * @author BT
 */
public class SessionCollector extends CollectorBase {

    public static final String PATH = "web/session";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return path
     */
    @Override
    protected String getPath() {
        return PATH;
    }

}
