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
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;
import javax.json.JsonObject;

/**
 * A GF REST interfészén kereztül adatokat gyűjtő kollektorok ős osztálya
 *
 * @author BT
 */
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
    protected QuantityValueDto getQuantityValues(JsonObject entity) {
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
    protected CurrentCountValueDto getCurrentCountValues(JsonObject entity) {
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
    protected CurrentObjectValueDto getCurrentObjectValues(JsonObject entity) {
        CurrentObjectValueDto dto = new CurrentObjectValueDto();
        dto.setUnit(entity.getJsonString("unit").getString());
        dto.setCurrent(this.getCurrentObjectValue(entity));
        dto.setLastSampleTime(long2Date(entity.getJsonNumber("lastsampletime").longValue()));
        dto.setName(entity.getJsonString("name").getString());
        dto.setDescription(entity.getJsonString("description").getString());
        dto.setStartTime(long2Date(entity.getJsonNumber("starttime").longValue()));
        return dto;
    }

}
