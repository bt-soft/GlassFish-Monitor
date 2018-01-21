/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationSnapshotProvider.java
 *  Created: 2018.01.21. 10:52:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.monitor.collector.application.ApplicationsCollector;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy szerver összes alkalmazásainak monitoradat begyűjtését végző CDI bean
 *
 * @author BT
 */
@Slf4j
public class ApplicationSnapshotProvider {

    @Inject
    private ApplicationsCollector applicationsCollector;

    /**
     * Az összes alkalmazás kollentor adatait összegyűjti, majd egy új alkalmazás Snapshot entitásba rakja az eredményeket
     *
     * @param server a monitorozandó Server entitása
     *
     * @return alkalmazás Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<AppSnapshotBase> fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        Set<AppSnapshotBase> snapshots = null;

        //Véégigmegyünk a szerver alkalmazásain
        for (Application app : server.getApplications()) {

            //Ha monitorozásra akítív, akkor meghívjuk rá az adatgyűjtőt
            if (app.getActive() != null && Objects.equals(app.getActive(), Boolean.TRUE)) {

                Set<AppSnapshotBase> appSnapshots = applicationsCollector.start(server.getSimpleUrl(), server.getSessionToken(), app.getAppRealName());

                if (appSnapshots != null && !appSnapshots.isEmpty()) {

                    //Beállítjuk az összes pillanatfelvételen, hogy ez melyik alkalmazáshoz van rendelve
                    appSnapshots.forEach((appSnapshot) -> {
                        appSnapshot.setApplication(app);
                    });

                    if (snapshots == null) {
                        snapshots = new LinkedHashSet<>();
                    }
                    snapshots.addAll(appSnapshots);
                }
            }
        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getElapsedNanoStr(start));

        return snapshots;
    }

}
