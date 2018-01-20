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

import hu.btsoft.gfmon.engine.monitor.IndependentRestClientBase;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import javax.json.JsonObject;

/**
 * Szerver uptime leszedése
 *
 * http://localhost:4848/management/domain/uptime
 *
 * @author BT
 */
public class ServerUptime extends IndependentRestClientBase {

    private static final String SUB_URL = "/management/domain/uptime";

    /**
     * GF Szerver verzió adatok kigyűjtése
     *
     * @param simpleUrl a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány verzió adatai
     */
    public String getServerUptime(String simpleUrl, String sessionToken) {

        //Válasz leszedése
        JsonObject jsonObject = super.getJsonObject(simpleUrl, SUB_URL, sessionToken);
        if (jsonObject == null) {
            return null;
        }

        //extraProperties leszedése
        JsonObject properties = jsonObject.getJsonObject("properties");
        if (properties == null) {
            return null;
        }

        //Értékek leszedése
        String strMs = properties.getJsonString("milliseconds").getString();
        return Elapsed.getMilliStr(Long.parseLong(strMs));

    }
}
