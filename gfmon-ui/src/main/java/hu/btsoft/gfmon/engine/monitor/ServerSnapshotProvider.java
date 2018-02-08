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
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.IServerCollector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy GF szerver összes szerver monitoradatát begyűjtő CDI bean
 * <p>
 * - Paraméterként megkapja a monitorozando GF szerver adatait
 * - Jól meg is nézegeti a GF REST interfészén keresztül a szükséges adatokat
 * - Közben gyűjti az egyes mért adatok nevét/mértékegységét/leírását az adatbázisba (a CollectorDataUnit entitás segítségével)
 *
 * @author BT
 */
@Slf4j
public class ServerSnapshotProvider {

    @Inject
    private Instance<IServerCollector> serverCollectors;

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JSonEntityToSnapshotEntityMapper jSonEntityToServerSnapshotEntityMapper;

    /**
     * Monitorozható adatnevek adatainak kigyűjtése Ezt csak egy üres adatbázis során indítjuk el
     *
     * @param server a monitorozandó Server entitása
     *
     * @return adatnevek halmaza
     */
    public List<DataUnitDto> fetchDataUnits(Server server) {

        List<DataUnitDto> result = null;

        //Végigmegyünk az összes adatgyűjtőn
        for (IServerCollector collector : serverCollectors) {

            List<DataUnitDto> collectDataUnits = collector.collectDataUnits(restDataCollector, server.getSimpleUrl(), server.getUserName(), server.getSessionToken());

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
     * Kigyűjtjük a szerver beállításaiban található monitorozandó path-okat és adatneveket
     *
     * @param server Szerver
     *
     * @return Map, key: monitorozando Path, value: gyűjtendő adatnevek Set-je
     */
    private Map<String/* path */, Set<String> /* dataNames */> createCollectedDatatNamesMap(Server server) {

        //Az egyes Path-ok alatti gyűjtendő adatnevek halmaza, ezzel az adott kollektor munkáját tudjuk szűkíteni
        Map<String/* path */, Set<String> /* dataNames */> collectedDatatNamesMap = new HashMap<>();

        if (server.getJoiners() != null) {

            server.getJoiners().stream()
                    .filter((joiner) -> (joiner.isActive()))
                    .map((joiner) -> joiner.getSvrCollectorDataUnit())
                    .forEachOrdered((svrCdu) -> {
                        String path = svrCdu.getRestPath();
                        if (!collectedDatatNamesMap.containsKey(path)) {
                            collectedDatatNamesMap.put(path, new HashSet<>());
                        }
                        Set<String> collectedDatatNames = collectedDatatNamesMap.get(path);
                        collectedDatatNames.add(svrCdu.getDataName());
                    });
        }

        return collectedDatatNamesMap;
    }

    /**
     * Az összes szerver kollektor adatait összegyűjti, majd egy új szerver Snapshot entitásba rakja az eredményeket
     *
     * @param server       a monitorozandó Server entitása
     * @param erroredPaths hibára futott mérési oldalak (pl.: törölték a listenert)
     *
     * @return szerver Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<SnapshotBase> fetchSnapshot(Server server, Set<String> erroredPaths) {

        long start = Elapsed.nowNano();

        Set<SnapshotBase> snapshots = null;

        Map<String/* path */, Set<String> /* dataNames */> collectedDatatNamesMap = createCollectedDatatNamesMap(server);

        //Végigmegyünk az összes adatgyűjtőn
        for (IServerCollector collector : serverCollectors) {

            //meg kell hívni ezt az adatgyűjtőt?
            if (!collectedDatatNamesMap.keySet().contains(collector.getPath())) {
                continue;
            }

            //Gyűjtendő adatnevek halmaza
            Set<String> collectedDatatNames = collectedDatatNamesMap.get(collector.getPath());

            //Csak, ha van mit összegyűjteni, akkor indítjuk a kollektort
            if (collectedDatatNames == null || collectedDatatNames.isEmpty()) {
                continue;
            }

            //Az adott kollektor adatainak lekérése
            List<CollectedValueDto> valuesList = collector.execute(restDataCollector, server.getSimpleUrl(), server.getUserName(), server.getSessionToken(), collectedDatatNames, erroredPaths);

            //Üres a mért eredmények Map-je
            if (valuesList == null || valuesList.isEmpty()) {
                log.warn("A(z) '{}' szerver '{}' path mérési eredményei üresek!", server.getSimpleUrl(), collector.getPath());
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            SnapshotBase jpaEntity = (SnapshotBase) jSonEntityToServerSnapshotEntityMapper.map(valuesList);
            if (jpaEntity != null) {
                if (snapshots == null) {
                    snapshots = new LinkedHashSet<>();
                }
                snapshots.add(jpaEntity);
            }
        }

//        log.info("Szerver Stat kigyűjtése elapsed: {}", Elapsed.getElapsedNanoStr(start));
        return snapshots;
    }
}
