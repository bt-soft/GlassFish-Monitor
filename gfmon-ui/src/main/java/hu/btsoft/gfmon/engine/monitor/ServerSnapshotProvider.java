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
import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SvrSnapshotBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.server.ServerMonitorValueDto;
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
import hu.btsoft.gfmon.engine.monitor.collector.server.IServerCollector;

/**
 * Egy GF szerver összes szerver monitoradatát begyűjtő CDI bean
 *
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
    private JSonEntityToServerSnapshotEntityMapper jSonEntityToServerSnapshotEntityMapper;

    /**
     * Monitorizható adatnevek adatainak kigyűjtése Ezt csak egy üres adatbázis során indítjuk el
     *
     * @param server a monitorozandó Server entitása
     *
     * @return adatnevek halmaza
     */
    public List<DataUnitDto> fetchDataUnits(Server server) {

        List<DataUnitDto> result = null;

        //Végigmegyünk az összes adatgyűjtőn
        for (IServerCollector collector : serverCollectors) {

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
     * Az összes szerver kollentor adatait összegyűjti, majd egy új szerver Snapshot entitásba rakja az eredményeket
     *
     * @param server a monitorozandó Server entitása
     *
     * @return szerver Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<SvrSnapshotBase> fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        Set<SvrSnapshotBase> snapshots = null;

        //Kigyűjtjük a szerver beállításaiban található monitorozandó path-okat és adatneveket
        //
        //A path-al tudjuk eldönteni, hogy egy kolektort egyáltalán meg kell-e hívni?
        Set<String> serverMonitorablePaths = new HashSet<>();

        //Az egyes Path-ok alatti adatnevek halmaza, ezzel az adott kollektor munkáját tudjuk szűkíteni
        Map<String, Set<String>> collectedDatatNamesMap = new HashMap<>();

        if (server.getJoiners() != null) {
            server.getJoiners().forEach((joiner) -> {

                SvrCollectorDataUnit svrDcu = joiner.getSvrCollectorDataUnit();

                //A kollektorok Path-jai
                String path = svrDcu.getRestPath();
                serverMonitorablePaths.add(path);

                if (!collectedDatatNamesMap.containsKey(path)) {
                    collectedDatatNamesMap.put(path, new HashSet<>());
                }
                Set<String> collectedDatatNames = collectedDatatNamesMap.get(path);

                //Ha kell gyűjteni az adatnevet, akkor megjegyezzük
                if (Objects.equals(Boolean.TRUE, joiner.getActive())) {
                    collectedDatatNames.add(svrDcu.getDataName());
                }
            });
        }

        //Végigmegyünk az összes adatgyűjtőn
        for (IServerCollector collector : serverCollectors) {

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
            List<ServerMonitorValueDto> valuesList = collector.execute(restDataCollector, server.getSimpleUrl(), server.getSessionToken(), collectedDatatNames);

            //Üres a mért eredmények Map-je
            if (valuesList == null || valuesList.isEmpty()) {
                log.warn("A(z) '{}' szerver mérési eredményei üresek!", server.getSimpleUrl());
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            if (snapshots == null) {
                snapshots = new HashSet<>();
            }
            jSonEntityToServerSnapshotEntityMapper.map(valuesList, snapshots);
        }

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getElapsedNanoStr(start));

        return snapshots;
    }
}
