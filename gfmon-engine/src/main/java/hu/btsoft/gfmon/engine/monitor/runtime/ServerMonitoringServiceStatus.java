/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerMonitoringServiceStatus.java
 *  Created: 2017.12.24. 18:31:11
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.runtime;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Ellenőrzi az adott GF példány MonitoringService beállításait
 *
 * pl.: http://localhost:4848/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels
 *
 * @author BT
 */
@Slf4j
public class ServerMonitoringServiceStatus {

    private static final String MONITORING_SERVICE_URI = "/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels";

    @Inject
    private Client client;

    /**
     * GF module-monitoring-levels Attributes kigyűjtése
     *
     * @param simpleUrl    a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány monitorozható GF moduljainak halmaza, vagy null, ha nincs egy sem
     */
    public Set<String/*GF MoitoringService module name*/> checkMonitorStatus(String simpleUrl, String sessionToken) {

        String protocol = StringUtils.isEmpty(sessionToken) ? IGFMonEngineConstants.PROTOCOL_HTTPS : IGFMonEngineConstants.PROTOCOL_HTTP;

        String fullUrl = protocol + simpleUrl + MONITORING_SERVICE_URI;

        WebTarget resource = client.target(fullUrl);
        Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

        //Cookie-ba eltesszük a session tokent
        if (!StringUtils.isEmpty(sessionToken)) {
            builder.cookie(new Cookie("gfresttoken", sessionToken));
        }

        Response restResponse = builder.get(Response.class);

        JsonObject jsonObject = restResponse.readEntity(JsonObject.class);
        if (jsonObject == null) {
            return null;
        }

        //extraProperties leszedése
        JsonObject extraProperties = jsonObject.getJsonObject("extraProperties");
        if (extraProperties == null) {
            return null;
        }

        Set<String/*GF MoitoringService module name*/> result = null;

        //entity leszedése
        JsonObject entities = extraProperties.getJsonObject("entity");

        //Keresünk egy olyan beállítást, ami nem "OFF" -> van mit monitorozni
        for (String key : entities.keySet()) {
            String value = entities.getJsonString(key).getString();
            if (!"OFF".equalsIgnoreCase(value)) {
                if (result == null) {
                    result = new HashSet<>();
                }
                result.add(key);
            }
        }

        return result;
    }

}
