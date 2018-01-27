/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServletStatisticCollector.java
 *  Created: 2018.01.27. 9:11:54
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.app;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 * /applications/{appRealname}/server/{servletName} -> alklamazás servlet statisztika
 *
 * @author BT
 */
public class AppServletStatisticCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/applications/{appRealName}/server/{childResourcesPath}";

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
