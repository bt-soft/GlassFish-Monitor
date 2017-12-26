/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    MemoryColletor.java
 *  Created: 2017.12.26. 9:13:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.jvm;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * JVM/Memory adatok gyűjtése
 *
 * @author BT
 */
public class MemoryColletor extends CollectorBase {

    public static final String URI = "jvm/memory";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return uri
     */
    @Override
    protected String getUri() {
        return "jvm/memory";
    }

}
