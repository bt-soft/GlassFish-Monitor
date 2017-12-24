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
package hu.btsoft.gfmon.engine.measure.collector;

import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentCountValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import static hu.btsoft.gfmon.engine.measure.collector.httpservice.RequestCollector.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Function;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * A GF REST interfészén kereztül adatokat gyűjtő kollektorok ős osztálya
 *
 * @author BT
 */
@Slf4j
public abstract class CollectorBase implements Function<RestDataCollector, HashMap<String/*entityName*/, ValueBaseDto>> {

    /**
     * Timestamp -> Date konverzió
     *
     * @param value dátum timestamp formátumban
     *
     * @return java Date objektum vagy null
     */
    private Date long2Date(long value) {

        Date result = null;

        try {
            result = new Date(value);
        } catch (Exception ignored) {
            //
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
        String unit = entity.getJsonString("unit").getString();

        Object value;
        switch (unit) {

            case "String":
                value = entity.getJsonString("current").getString();
                break;

            case "millisecond":
            case "count":
            case "bytes":
                value = entity.getJsonNumber("current").longValue();
                break;

            default:
                value = "*unknown*";
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

        dto.setUnit(entity.getJsonString("unit").getString());
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
        dto.setUnit(entity.getJsonString("unit").getString());
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
        dto.setUnit(entity.getJsonString("unit").getString());
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
     * @param uri      honnan származik?
     *
     * @return értékek map
     */
    protected HashMap<String, ValueBaseDto> fetchValues(JsonObject entities, String uri) {

        if (entities == null) {
            return null;
        }

        HashMap<String, ValueBaseDto> result = new LinkedHashMap<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject valueEntity = entities.getJsonObject(entityName);
            String unit = valueEntity.getJsonString("unit").getString();

            if (unit == null) {
                log.error("A(z) '{}' JSon entitásnak nincs 'unit' értéke!", entityName);
                continue;
            }

            switch (unit) {

                case "count": {
                    QuantityValueDto dto = getQuantityValues(valueEntity);
                    dto.setUri(uri);
                    result.put(entityName, dto);
                    break;
                }

                case "string": {
                    CurrentObjectValueDto dto = getCurrentObjectValues(valueEntity);
                    dto.setUri(URI);
                    result.put(entityName, dto);
                    break;
                }

                default:
                    log.warn("Nincs lekezelve a JSon entitás, név: '{}', unit: '{}' !", entityName, unit);
                    break;
            }
        }

        return result;
    }

}
