/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServerCollector.java
 *  Created: 2018.01.21. 11:47:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.server;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.IAppServerCollector;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Alkalmazás Server adatgyűjtés
 * http://localhost:4848/monitoring/domain/server/applications/{appRealName}/server
 *
 * @author BT
 */
@Slf4j
public class AppServerCollector extends CollectorBase implements IAppServerCollector {

    /**
     * Aktuális path az ősöknek
     */
    @Override
    public String getPath() {
        return null;
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @param entities JSon entitás
     *
     * @return mért értékek listája
     */
    protected List<CollectedValueDto> fetchValues(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<CollectedValueDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

            //Leszedjük az adatnevet és megvizsgáljuk, hogy kell-e gyűjteni egyáltalán ezt az adatnév értéket?
            String dataName = jsonValueEntity.getJsonString("name").getString();

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
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param appRealName       az alkalmazás igazi neve
     * @param sessionToken      GF session token
     *
     * @return mért eredmények listája
     *
     */
    @Override
    public List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String appRealName, String sessionToken) {

        String appPath = getPath() + appRealName;
        Response response = restDataCollector.getMonitorResponse(appPath, simpleUrl, sessionToken);
        JsonObject entities = restDataCollector.getJsonEntities(response);

        return this.fetchValues(entities);
    }

}
