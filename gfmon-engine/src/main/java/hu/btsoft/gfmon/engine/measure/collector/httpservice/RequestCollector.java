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
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import java.util.HashMap;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

/**
 * JVM/Memory adatok gyűjtése
 *
 * @author BT
 */
public class RequestCollector extends CollectorBase {

    public static final String URI = "server/http-service/server/request";

    /**
     * REST JSon monitor adatok összegyűjtése
     *
     * @param collector REST adatgyűjtő
     *
     * @return
     */
    @Override
    public HashMap<String, ValueBaseDto> apply(RestDataCollector collector) {

        Response response = collector.getMonitorResponse(URI);
        JsonObject entities = collector.getJsonEntities(response);

        return super.fetchValues(entities, URI);
    }

}
