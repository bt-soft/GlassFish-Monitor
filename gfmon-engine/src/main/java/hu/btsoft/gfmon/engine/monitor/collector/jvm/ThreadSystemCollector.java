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
package hu.btsoft.gfmon.engine.monitor.collector.jvm;

import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;

/**
 * JVM thread adatgyűjtő
 *
 * @author BT
 */
public class ThreadSystemCollector extends CollectorBase {

    public static final String PATH = "jvm/thread-system";

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
