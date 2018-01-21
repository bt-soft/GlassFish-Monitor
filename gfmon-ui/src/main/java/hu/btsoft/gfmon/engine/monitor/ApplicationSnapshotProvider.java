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
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.application.ApplicationsCollector;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
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
    public List<AppSnapshotBase> fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        List<String> collectedAppRealNames = new LinkedList<>();
        server.getApplications().forEach((app) -> {
            if (app.getActive() != null && Objects.equals(app.getActive(), Boolean.TRUE)) {
                collectedAppRealNames.add(app.getAppRealName());
            }
        });

        if (!collectedAppRealNames.isEmpty()) {
            List<CollectedValueDto> result = applicationsCollector.execute(server.getSimpleUrl(), server.getSessionToken(), collectedAppRealNames);
        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getElapsedNanoStr(start));

        return null;
    }

}
