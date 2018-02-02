/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppEjbBeanCacheStatCollector.java
 *  Created: 2018.01.27. 9:33:10
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.ejb;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 *
 * @author BT
 */
public class AppEjbBeanCacheStatCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/applications/{appRealName}/{beanName}/bean-cache";

    /**
     * Path elkérése
     *
     * @return path
     */
    @Override
    public String getPathForEntityMapping() {
        return PATH;
    }
}
