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
package hu.btsoft.gfmon.engine.monitor.collector.server.jvm;

import hu.btsoft.gfmon.engine.monitor.collector.server.ServerCollectorBase;

/**
 * JVM/Memory adatok gyűjtése
 *
 * @author BT
 */
public class MemoryColletor extends ServerCollectorBase {

    public static final String PATH = "jvm/memory";

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     *
     * @return uri
     */
    @Override
    public String getPath() {
        return PATH;
    }

}
