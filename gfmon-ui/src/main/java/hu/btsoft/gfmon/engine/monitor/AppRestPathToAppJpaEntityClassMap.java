/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SvrRestPathToJpaEntityClassMap.java
 *  Created: 2018.01.06. 10:40:13
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.app.AppStatistic;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.app.AppServletStatistic;
import hu.btsoft.gfmon.engine.monitor.collector.application.ApplicationsCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * Monitorozott URL -> JPA entitás osztály típus map
 *
 * @author BT
 */
@Slf4j
public class AppRestPathToAppJpaEntityClassMap {

    //public static final Pattern SERVER_REGEXP = Pattern.compile("^\\/?applications\\/([^\\/]+)\\/(?<subPath>server(\\/.+)?)$");
    /**
     * Monitor nakedPath alapján megállíptja, hogy milyen entitást kell használni
     *
     * @param tokenizedPath application monitor path vége
     *
     * @return JPA entitás osztály típus
     */
    public static Class<? extends AppSnapshotBase> getJpaEntityClass(String tokenizedPath) {

        Class<? extends AppSnapshotBase> clazz = null;

        switch (tokenizedPath) {
            case ApplicationsCollector.APP_STAT_TOKENIZED_PATH:
                clazz = AppStatistic.class;
                break;

            case ApplicationsCollector.APP_STAT_SERVLET_TOKENIZED_PATH:
                clazz = AppServletStatistic.class;
                break;

            default:
                log.error("A(z) '{}' monitor path-hoz nincs alkalmazás JPA entitás osztály rendelve!", tokenizedPath);
        }

        return clazz;
    }
}
