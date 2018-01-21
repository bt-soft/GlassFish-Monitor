/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ThreadSystemCollector.java
 *  Created: 2017.12.26. 10:23:51
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server.jvm;

import hu.btsoft.gfmon.engine.monitor.collector.server.ServerCollectorBase;

/**
 * JVM thread adatgyűjtő
 *
 * @author BT
 */
public class ThreadSystemCollector extends ServerCollectorBase {

    public static final String PATH = "jvm/thread-system";

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
