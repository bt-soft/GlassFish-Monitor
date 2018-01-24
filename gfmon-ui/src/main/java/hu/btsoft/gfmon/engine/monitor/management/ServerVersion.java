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
package hu.btsoft.gfmon.engine.monitor.management;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.engine.rest.RestClientBase;
import java.util.HashMap;
import java.util.Map;
import javax.json.JsonObject;

/**
 * Szerver verzió leszedése
 * <p>
 * http://localhost:4848/management/domain/version
 *
 * @author BT
 */
public class ServerVersion extends RestClientBase {

    private static final String SUB_URL = "/management/domain/version";

    /**
     * GF Szerver verzió adatok kigyűjtése
     *
     * @param simpleUrl    a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány verzió adatai
     */
    public Map<String, String> getServerVersionInfo(String simpleUrl, String sessionToken) {

        //Válasz leszedése
        JsonObject extraProperties = GFJsonUtils.getExtraProperties(super.getRootJsonObject(simpleUrl, SUB_URL, sessionToken));
        if (extraProperties == null) {
            return null;
        }

        //Értékek leszedése
        Map<String, String> result = new HashMap<>();
        result.put("version", extraProperties.getJsonString("version").getString());
        result.put("version-number", extraProperties.getJsonString("version-number").getString());
        result.put("full-version", extraProperties.getJsonString("full-version").getString());

        return result;
    }
}
