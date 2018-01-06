/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    CollectorBase.java
 *  Created: 2017.12.24. 17:21:48
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector;

import hu.btsoft.gfmon.corelib.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.monitor.ICollectMonitoredData;
import hu.btsoft.gfmon.engine.monitor.MonitorPathToJpaEntityClassMap;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * A GF REST interfészén kereztül adatokat gyűjtő kollektorok ős osztálya
 *
 * @author BT
 */
@Slf4j
public abstract class CollectorBase implements ICollectMonitoredData {

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni?
     * pl.: "server/http-service/server/request"
     *
     * @return path
     */
    protected abstract String getPath();

    /**
     * Timestamp -> Date konverzió
     *
     * @param value dátum timestamp formátumban
     *
     * @return java Date objektum vagy null
     */
    private Date long2Date(long value) {

        if (value == -1) {
            return null;
        }

        Date result = null;
        try {
            result = new Date(value);
        } catch (Exception e) {
            log.warn("Dátum konverziós hiba: {}", value, e);
        }
        return result;
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     *
     * @param entities JSon entitás
     *
     * @return értékek listája
     */
    protected List<MonitorValueDto> fetchValues(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<MonitorValueDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

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

            MonitorValueDto dto = new MonitorValueDto();

            dto.setUnit(valueUnitType);
            dto.setLastSampleTime(long2Date(jsonValueEntity.getJsonNumber("lastsampletime").longValue()));
            dto.setName(jsonValueEntity.getJsonString("name").getString());
            dto.setStartTime(long2Date(jsonValueEntity.getJsonNumber("starttime").longValue()));
//          dto.setDescription(entity.getJsonString("description").getString());
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
     * @param restDataCollector REST adatgyűjtó példány
     * @param simpleUrl         a szerver url-je
     * @param sessionToken      GF session token
     *
     * @return Json entitás - értékek Lista
     */
    @Override
    public List<MonitorValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken) {

        Response response = restDataCollector.getMonitorResponse(getPath(), simpleUrl, sessionToken);
        JsonObject entities = restDataCollector.getJsonEntities(response);

        return this.fetchValues(entities);
    }

    /**
     * A REST válaszokból kinyeri az adatneveket és leírásukat
     *
     * @param entities JSon entitás
     *
     * @return adatneves leírása
     */
    protected List<DataUnitDto> fetchDataUnits(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<DataUnitDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        entities.keySet().stream().map((entityName) -> entities.getJsonObject(entityName)).map((jsonValueEntity) -> {
            DataUnitDto dto = new DataUnitDto();
            dto.setRestPath(this.getPath());
            Class entityClass = MonitorPathToJpaEntityClassMap.getJpaEntityClass(this.getPath());
            dto.setEntityName(entityClass != null ? entityClass.getSimpleName() : "unknown");
            dto.setDataName(jsonValueEntity.getJsonString("name").getString());
            dto.setUnit(jsonValueEntity.getJsonString("unit").getString());
            dto.setDescription(jsonValueEntity.getJsonString("description").getString());
            return dto;
        }).forEachOrdered((dto) -> {
            result.add(dto);
        });

        return result;

    }

    /**
     * A mért adatok neve/mértékegysége/leírása lista
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param sessionToken      GF session token
     *
     * @return mért adatok leírásának listája
     */
    @Override
    public List<DataUnitDto> collectDataUnits(RestDataCollector restDataCollector, String simpleUrl, String sessionToken) {
        Response response = restDataCollector.getMonitorResponse(this.getPath(), simpleUrl, sessionToken);
        JsonObject entities = restDataCollector.getJsonEntities(response);

        return this.fetchDataUnits(entities);
    }

}
