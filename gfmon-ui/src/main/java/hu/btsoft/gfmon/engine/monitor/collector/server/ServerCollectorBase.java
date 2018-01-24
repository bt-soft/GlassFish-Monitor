/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerCollectorBase.java
 *  Created: 2017.12.24. 17:21:48
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * A GF REST interfészén keresztül adatokat gyűjtő kollektorok ős osztálya
 *
 * @author BT
 */
@Slf4j
public abstract class ServerCollectorBase extends CollectorBase implements IServerCollector {

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @param entities            JSon entitás
     * @param collectedDatatNames kigyűjtendő adatnevek halmaza
     *
     * @return értékek listája
     */
    protected List<CollectedValueDto> fetchValues(JsonObject entities, Set<String> collectedDatatNames) {

        if (entities == null) {
            return null;
        }

        List<CollectedValueDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

            //Leszedjük az adatnevet és megvizsgáljuk, hogy kell-e gyűjteni egyáltalán ezt az adatnév értéket?
            String dataName = jsonValueEntity.getJsonString("name").getString();
            if (!collectedDatatNames.contains(dataName)) {
                continue;
            }

            String unitName = jsonValueEntity.getJsonString("unit").getString();
            if (unitName == null) {
                log.error("A(z) '{}' JSon entitásnak nincs 'unit' értéke!", entityName);
                continue;
            }

            //Unit type kitalálása
            ValueUnitType valueUnitType = ValueUnitType.fromValue(jsonValueEntity.getJsonString("unit").getString());
            //Ha COUNT az unit, de van 'lowwatermark' és 'highwatermark' -> COUNT_CURLWHW lesz a típus
            if (valueUnitType == ValueUnitType.COUNT
                    && jsonValueEntity.getJsonNumber("lowwatermark") != null
                    && jsonValueEntity.getJsonNumber("highwatermark") != null) {
                valueUnitType = ValueUnitType.COUNT_CURLWHW;
            }

            CollectedValueDto dto = new CollectedValueDto();

            dto.setUnit(valueUnitType);
            dto.setLastSampleTime(long2Date(jsonValueEntity.getJsonNumber("lastsampletime").longValue()));
            dto.setName(dataName);
            dto.setStartTime(long2Date(jsonValueEntity.getJsonNumber("starttime").longValue()));
            dto.setPath(getPath());

            //Érték típushelyes leszedése
            switch (valueUnitType) {

                case SECONDS:
                case MILLISECOND:
                case NANOSECOND:
                case COUNT:
                case BYTES:
                    dto.setCount(jsonValueEntity.getJsonNumber("count").longValue());
                    break;

                case COUNT_CURLWHW:
                    JsonNumber jn = jsonValueEntity.getJsonNumber("current");
                    dto.setCurrent(jn.longValue());

                    dto.setLowWatermark(jsonValueEntity.getJsonNumber("lowwatermark").longValue());
                    dto.setHighWatermark(jsonValueEntity.getJsonNumber("highwatermark").longValue());
                    break;

                case LIST:
                case STRING:
                    dto.setCurrent(jsonValueEntity.getJsonString("current").getString());
                    break;

                default:
                    log.warn("Nincs lekezelve a JSon entitás, név: '{}', unit: '{}' !", entityName, unitName);
                    break;
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * REST JSon monitor adatok összegyűjtése
     *
     * @param restDataCollector  REST adatgyűjtó példány
     * @param simpleUrl          a szerver url-je
     * @param sessionToken       GF session token
     * @param collectedDataNames kigyűjtendő adatnevek halmaza
     *
     * @return Json entitás - értékek Lista
     */
    @Override
    public List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken, Set<String> collectedDataNames) {

        //Ha nem kell ebből az adatgyűjtőből semmi adat, akkor meg sem hívjuk;
        if (collectedDataNames == null) {
            return null;
        }

        JsonObject entities = super.getMonitoredEntities(restDataCollector, simpleUrl, sessionToken, getPath());
        return this.fetchValues(entities, collectedDataNames);
    }

}
