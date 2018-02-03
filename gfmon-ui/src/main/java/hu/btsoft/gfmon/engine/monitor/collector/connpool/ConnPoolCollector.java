/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolCollector.java
 *  Created: 2018.01.28. 12:12:47
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.connpool;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 * http://localhost:4848/monitoring/domain/server/resources
 *
 * @author BT
 */
public class ConnPoolCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/resources/{connectionPoolName}";

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
