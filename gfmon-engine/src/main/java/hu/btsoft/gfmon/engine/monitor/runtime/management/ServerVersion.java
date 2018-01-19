/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerUptime.java
 *  Created: 2018.01.19. 9:44:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.runtime.management;

import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;

/**
 * Szerver verzió leszedése
 *
 * http://localhost:4848/management/domain/version
 *
 * @author BT
 */
public class ServerVersion extends ManagementBase {

    private static final String SUB_URL = "/management/domain/version";

    /**
     * GF Szerver verzió adatok kigyűjtése
     *
     * @param simpleUrl a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány verzió adatai
     */
    public Map<String, String> getServerVersionInfo(String simpleUrl, String sessionToken) {

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

        //entity leszedése
        JsonObject jsonValueEntity = extraProperties.getJsonObject("entity");

        Map<String, String> result = new HashMap<>();

        //Értékek leszedése
        result.put("version", jsonValueEntity.getJsonString("version").getString());
        result.put("version-number", jsonValueEntity.getJsonString("version-number").getString());
        result.put("full-version", jsonValueEntity.getJsonString("full-version").getString());

        return result;
    }
}
