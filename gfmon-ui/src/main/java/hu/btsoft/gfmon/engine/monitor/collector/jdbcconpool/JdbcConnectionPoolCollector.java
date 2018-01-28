/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPoolCollector.java
 *  Created: 2018.01.28. 12:12:47
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.jdbcconpool;

import hu.btsoft.gfmon.engine.monitor.collector.application.AppStatCollectorBase;

/**
 * http://localhost:4848/monitoring/domain/server/resources
 *
 * @author BT
 */
public class JdbcConnectionPoolCollector extends AppStatCollectorBase {

    /**
     * path
     */
    public static final String PATH = "/resources/{connectionPoolname}";

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
