/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    RestClientBase.java
 *  Created: 2018.01.24. 9:59:14
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.rest;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
 * REST kliens ős osztály
 *
 * @author BT
 */
@Slf4j
public abstract class RestClientBase implements Serializable {

    /**
     * REST kliens
     */
    @Inject
    @GFMonitorRestClient
    protected Client client;

//// <editor-fold defaultstate="collapsed" desc="Response -> JsonObject getterek">
//    /**
//     * A JSon válaszból az extraProperties leszedése
//     *
//     * @param result
//     *
//     * @return
//     */
//    public JsonObject getExtraProperties(Response result) {
//
//        if (result == null) {
//            return null;
//        }
//
//        JsonObject response = result.readEntity(JsonObject.class);
//        if (response == null) {
//            return null;
//        }
//
//        JsonObject extraProperties = response.getJsonObject("extraProperties");
//        return extraProperties;
//    }
//
//    /**
//     * A JSon válaszból az extraProperties/childResources leszedése
//     *
//     * @param result
//     *
//     * @return
//     */
//    public JsonObject getChildResources(Response result) {
//
//        JsonObject retVal = null;
//
//        JsonObject extraProperties = this.getExtraProperties(result);
//        if (extraProperties != null) {
//            retVal = extraProperties.getJsonObject("childResources");
//        } else {
//            log.trace("null az extraProperties JSonObject!");
//        }
//
//        return retVal;
//
//    }
//
//    /**
//     * JSon entities leszedése a Response-ról
//     *
//     * @param result
//     *
//     * @return
//     */
//    public JsonObject getJsonEntities(Response result) {
//
//        JsonObject retVal = null;
//
//        JsonObject extraProperties = this.getExtraProperties(result);
//        if (extraProperties != null) {
//            retVal = extraProperties.getJsonObject("entity");
//        } else {
//            log.trace("null az extraProperties JSonObject!");
//        }
//
//        return retVal;
//    }
//
//    /**
//     * A JSon válaszból az extraProperties/entity/{name} leszedése
//     *
//     * @param result REST JSON result
//     * @param name   entity név
//     *
//     * @return entity JSon
//     */
//    public JsonObject getJsonEntityByName(Response result, String name) {
//        JsonObject retVal = null;
//
//        JsonObject extraProperties = this.getExtraProperties(result);
//        if (extraProperties != null) {
//            JsonObject jsonEntities = extraProperties.getJsonObject("entity");
//            if (jsonEntities != null) {
//                retVal = jsonEntities.getJsonObject(name);
//            } else {
//                log.info("null az entity érték!");
//            }
//        } else {
//            log.info("null az extraproperties érték!");
//        }
//
//        log.info(String.format("JsonObject Name: %s, retVal :%s (result: %s)", name, retVal, result));
//        return retVal;
//    }
//
//    /**
//     * A childresources szintről leszedi a tömb kulcsait
//     *
//     * @param result JSO válasz
//     *
//     * @return tömb vagy null
//     */
//    public Set<String> getChildResourcesKeys(Response result) {
//
//        JsonObject childResources = getChildResources(result);
//        return childResources != null ? childResources.keySet() : null;
//    }
//
//    /**
//     * A childresources szintről leszedi a tömb kulcsait
//     *
//     * @param result JSO válasz
//     *
//     * @return tömb vagy null
//     */
//    public Map<String, String> getChildResourcesMap(Response result) {
//
//        JsonObject childResources = getChildResources(result);
//        if (childResources == null) {
//            return null;
//        }
//
//        Map<String, String> map = new LinkedHashMap<>();
//        childResources.keySet().forEach((key) -> {
//            map.put(key, childResources.getString(key));
//        });
//
//        return map;
//    }
// </editor-fold>
    /**
     * Protokol kitalálása
     * Ha nincs user, akkor sima http
     *
     * @param userName REST hívás usere
     *
     * @return http/https
     */
    protected String getProtocol(String userName) {
        return StringUtils.isEmpty(userName) ? IGFMonEngineConstants.PROTOCOL_HTTP : IGFMonEngineConstants.PROTOCOL_HTTPS;
    }

    /**
     * A megadott full url-ről leszedi a választ
     *
     * @param fullUrl      teljes URL (http[s]://localhost:4848/....)
     * @param userName     REST hívás usere
     * @param sessionToken GF session token
     *
     * @return Json Object
     */
    public JsonObject getRootJsonObject(String fullUrl, String userName, String sessionToken) {

        try {
            String protocol = this.getProtocol(userName);

            //A protokol lecserélése, ha szükséges
            URL url = new URL(fullUrl);
            if (!fullUrl.startsWith(protocol)) {
                url = new URL(protocol, url.getHost(), url.getPort(), url.getFile());
            }

            WebTarget resource = client.target(url.toURI());
            Invocation.Builder builder = resource.request(MediaType.APPLICATION_JSON);

            //Cookie-ba eltesszük a session tokent
            if (!StringUtils.isEmpty(sessionToken)) {
                builder.cookie(new Cookie("gfresttoken", sessionToken));
            }

            Response response = builder.get(Response.class);
            if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
                //Nem jött válasz -> valószínűleg nincs engedélyezve az alkalmazás
                log.warn("A(z) '{}' url hívására {} hibakód jött", fullUrl, response.getStatusInfo().getReasonPhrase());
                return null;
            }

            JsonObject jsonObject = response.readEntity(JsonObject.class);

            return jsonObject;
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("URL hiba", e);
        }

        return null;
    }

    /**
     * A megadott subUrl-ről leszedi a válasz JSonObject-et
     *
     * @param simpleUrl    szerver url
     * @param subUrl       sub url
     * @param userName     REST hívás usere
     * @param sessionToken GF session token
     *
     * @return válast jsonjObject
     */
    public JsonObject getRootJsonObject(String simpleUrl, String subUrl, String userName, String sessionToken) {
        String fullUrl = this.getProtocol(userName) + simpleUrl + subUrl;
        return this.getRootJsonObject(fullUrl, userName, sessionToken);
    }

}
