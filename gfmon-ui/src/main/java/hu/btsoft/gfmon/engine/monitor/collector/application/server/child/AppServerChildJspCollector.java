/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServerChildJspCollector.java
 *  Created: 2018.01.21. 17:13:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.server.child;

/**
 * http://localhost:4848/monitoring/domain/server/applications/{realAppname}/server/jsp
 *
 * @author BT
 */
public class AppServerChildJspCollector /*extends ApplicationCollectorBase*/ {

    public static final String PATH = "applications/<appRealName>/server/jsp";

//    /**
//     * Aktuális path az ősöknek
//     */
//    @Override
//    public String getPath() {
//        return PATH;
//    }
//
//    @Override
//    public String getPathWithRealAppName() {
//        return PATH.replace("<appRealName>", appRealName);
//    }
}
