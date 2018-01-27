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
public abstract class RestDataCollectorBase extends RestClientBase {

//// <editor-fold defaultstate="collapsed" desc="Típusos getterek">
//    public long getLong(String uri, String name) {
//        return getLong(uri, name, "count");
//    }
//
//    public int getInt(String uri, String name) {
//        return getInt(uri, name, "count");
//    }
//
//    public long getLong(String uri, String name, String key) {
//        Response result = this.getResponse(uri);
//        JsonObject jsonObject = super.getJsonEntityByName(result, name);
//        if (jsonObject == null) {
//            return 0L;
//        }
//        return jsonObject.getJsonNumber(key).longValue();
//    }
//
//    public int getInt(String uri, String name, String key) {
//        Response result = this.getResponse(uri);
//        JsonObject jsonObject = super.getJsonEntityByName(result, name);
//        if (jsonObject == null) {
//            return 0;
//        }
//        return jsonObject.getInt(key);
//    }
//
//    public String getString(String uri, String name, String key) {
//        Response result = this.getResponse(uri);
//        JsonObject jsonObject = super.getJsonEntityByName(result, name);
//        if (jsonObject == null) {
//            return null;
//        }
//        return jsonObject.getString(key);
//    }
//
//    public String[] getStringArray(String name, String key) {
//        String[] empty = new String[0];
//        Response result = this.getResponse(name);
//
//        JsonObject response = result.readEntity(JsonObject.class);
//        if (response == null) {
//            return empty;
//        }
//
//        response = response.getRootJsonObject("extraProperties");
//        if (response == null) {
//            return empty;
//        }
//
//        response = response.getRootJsonObject("childResources");
//        if (response == null) {
//            return empty;
//        }
//
//        int length = response.size();
//        String retVal[] = new String[length];
//        Iterator keyIterator = response.keySet().iterator();
//        int counter = 0;
//        while (keyIterator.hasNext()) {
//            retVal[counter++] = (String) keyIterator.next();
//        }
//        return retVal;
//    }
//// </editor-fold>
    /**
     * A szerver url-jéhez képest hol található a megszerzendő JSon adat?
     * A leszármazot implementálja
     *
     * @return sub uri
     */
    protected abstract String getSubUri();

    /**
     * A GF REST API alap URL-jének összeállítása
     *
     * @param simpleUrl a GF szerver url-je
     * @param userName  REST hívás usere
     *
     * @return url
     */
    private String getMonitorBaseURI(String simpleUrl, String userName) {
        return super.getProtocol(userName) + simpleUrl + getSubUri();
    }

    /**
     * REST válasz olvasása
     *
     * @param fullUrl      monitorozott rest teljes erőforrás URL
     * @param sessionToken a GF session token-je
     *
     * @return REST válasz
     */
    public Response getResponse(String fullUrl, String sessionToken) {
        WebTarget resource = client.target(fullUrl);
        Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

        //Cookie-ba eltesszük a session tokent
        if (!StringUtils.isEmpty(sessionToken)) {
            builder.cookie(new Cookie("gfresttoken", sessionToken));
        }
        return builder.get(Response.class);
    }

    /**
     * REST válasz olvasása
     *
     * @param resourceUri  monitorozott rest erőforrás URI
     * @param simpleUrl    a GF szerver url-je
     * @param userName     REST hívás usere
     * @param sessionToken a GF session token-je
     *
     * @return REST válasz
     */
    public Response getResponse(String resourceUri, String simpleUrl, String userName, String sessionToken) {

        String fullUrl = this.getMonitorBaseURI(simpleUrl, userName) + resourceUri;
        return this.getResponse(fullUrl, sessionToken);
    }
}
