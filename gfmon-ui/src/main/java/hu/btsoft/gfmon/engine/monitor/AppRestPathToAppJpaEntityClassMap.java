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
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.server.ApplicationServer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.server.ApplicationServerChild;
import hu.btsoft.gfmon.engine.monitor.collector.application.server.AppServerCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.server.child.AppServerChildJspCollector;
import lombok.extern.slf4j.Slf4j;

/**
 * Monitorozott URL -> JPA entitás osztály típus map
 *
 * @author BT
 */
@Slf4j
public class AppRestPathToAppJpaEntityClassMap {

    /**
     * Monitor nakedPath alapján megállíptja, hogy milyen entitást kell használni
     *
     * @param nakedPath monitor path
     *
     * @return JPA entitás osztály típus
     */
    public static Class<? extends AppSnapshotBase> getJpaEntityClass(String nakedPath) {

        Class<? extends AppSnapshotBase> clazz = null;

        //A JPA entitás típusát attól függően azonosítjuk, hogy mely path-ról származik a mérés
        switch (nakedPath) {

            case AppServerCollector.PATH:
                clazz = ApplicationServer.class;
                break;

            case AppServerChildJspCollector.PATH:
                clazz = ApplicationServerChild.class;
                break;

            default:
                log.error("A(z) '{}' monitor path-hoz nincs alkalmazás JPA entitás osztály rendelve!", nakedPath);
        }

        return clazz;

    }
}
