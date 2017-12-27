/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SnapshotProvider.java
 *  Created: 2017.12.24. 16:16:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.entity.Server;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.monitor.collector.MonitorValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy GF szerver monitorozását elvégző CDI bean
 *
 * - Paraméterként megkapja a monitorozando GF adatait
 * - Jól meg is nézegeti a GF REST interfészén keresztül a szükséges adatokat
 *
 * @author BT
 */
@Slf4j
public class SnapshotProvider {

    @Inject
    private Instance<ICollectMonitoredData> dataCollectors;

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JSonEntityToSnapsotEntityMapper jsonEntityToSnapsotModelMapper;

    /**
     * Az összes kollentor adatait összegyűjti, majd egy új Snapshot entitásba rakja az eredményeket
     *
     * @param server a monitorozandó Server entitása
     *
     * @return Snapshot példány, az adatgyűjtés eredménye (új entitás)
     */
    public Set<SnapshotBase> fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        Set<SnapshotBase> snapshots = null;

        //Végigmegyünk az összes adatgyűjtőn
        for (ICollectMonitoredData collector : dataCollectors) {

//            //Megvizsgáljuk, hogy az adott szervernél be van-e kapcsolva az a MonitorServices modul, amit a kollektor nézegetne
//            //Ha nem, akkor nem indítjuk a kollektort
//            if (!server.getMonitorableModules().contains(collector.getMonitoringServiceModuleName())) {
//                continue;
//            }
//
            //Az adott kollektor adatainak lekérése
            HashMap<String/*JSon entityName*/, MonitorValueDto> valuesMap = collector.execute(restDataCollector, server.getSimpleUrl(), server.getSessionToken());

            //Üres a mért eredmének Map-je
            if (valuesMap == null || valuesMap.isEmpty()) {
                log.warn("A(z) {} szerver mérési eredményei üresek!", server.getSimpleUrl());
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            if (snapshots == null) {
                snapshots = new HashSet<>();
            }
            jsonEntityToSnapsotModelMapper.map(valuesMap, snapshots);
        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getNanoStr(start));

        return snapshots;
    }
}
