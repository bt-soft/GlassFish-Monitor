/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    GFJsonUtils.java
 *  Created: 2018.01.21. 16:03:07
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.json.JsonObject;
import javax.json.JsonString;
import lombok.extern.slf4j.Slf4j;

/**
 * GlassFish JsonUtils
 *
 * @author BT
 */
@Slf4j
public class GFJsonUtils {

    public static String getRootAttributeValue(JsonObject root, String attributeName) {
        if (root != null) {
            JsonString jsonString = root.getJsonString("message");
            if (jsonString != null) {
                return jsonString.getString();
            }
        }
        return null;
    }

    /**
     * A message leszedése
     *
     * @param root teljes Json üzenet
     *
     * @return érték vagy null
     */
    public static String getMessage(JsonObject root) {
        return getRootAttributeValue(root, "message");
    }

    /**
     * A command leszedése
     *
     * @param root teljes Json üzenet
     *
     * @return érték vagy null
     */
    public static String getCommand(JsonObject root) {
        return getRootAttributeValue(root, "command");
    }

    /**
     * A exit_code leszedése
     *
     * @param root teljes Json üzenet
     *
     * @return érték vagy null
     */
    public static String getExitCode(JsonObject root) {
        return getRootAttributeValue(root, "exit_code");
    }

    /**
     * JSon root válaszból kiszedi az extraProperties értéket
     *
     * @param root root json
     *
     * @return json extraProperties, vagy null
     */
    public static JsonObject getExtraProperties(JsonObject root) {
        if (root != null) {
            return root.getJsonObject("extraProperties");
        }
        return null;
    }

    /**
     * JSon root válaszból kiszedi az extraProperties/entity értéket
     *
     * @param root root json
     *
     * @return json extraProperties, vagy null
     */
    public static JsonObject getEntities(JsonObject root) {
        JsonObject extraProperties = getExtraProperties(root);
        if (extraProperties != null) {
            return extraProperties.getJsonObject("entity");
        }
        return null;
    }

    /**
     * A JSon válaszból az extraProperties/entity/{name} leszedése
     *
     * @param root       root json
     * @param entityName entity név
     *
     * @return entity JSon, vagy null
     */
    public static JsonObject getEntityByName(JsonObject root, String entityName) {

        JsonObject entity = getEntities(root);
        if (entity != null) {
            return entity.getJsonObject(entityName);
        }
        return null;
    }

    /**
     * JSon root válaszból kiszedi az extraProperties/getChildResources értéket
     *
     * @param root root json
     *
     * @return json extraProperties, vagy null
     */
    public static JsonObject getChildResources(JsonObject root) {
        JsonObject extraProperties = getExtraProperties(root);
        if (extraProperties != null) {
            return extraProperties.getJsonObject("childResources");
        }
        return null;
    }

    /**
     * A childresources szintről leszedi a tömb kulcsait
     *
     * @param root root json
     *
     * @return tömb vagy null
     */
    public static Set<String> getChildResourcesKeys(JsonObject root) {
        JsonObject childResources = getChildResources(root);
        return childResources != null ? childResources.keySet() : null;
    }

    /**
     * A childresources szintről leszedi a tömb kulcsait
     *
     * @param root root json
     *
     * @return tömb vagy null
     */
    public static Map<String, String> getChildResourcesMap(JsonObject root) {

        JsonObject childResources = getChildResources(root);
        if (childResources == null) {
            return null;
        }

        Map<String, String> map = new LinkedHashMap<>();
        childResources.keySet().forEach((key) -> {
            map.put(key, childResources.getString(key));
        });

        return map;
    }

    /**
     * A Childresources-ből kiszedi a 'name' értékét
     *
     * @param root root json
     * @param name keresett név
     *
     * @return Stringes érték vagy null
     */
    public static String getChildResourcesValueByName(JsonObject root, String name) {
        Map<String, String> childResourcesMap = getChildResourcesMap(root);
        if (childResourcesMap != null && childResourcesMap.containsKey(name)) {
            return childResourcesMap.get(name);
        }

        return null;
    }
}
