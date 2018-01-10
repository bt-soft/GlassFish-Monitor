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

import hu.btsoft.gfmon.corelib.model.dto.DataUnitDto;
import hu.btsoft.gfmon.corelib.model.entity.server.CollectorDataUnit;
import hu.btsoft.gfmon.corelib.model.entity.server.Server;
import hu.btsoft.gfmon.corelib.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.monitor.collector.MonitorValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy GF szerver monitorozását elvégző CDI bean
 *
 * - Paraméterként megkapja a monitorozando GF adatait
 * - Jól meg is nézegeti a GF REST interfészén keresztül a szükséges adatokat
 * - Közben gyűjti az egyes mért adatok nevét/mértékegységét/leírását az adatbázisba (a CollectorDataUnit entitás segítségével)
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
     * Monitorizható adatnevek adatainak kigyűjtése
     * Ezt csak egy üres adatbázis során indítjuk el
     *
     * @param server a monitorozandó Server entitása
     *
     * @return adatnevek halmaza
     */
    public List<DataUnitDto> fetchDataUnits(Server server) {

        List<DataUnitDto> result = null;

        //Végigmegyünk az összes adatgyűjtőn
        for (ICollectMonitoredData collector : dataCollectors) {

            List<DataUnitDto> collectDataUnits = collector.collectDataUnits(restDataCollector, server.getSimpleUrl(), server.getSessionToken());

            if (collectDataUnits != null) {
                if (result == null) {
                    result = new LinkedList<>();
                }
                result.addAll(collectDataUnits);
            }
        }

        return result;

    }

    /**
     * Az összes kollentor adatait összegyűjti, majd egy új Snapshot entitásba rakja az eredményeket
     *
     * @param server a monitorozandó Server entitása
     *
     * @return Snapshot példányok halmaza, az adatgyűjtés eredménye (új entitás)
     */
    public Set<SnapshotBase> fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        Set<SnapshotBase> snapshots = null;

        //Kigyűjtjük a szerver beállításaiban található monitorozandó path-okat és adatneveket
        //
        //A path-al tudjuk eldönteni, hogy egy kolektort egyáltalán meg kell-e hívni?
        Set<String> serverMonitorablePaths = new HashSet<>();

        //Az egyes Path-ok alatti adatnevek halmaza, ezzel az adott kollektor munkáját tudjuk szűkíteni
        Map<String, Set<String>> collectedDatatNamesMap = new HashMap<>();

        if (server.getJoiners() != null) {
            server.getJoiners().forEach((joiner) -> {

                CollectorDataUnit dcu = joiner.getCollectorDataUnit();

                //A kollektorok Path-jai
                String path = dcu.getRestPath();
                serverMonitorablePaths.add(path);

                if (!collectedDatatNamesMap.containsKey(path)) {
                    collectedDatatNamesMap.put(path, new HashSet<>());
                }
                Set<String> collectedDatatNames = collectedDatatNamesMap.get(path);

                //Ha kell gyűjteni az adatnevet, akkor megjegyezzük
                if (Objects.equals(Boolean.TRUE, joiner.getActive())) {
                    collectedDatatNames.add(dcu.getDataName());
                }
            });
        }

//
//        StreamSupport.stream(this.dataCollectors.spliterator(), false)
//                .filter(collector -> serverMonitorablePaths.contains(collector.getPath()))
//                .map(collector -> collector.execute(restDataCollector, server.getSimpleUrl(), server.getSessionToken(), collectedDatatNamesMap.get(collector.getPath())))
//                .map(valuesList -> (List<MonitorValueDto>)valuesList)
//                .filter(list -> list != null && !list.isEmpty())
//
//
        //Végigmegyünk az összes adatgyűjtőn
        for (ICollectMonitoredData collector : dataCollectors) {

            //meg kell hívni ezt az adatgyűjtőt?
            if (!serverMonitorablePaths.contains(collector.getPath())) {
                continue;
            }

            //Gyűjtendő adatnevek halmaza
            Set<String> collectedDatatNames = collectedDatatNamesMap.get(collector.getPath());

            //Csak, ha van mit összegyűjteni, akkor indítjuk a kollektort
            if (collectedDatatNames == null || collectedDatatNames.isEmpty()) {
                //log.trace("A(z) {} szerveren a(z) '{}' adatgyűjtőt nem kell futtatni", server.getUrl(), collector.getPath());
                continue;
            }

            //Az adott kollektor adatainak lekérése
            log.trace("A(z) '{}' szerveren a(z) '{}' adatgyűjtő futtatása", server.getUrl(), collector.getPath());
            List<MonitorValueDto> valuesList = collector.execute(restDataCollector, server.getSimpleUrl(), server.getSessionToken(), collectedDatatNames);

            //Üres a mért eredmények Map-je
            if (valuesList == null || valuesList.isEmpty()) {
                log.warn("A(z) '{}' szerver mérési eredményei üresek!", server.getSimpleUrl());
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            if (snapshots == null) {
                snapshots = new HashSet<>();
            }
            jsonEntityToSnapsotModelMapper.map(valuesList, snapshots);
        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getNanoStr(start));

        return snapshots;
    }
}
