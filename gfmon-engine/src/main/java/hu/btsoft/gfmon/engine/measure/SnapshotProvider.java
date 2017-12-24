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
package hu.btsoft.gfmon.engine.measure;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.measure.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import hu.btsoft.gfmon.engine.model.entity.Server;
import hu.btsoft.gfmon.engine.model.entity.Snapshot;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.StreamSupport;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy GF szerver monitorozását elvégző CDI bean
 *
 * - Paraméterként megkapja a monitorozando GF adatait
 * - Jól meg is nézegeti a GF REST interfészén keresztül a szükséges adatokat
 * - Majd az eredményeket beira az adatbázisba
 *
 * @author BT
 */
@Slf4j
public class SnapshotProvider {

    @Inject
    private Instance<Function<RestDataCollector, HashMap<String/*entityName*/, ValueBaseDto>>> dataCollectors;

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JsonEntityNameToSnapshotMapper jsonEntityNameToSnapshotMapper;

    /**
     *
     * @param server Server entitás
     *
     * @return Snapshot példány
     */
    public Snapshot fetchSnapshot(Server server) {

        long start = Elapsed.nowNano();

        restDataCollector.setSimpleUrl(server.getSimpleUrl());
        restDataCollector.setSessionToken(server.getSessionToken());

        Snapshot snapshot = collectData();

        log.trace("server url: {}, elapsed: {}", server.getUrl(), Elapsed.getNanoStr(start));

        return snapshot;
    }

    private Snapshot collectData() {

        Snapshot snapshot = new Snapshot();

//        Consumer<HashMap<String, ValueDTOBase>> valuesMapConsumer = (HashMap<String, ValueDTOBase> valuesMap) -> {
//            if (valuesMap != null) {
//                for (String entityName : valuesMap.keySet()) {
//
//                    ValueDTOBase dto = valuesMap.get(entityName);
//                    log.trace(String.format("\nEntityName: %s, values: %s", entityName, dto.toString()));
//                }
//            }
//        };
//
        DataCollectionBehaviour dataCollectionBehaviour = new DataCollectionBehaviour(jsonEntityNameToSnapshotMapper, snapshot);

        StreamSupport.stream(this.dataCollectors.spliterator(), false /* no/paralel */)
                .map(coll -> coll.apply(restDataCollector))
                .map(valuesMap -> (HashMap<String/*entityName*/, ValueBaseDto>) valuesMap)
                //.forEach(valuesMapConsumer);
                .forEach(dataCollectionBehaviour::perform);

        return snapshot;
    }

    private class DataCollectionBehaviour {

        private final JsonEntityNameToSnapshotMapper entityName2SnapshotMapper;
        private final Snapshot snapshot;

        /**
         * Konstruktor
         *
         * @param entityName2SnapshotMapper
         * @param snapshot
         */
        public DataCollectionBehaviour(JsonEntityNameToSnapshotMapper entityName2SnapshotMapper, Snapshot snapshot) {
            this.entityName2SnapshotMapper = entityName2SnapshotMapper;
            this.snapshot = snapshot;
        }

        public void perform(HashMap<String, ValueBaseDto> valuesMap) {
            if (valuesMap != null) {
                entityName2SnapshotMapper.map(valuesMap, snapshot);
            }
        }
    }
}
