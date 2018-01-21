/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServerCollector.java
 *  Created: 2018.01.21. 17:30:30
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.server;

/**
 *
 * @author BT
 */
public class AppServerCollector extends ApplicationCollectorBase {

    public static final String PATH = "applications/<appRealName>/server";

    /**
     * Aktuális path az ősöknek
     */
    @Override
    public String getPath() {
        return PATH;
    }

    @Override
    public String getPathWithRealAppName() {
        return PATH.replace("<appRealName>", appRealName);
    }
}
