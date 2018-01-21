/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationCollectorBase.java
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
 * Alkalmazás adatgyűjtés ős osztály
 *
 * http://localhost:4848/monitoring/domain/server/applications/{appRealName}/???
 *
 * @author BT
 */
@Slf4j
public abstract class ApplicationCollectorBase extends CollectorBase implements IAppServerCollector {

    /**
     * Az alkalmazás neve
     */
    protected String appRealName;

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @return az összegyűjtött adatok listájának map-je a kategóriával/hellyel kulcsolva
     */
    private List<CollectedValueDto> fetchValues(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<CollectedValueDto> resultList = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

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

            resultList.add(dto);
        }

        return resultList;
    }

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param appRealName       az alkalmazás igazi nevével
     * @param sessionToken      GF session token
     *
     * @return application új entitás snapshotok listája
     *
     */
    @Override
    public List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String appRealName, String sessionToken) {

        this.appRealName = appRealName;

        Response response = restDataCollector.getMonitorResponse(this.getPathWithRealAppName(), simpleUrl, sessionToken);
        JsonObject jsonEntities = restDataCollector.getJsonEntities(response);

        return this.fetchValues(jsonEntities);
    }

}
