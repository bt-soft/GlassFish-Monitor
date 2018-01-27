/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppWebStatisticCollector.java
 *  Created: 2018.01.21. 17:30:30
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.app;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 * /applications/{appRealname}/server -> alkalmazás statisztika
 *
 * @author BT
 */
public class AppWebStatisticCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/applications/{appRealName}/server";

    /**
     * Path elkérése
     *
     * @return path
     */
    @Override
    public String getPath() {
        return PATH;
    }
}
