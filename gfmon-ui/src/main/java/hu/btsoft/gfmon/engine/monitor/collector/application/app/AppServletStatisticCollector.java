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
 * /applications/{appRealname}/server/{servletName} -> alkalmazás servlet statisztika
 * - jsp
 * - defaul
 * - FacesServlet
 * - ThemeServlet
 *
 * @author BT
 */
public class AppServletStatisticCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/applications/{appRealName}/server/{servletName}";

    /**
     * JPA entitás map-hez
     *
     * @return
     */
    @Override
    public String getPathForEntityMapping() {
        return PATH;
    }
}
