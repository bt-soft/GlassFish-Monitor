/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JspCollector.java
 *  Created: 2017.12.26. 9:13:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.web;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * WEB/JSP adatok gyűjtése
 *
 * @author BT
 */
public class JspColletor extends CollectorBase {

    public static final String PATH = "web/jsp";

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
