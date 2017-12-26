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

import hu.btsoft.gfmon.engine.monitor.ICollectMonitoredData;
import hu.btsoft.gfmon.engine.monitor.collector.dto.CurrentCountValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.ValueBaseDto;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
     * @return uri
     */
    protected abstract String getUri();

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
     * Value érték leszedése az unit nevének megfelelően
     *
     * @param entity JSON entitás
     *
     * @return
     */
    private Object getCurrentObjectValue(JsonObject entity) {
        String unitName = entity.getJsonString("unit").getString();

        Object value;
        switch (ValueUnitType.fromValue(unitName)) {
            case LIST:
            case STRING:
                value = entity.getJsonString("current").getString();
                break;

            case MILLISECOND:
            case COUNT:
            case BYTES:
                value = entity.getJsonNumber("current").longValue();
                break;

            default:
                value = "***unknown unit name: " + unitName;
        }

        return value;
    }

    /**
     * Sima count értékek leszedése
     *
     * @param entity JSON count entitás
     *
     * @return dto
     */
    private QuantityValueDto getQuantityValues(JsonObject entity) {
        QuantityValueDto dto = new QuantityValueDto();

        dto.setUnit(ValueUnitType.fromValue(entity.getJsonString("unit").getString()));
        dto.setLastSampleTime(long2Date(entity.getJsonNumber("lastsampletime").longValue()));
        dto.setName(entity.getJsonString("name").getString());
        dto.setCount(entity.getJsonNumber("count").longValue());
        dto.setDescription(entity.getJsonString("description").getString());
        dto.setStartTime(long2Date(entity.getJsonNumber("starttime").longValue()));

        return dto;
    }

    /**
     * Current count értékek leszedése
     *
     * @param entity JSON entitás
     *
     * @return dto
     */
    private CurrentCountValueDto getCurrentCountValues(JsonObject entity) {
        CurrentCountValueDto dto = new CurrentCountValueDto();
        dto.setUnit(ValueUnitType.fromValue(entity.getJsonString("unit").getString()));
        dto.setCurrent(entity.getJsonNumber("current").longValue());
        dto.setLastSampleTime(long2Date(entity.getJsonNumber("lastsampletime").longValue()));
        dto.setLowWatermark(entity.getJsonNumber("lowwatermark").longValue());
        dto.setName(entity.getJsonString("name").getString());
        dto.setDescription(entity.getJsonString("description").getString());
        dto.setHighWatermark(entity.getJsonNumber("highwatermark").longValue());
        dto.setStartTime(long2Date(entity.getJsonNumber("starttime").longValue()));

        return dto;
    }

    /**
     * Current Object értékek leszedése
     *
     * @param entity JSON entitás
     *
     * @return dto
     */
    private CurrentObjectValueDto getCurrentObjectValues(JsonObject entity) {
        CurrentObjectValueDto dto = new CurrentObjectValueDto();
        dto.setUnit(ValueUnitType.fromValue(entity.getJsonString("unit").getString()));
        dto.setCurrent(this.getCurrentObjectValue(entity));
        dto.setLastSampleTime(long2Date(entity.getJsonNumber("lastsampletime").longValue()));
        dto.setName(entity.getJsonString("name").getString());
        dto.setDescription(entity.getJsonString("description").getString());
        dto.setStartTime(long2Date(entity.getJsonNumber("starttime").longValue()));

        return dto;
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     *
     * @param entities JSon entitás
     *
     * @return értékek map
     */
    protected HashMap<String, ValueBaseDto> fetchValues(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        HashMap<String, ValueBaseDto> result = new LinkedHashMap<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject valueEntity = entities.getJsonObject(entityName);
            String unitName = valueEntity.getJsonString("unit").getString();

            if (unitName == null) {
                log.error("A(z) '{}' JSon entitásnak nincs 'unit' értéke!", entityName);
                continue;
            }

            ValueBaseDto dto = null;

            switch (ValueUnitType.fromValue(unitName)) {

                case SECONDS:
                case MILLISECOND:
                case NANOSECOND:
                case COUNT:
                case BYTES: {
                    dto = getQuantityValues(valueEntity);
                    break;
                }

                case STRING: {
                    dto = getCurrentObjectValues(valueEntity);
                    break;
                }

                case LIST:
                    log.warn("Nincs még a List típusra adatfelolvasás!");
                    break;

                default:
                    log.warn("Nincs lekezelve a JSon entitás, név: '{}', unit: '{}' !", entityName, unitName);
                    break;
            }

            if (dto != null) {
                dto.setUri(getUri());
                result.put(entityName, dto);
            }
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
     * @return Json entitás - értékek Map
     */
    @Override
    public HashMap<String/*JSon entityName*/, ValueBaseDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken) {

        Response response = restDataCollector.getMonitorResponse(getUri(), simpleUrl, sessionToken);
        JsonObject entities = restDataCollector.getJsonEntities(response);

        return this.fetchValues(entities);
    }

}
