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

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.rest.RestClientBase;
import javax.json.JsonObject;

/**
 * Szerver uptime leszedése
 * <p>
 * http://localhost:4848/management/domain/uptime
 *
 * @author BT
 */
public class ServerUptime extends RestClientBase {

    private static final String SUB_URL = "/management/domain/uptime";

    /**
     * GF Szerver verzió adatok kigyűjtése
     *
     * @param simpleUrl    a GF szerver URL-je
     * @param user         rest user
     * @param sessionToken session token
     *
     * @return A GF példány verzió adatai
     */
    public String getServerUptime(String simpleUrl, String user, String sessionToken) {

        //Válasz leszedése
        JsonObject rootJsonObject = super.getRootJsonObject(simpleUrl, SUB_URL, user, sessionToken);
        if (rootJsonObject != null) {
            JsonObject properties = rootJsonObject.getJsonObject("properties");
            if (properties
                    != null) {
                //Értékek leszedése
                String strMs = properties.getJsonString("milliseconds").getString();
                return Elapsed.getMilliStr(Long.parseLong(strMs));
            }

        }
        return null;
    }
}
