/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RestDataCollector.java
 *  Created: 2017.12.24. 16:24:00
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.util.Iterator;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author BT
 */
@Slf4j
public class RestDataCollector {

    @Inject
    private Client client;

    @Setter
    private String simpleUrl;

    @Setter
    private String sessionToken;

    // <editor-fold defaultstate="collapsed" desc="Típusos getterek">
    public long getLong(String uri, String name) {
        return getLong(uri, name, "count");
    }

    public int getInt(String uri, String name) {
        return getInt(uri, name, "count");
    }

    public long getLong(String uri, String name, String key) {
        Response result = getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return 0L;
        }
        return jsonObject.getJsonNumber(key).longValue();
    }

    public int getInt(String uri, String name, String key) {
        Response result = getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return 0;
        }
        return jsonObject.getInt(key);
    }

    public String getString(String uri, String name, String key) {
        Response result = getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getString(key);
    }

    public String[] getStringArray(String name, String key) {
        String[] empty = new String[0];
        Response result = getMonitorResponse(name);

        JsonObject response = result.readEntity(JsonObject.class);
        if (response == null) {
            return empty;
        }

        response = response.getJsonObject("extraProperties");
        if (response == null) {
            return empty;
        }

        response = response.getJsonObject("childResources");
        if (response == null) {
            return empty;
        }

        int length = response.size();
        String retVal[] = new String[length];
        Iterator keyIterator = response.keySet().iterator();
        int counter = 0;
        while (keyIterator.hasNext()) {
            retVal[counter++] = (String) keyIterator.next();
        }
        return retVal;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="JsonObject getterek">
    /**
     * JSon entities leszedése a Response-ról
     *
     * @param result
     *
     * @return
     */
    public JsonObject getJsonEntities(Response result) {

        JsonObject retVal = null;

        if (result == null) {
            return retVal;
        }

        JsonObject response = result.readEntity(JsonObject.class);
        if (response == null) {
            return retVal;
        }

        JsonObject extraProperties = response.getJsonObject("extraProperties");
        if (extraProperties != null) {
            retVal = extraProperties
                    .getJsonObject("entity");
        } else {
            log.trace("null a vett JSonObject!");
        }

        log.trace("JsonObject Entities - retVal: {} (result: {})", retVal, result);

        return retVal;
    }

    /**
     * A JSon válaszból az extraProperties/entity/{name} leszedése
     *
     * @param result REST JSON result
     * @param name   entity név
     *
     * @return entity JSon
     */
    public JsonObject getJsonObject(Response result, String name) {
        JsonObject retVal = null;

        if (result == null) {
            return retVal;
        }

        JsonObject response = result.readEntity(JsonObject.class);
        if (response == null) {
            return retVal;
        }

        JsonObject extraProperties = response.getJsonObject("extraProperties");
        if (extraProperties != null) {
            retVal = extraProperties
                    .getJsonObject("entity")
                    .getJsonObject(name);
        } else {
            log.info("Null JSonObject vétel!");
        }

        log.info(String.format("JsonObject Name: %s, retVal :%s (result: %s)", name, retVal, result));
        return retVal;
    }
    // </editor-fold>

    /**
     * A protokoll megállapításánál a sessionTokent kell figyelni (nem az usernevet)
     *
     * @return http/https
     */
    private String getProtocol() {
        return !StringUtils.isEmpty(sessionToken) ? IGFMonEngineConstants.PROTOCOL_HTTPS : IGFMonEngineConstants.PROTOCOL_HTTP;
    }

    /**
     * A GF REST API alap URL-jének összeállítása
     *
     * @return
     */
    private String getMonitorBaseURI() {
        return getProtocol() + simpleUrl + "/monitoring/domain/server/";
    }

    /**
     * REST válasz olvasása
     *
     * @param uri monitorozott rest erőforrás URI
     *
     * @return
     */
    public Response getMonitorResponse(String uri) {

        String fullUrl = this.getMonitorBaseURI() + uri;
        WebTarget resource = client.target(fullUrl);
        Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

        //Cookie-ba eltesszük a session tokent
        if (!StringUtils.isEmpty(sessionToken)) {
            builder.cookie(new Cookie("gfresttoken", sessionToken));
        }
        return builder.get(Response.class);

    }

}
