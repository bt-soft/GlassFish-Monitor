/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RestDataCollectorBase.java
 *  Created: 2017.12.24. 16:24:00
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.rest;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.util.Iterator;
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
 *
 * @author BT
 */
@Slf4j
public abstract class RestDataCollectorBase {

    /**
     * REST kliens
     */
    @Inject
    @GFMonitorRestClient
    private Client client;

    /**
     * A GF szerver url-je
     */
    protected String simpleUrl;

    /**
     * GF session token
     */
    protected String sessionToken;

    // <editor-fold defaultstate="collapsed" desc="Típusos getterek">
    public long getLong(String uri, String name) {
        return getLong(uri, name, "count");
    }

    public int getInt(String uri, String name) {
        return getInt(uri, name, "count");
    }

    public long getLong(String uri, String name, String key) {
        Response result = this.getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return 0L;
        }
        return jsonObject.getJsonNumber(key).longValue();
    }

    public int getInt(String uri, String name, String key) {
        Response result = this.getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return 0;
        }
        return jsonObject.getInt(key);
    }

    public String getString(String uri, String name, String key) {
        Response result = this.getMonitorResponse(uri);
        JsonObject jsonObject = getJsonObject(result, name);
        if (jsonObject == null) {
            return null;
        }
        return jsonObject.getString(key);
    }

    public String[] getStringArray(String name, String key) {
        String[] empty = new String[0];
        Response result = this.getMonitorResponse(name);

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
     * A JSon válaszból az extraProperties leszedése
     *
     * @param result
     *
     * @return
     */
    public JsonObject getExtraProperties(Response result) {

        if (result == null) {
            return null;
        }

        JsonObject response = result.readEntity(JsonObject.class);
        if (response == null) {
            return null;
        }

        JsonObject extraProperties = response.getJsonObject("extraProperties");
        return extraProperties;
    }

    /**
     * A JSon válaszból az extraProperties/childResources leszedése
     *
     * @param result
     *
     * @return
     */
    public JsonObject getChildResources(Response result) {

        JsonObject retVal = null;

        JsonObject extraProperties = this.getExtraProperties(result);
        if (extraProperties != null) {
            retVal = extraProperties
                    .getJsonObject("childResources");
        } else {
            log.trace("null az extraProperties JSonObject!");
        }

        return retVal;

    }

    /**
     * JSon entities leszedése a Response-ról
     *
     * @param result
     *
     * @return
     */
    public JsonObject getJsonEntities(Response result) {

        JsonObject retVal = null;

        JsonObject extraProperties = this.getExtraProperties(result);
        if (extraProperties != null) {
            retVal = extraProperties
                    .getJsonObject("entity");
        } else {
            log.trace("null az extraProperties JSonObject!");
        }

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

        JsonObject extraProperties = this.getExtraProperties(result);
        if (extraProperties != null) {
            JsonObject jsonEntities = this.getJsonEntities(result);
            if (jsonEntities != null) {
                retVal = jsonEntities.getJsonObject(name);
            } else {
                log.info("null az entity érték!");
            }
        } else {
            log.info("null az extraproperties érték!");
        }

        log.info(String.format("JsonObject Name: %s, retVal :%s (result: %s)", name, retVal, result));
        return retVal;
    }

    /**
     * A childresources szintről leszedi a tömb kulcsait
     *
     * @param result JSO válasz
     *
     * @return tömb vagy null
     */
    public Set<String> getChildResourcesKeys(Response result) {

        JsonObject childResources = getChildResources(result);
        return childResources != null ? childResources.keySet() : null;
    }

    // </editor-fold>
    /**
     * A GF REST API alap URL-jének összeállítása
     *
     * @return
     */
    private String getMonitorBaseURI() {

        //A protokoll megállapításánál a sessionTokent kell figyelni (nem az usernevet)
        String protocol = StringUtils.isEmpty(sessionToken) ? IGFMonEngineConstants.PROTOCOL_HTTPS : IGFMonEngineConstants.PROTOCOL_HTTP;

        return protocol + simpleUrl + getSubUri();
    }

    /**
     * REST válasz olvasása
     *
     * @param uri          monitorozott rest erőforrás URI
     * @param simpleUrl
     * @param sessionToken
     *
     * @return
     */
    /**
     * REST válasz olvasása
     *
     * @param uri monitorozott rest erőforrás URI
     *
     * @return REST válasz
     */
    protected Response getMonitorResponse(String uri) {

        String fullUrl = this.getMonitorBaseURI() + uri;
        WebTarget resource = client.target(fullUrl);
        Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

        //Cookie-ba eltesszük a session tokent
        if (!StringUtils.isEmpty(sessionToken)) {
            builder.cookie(new Cookie("gfresttoken", sessionToken));
        }
        return builder.get(Response.class);
    }

    /**
     * A szerver url-jéhez képest hol tatlálható a megszerzendő JSon adat
     *
     * @return sub uri
     */
    protected abstract String getSubUri();
}
