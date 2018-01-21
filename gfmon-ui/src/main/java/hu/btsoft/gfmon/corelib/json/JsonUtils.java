/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JsonUtils.java
 *  Created: 2018.01.21. 16:03:07
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Slf4j
public class JsonUtils {

    /**
     * A JSon válaszból az extraProperties leszedése
     *
     * @param jsonObject
     *
     * @return
     */
    public static JsonObject getExtraProperties(JsonObject jsonObject) {

        if (jsonObject == null) {
            return null;
        }

        return jsonObject.getJsonObject("extraProperties");
    }

    /**
     * A JSon válaszból az extraProperties/childResources leszedése
     *
     * @param extraProperties
     *
     * @return
     */
    public static JsonObject getChildResources(JsonObject extraProperties) {

        JsonObject retVal = null;

        if (extraProperties != null) {
            retVal = extraProperties.getJsonObject("childResources");
        } else {
            log.trace("null az extraProperties JSonObject!");
        }

        return retVal;

    }

    /**
     * JSon entities leszedése a Response-ról
     *
     * @param extraProperties
     *
     * @return
     */
    public static JsonObject getJsonEntities(JsonObject extraProperties) {

        JsonObject retVal = null;

        if (extraProperties != null) {
            retVal = extraProperties.getJsonObject("entity");
        } else {
            log.trace("null az extraProperties JSonObject!");
        }

        return retVal;
    }

    /**
     * A JSon válaszból az extraProperties/entity/{name} leszedése
     *
     * @param jsonObject
     * @param name       entity név
     *
     * @return entity JSon
     */
    public static JsonObject getJsonObject(JsonObject jsonObject, String name) {
        JsonObject retVal = null;

        JsonObject extraProperties = getExtraProperties(jsonObject);
        if (extraProperties != null) {
            JsonObject jsonEntities = getJsonEntities(extraProperties);
            if (jsonEntities != null) {
                retVal = jsonEntities.getJsonObject(name);
            } else {
                log.info("null az entity érték!");
            }
        } else {
            log.info("null az extraproperties érték!");
        }

        log.info(String.format("JsonObject Name: %s, retVal :%s (result: %s)", name, retVal, jsonObject));
        return retVal;
    }

    /**
     * A childresources szintről leszedi a tömb kulcsait
     *
     * @param extraProperties
     *
     * @return tömb vagy null
     */
    public static Set<String> getChildResourcesKeys(JsonObject extraProperties) {

        JsonObject childResources = getChildResources(extraProperties);
        return childResources != null ? childResources.keySet() : null;
    }

    /**
     * A childresources szintről leszedi a tömb kulcsait
     *
     * @param jsonObject
     *
     * @return tömb vagy null
     */
    public static Map<String, String> getChildResourcesMap(JsonObject jsonObject) {

        JsonObject childResources = getChildResources(jsonObject);
        if (childResources == null) {
            return null;
        }

        Map<String, String> map = new LinkedHashMap<>();
        childResources.keySet().forEach((key) -> {
            map.put(key, childResources.getString(key));
        });

        return map;
    }

}
