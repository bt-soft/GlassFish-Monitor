/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppEjbCollector.java
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
public class AppEjbBeanMethodCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/applications/{appRealName}/{beanName}/bean-methods/{methodname}";

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
