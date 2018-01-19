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
package hu.btsoft.gfmon.engine.monitor.management;

import java.util.HashSet;
import java.util.Set;
import javax.json.JsonObject;

/**
 * Ellenőrzi az adott GF példány MonitoringService beállításait
 *
 * pl.: http://localhost:4848/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels
 *
 * @author BT
 */
public class ServerMonitoringServiceStatus extends ManagementBase {

    private static final String SUB_URL = "/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels";

    /**
     * GF module-monitoring-levels Attributes kigyűjtése
     *
     * @param simpleUrl a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány monitorozható GF moduljainak halmaza, vagy null, ha nincs egy sem
     */
    public Set<String> checkMonitorStatus(String simpleUrl, String sessionToken) {

        //Válasz leszedése
        JsonObject jsonObject = super.getJsonObject(simpleUrl, SUB_URL, sessionToken);
        if (jsonObject == null) {
            return null;
        }

        //extraProperties leszedése
        JsonObject extraProperties = jsonObject.getJsonObject("extraProperties");
        if (extraProperties == null) {
            return null;
        }

        Set<String> result = null;

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
