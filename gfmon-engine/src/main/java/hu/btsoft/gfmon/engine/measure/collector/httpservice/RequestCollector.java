/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RequestCollector.java
 *  Created: 2017.12.24. 17:21:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.httpservice;

import hu.btsoft.gfmon.engine.measure.collector.CollectorBase;
import hu.btsoft.gfmon.engine.measure.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

/**
 * JVM/Memory adatok gyűjtése
 *
 * @author BT
 */
public class RequestCollector extends CollectorBase {

    public static final String URI = "server/http-service/server/request";
    private static final String[] ENTITY_NAMES = {
        "count200",
        "count2xx",
        "count302",
        "count304",
        "count3xx",
        "count400",
        "count401",
        "count403",
        "count4xx",
        "count503",
        "count5xx",
        "countopenconnections",
        "countother",
        "countrequests",
        "errorcount",
        "maxopenconnections",
        "maxtime",
        "processingtime"
    };

    private static final String[] ENTITY_NAMES_CURRENTOBJECT = {
        "method",
        "uri"
    };

    /**
     * REST JSon monitor adatok összegyűjtése
     *
     * @param collector REST adatgyűjtő
     *
     * @return
     */
    @Override
    public HashMap<String, ValueBaseDto> apply(RestDataCollector collector) {

        HashMap<String, ValueBaseDto> result = new LinkedHashMap<>();

        Response response = collector.getMonitorResponse(URI);
        JsonObject entities = collector.getJsonEntities(response);

        if (entities == null) {
            return null;
        }

        for (String entityName : ENTITY_NAMES) {
            JsonObject entity = entities.getJsonObject(entityName);
            QuantityValueDto dto = super.getQuantityValues(entity);
            dto.setUri(URI);
            result.put(entityName, dto);
        }
        for (String entityName : ENTITY_NAMES_CURRENTOBJECT) {
            JsonObject entity = entities.getJsonObject(entityName);
            CurrentObjectValueDto dto = super.getCurrentObjectValues(entity);
            dto.setUri(URI);
            result.put(entityName, dto);
        }

        return result;
    }

}
