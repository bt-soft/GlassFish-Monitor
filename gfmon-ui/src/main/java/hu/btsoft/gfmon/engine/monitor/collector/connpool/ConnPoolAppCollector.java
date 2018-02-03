/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolAppCollector.java
 *  Created: 2018.01.28. 12:12:47
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.connpool;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 * A JDBC ConncetionPool-t használó alklamazások statisztikájának kigyűjtóje
 * <p>
 * http://localhost:4848/monitoring/domain/server/resources/{connectionPoolName}/{appname}
 *
 * @author BT
 */
public class ConnPoolAppCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/resources/{connectionPoolName}/{appName}";

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
