/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IndependentRestClientBase.java
 *  Created: 2018.01.19. 9:51:03
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.rest.GFMonitorRestClient;
import java.io.Serializable;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;

/**
 * Önálló REST kliens ős osztály
 *
 * @author BT
 */
public abstract class IndependentRestClientBase implements Serializable {

    @Inject
    @GFMonitorRestClient
    private Client client;

    /**
     * A megadott subUrl-ről leszedi a válasz JSonObject-et
     *
     * @param simpleUrl    szerver url
     * @param subUrl       sub url
     * @param sessionToken session token
     *
     * @return válast jsonjObject
     */
    protected JsonObject getJsonObject(String simpleUrl, String subUrl, String sessionToken) {

        String protocol = StringUtils.isEmpty(sessionToken) ? IGFMonEngineConstants.PROTOCOL_HTTPS : IGFMonEngineConstants.PROTOCOL_HTTP;

        String fullUrl = protocol + simpleUrl + subUrl;

        WebTarget resource = client.target(fullUrl);
        Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

        //Cookie-ba eltesszük a session tokent
        if (!StringUtils.isEmpty(sessionToken)) {
            builder.cookie(new Cookie("gfresttoken", sessionToken));
        }

        Response restResponse = builder.get(Response.class);

        JsonObject jsonObject = restResponse.readEntity(JsonObject.class);

        return jsonObject;
    }
}
