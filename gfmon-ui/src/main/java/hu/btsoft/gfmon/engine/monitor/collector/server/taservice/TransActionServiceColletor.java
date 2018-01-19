/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    TransActionServiceColletor.java
 *  Created: 2017.12.27. 14:29:18
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server.taservice;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * TransAction Service adatok gyűjtése
 *
 * @author BT
 */
public class TransActionServiceColletor extends CollectorBase {

    public static final String PATH = "transaction-service";

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
