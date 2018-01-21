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
import hu.btsoft.gfmon.engine.model.entity.application.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.monitor.collector.application.IApplicationCollector;
import java.util.Set;
import javax.enterprise.inject.Instance;
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
    private Instance<IApplicationCollector> applicationCollectors;

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

        //Végigmegyünk az összes adatgyűjtőn
        for (IApplicationCollector collector : applicationCollectors) {

        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getElapsedNanoStr(start));

        return snapshots;
    }

}
