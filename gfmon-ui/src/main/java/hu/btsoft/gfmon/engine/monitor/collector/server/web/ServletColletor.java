/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServletColletor.java
 *  Created: 2017.12.26. 9:13:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server.web;

import hu.btsoft.gfmon.engine.monitor.collector.server.ServerCollectorBase;

/**
 * WEB/Servlet adatok gyűjtése
 *
 * @author BT
 */
public class ServletColletor extends ServerCollectorBase {

    public static final String PATH = "web/servlet";

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
