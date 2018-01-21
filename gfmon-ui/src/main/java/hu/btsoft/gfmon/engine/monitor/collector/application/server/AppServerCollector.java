/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServerCollector.java
 *  Created: 2018.01.21. 11:47:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.server;

import hu.btsoft.gfmon.corelib.json.JsonUtils;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.IAppServerCollector;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * Alkalmazás Server adatgyűjtés
 * http://localhost:4848/monitoring/domain/server/applications/{appRealName}/server
 *
 * @author BT
 */
@Slf4j
public class AppServerCollector extends CollectorBase implements IAppServerCollector {

    private String appRealName;
    private String subPath;

    /**
     * Aktuális path az ősöknek
     */
    @Override
    public String getPath() {
        return "applications/" + appRealName + "/" + subPath;
    }

    /**
     * Map feltöltése, ha nem üres a lista
     *
     * @param map  mep
     * @param key  kulcs
     * @param list lista
     *
     * @return map
     */
    private Map<String, List<CollectedValueDto>> putMap(Map<String, List<CollectedValueDto>> map, String key, List<CollectedValueDto> list) {
        if (list != null && !list.isEmpty()) {
            if (map == null) {
                map = new HashMap<>();
            }
            map.put(key, list);
        }
        return map;
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @return az összegyűjtött adatok listájának map-je a kategóriával/hellyel kulcsolva
     */
    private Map<String, List<CollectedValueDto>> fetchValues(JsonObject entities, Map<String, List<CollectedValueDto>> resultMap, String mapKey) {

        if (entities == null) {
            return null;
        }

        List<CollectedValueDto> resultList = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

            String dataName = jsonValueEntity.getJsonString("name").getString();

            String unitName = jsonValueEntity.getJsonString("unit").getString();
            if (unitName == null) {
                log.error("A(z) '{}' JSon entitásnak nincs 'unit' értéke!", entityName);
                continue;
            }

            //Unit type kitalálása
            ValueUnitType valueUnitType = ValueUnitType.fromValue(jsonValueEntity.getJsonString("unit").getString());
            //Ha COUNT az unit, de van 'lowwatermark' és 'highwatermark' -> COUNT_CURLWHW lesz a típus
            if (valueUnitType == ValueUnitType.COUNT
                    && jsonValueEntity.getJsonNumber("lowwatermark") != null
                    && jsonValueEntity.getJsonNumber("highwatermark") != null) {
                valueUnitType = ValueUnitType.COUNT_CURLWHW;
            }

            CollectedValueDto dto = new CollectedValueDto();

            dto.setUnit(valueUnitType);
            dto.setLastSampleTime(long2Date(jsonValueEntity.getJsonNumber("lastsampletime").longValue()));
            dto.setName(dataName);
            dto.setStartTime(long2Date(jsonValueEntity.getJsonNumber("starttime").longValue()));
            dto.setPath(getPath());

            //Érték típushelyes leszedése
            switch (valueUnitType) {

                case SECONDS:
                case MILLISECOND:
                case NANOSECOND:
                case COUNT:
                case BYTES:
                    dto.setCount(jsonValueEntity.getJsonNumber("count").longValue());
                    break;

                case COUNT_CURLWHW:
                    JsonNumber jn = jsonValueEntity.getJsonNumber("current");
                    dto.setCurrent(jn.longValue());

                    dto.setLowWatermark(jsonValueEntity.getJsonNumber("lowwatermark").longValue());
                    dto.setHighWatermark(jsonValueEntity.getJsonNumber("highwatermark").longValue());
                    break;

                case LIST:
                case STRING:
                    dto.setCurrent(jsonValueEntity.getJsonString("current").getString());
                    break;

                default:
                    log.warn("Nincs lekezelve a JSon entitás, név: '{}', unit: '{}' !", entityName, unitName);
                    break;
            }

            resultList.add(dto);
        }

        return putMap(resultMap, mapKey, resultList);
    }

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param appRealName       az alkalmazás igazi nevével
     * @param subPath           appRealName-t követő subpath (server, server/jsp, server/Faces Servlet, ...
     * @param sessionToken      GF session token
     *
     * @return application új entitás snapshotok listája
     *
     */
    @Override
    public Map<String, List<CollectedValueDto>> execute(RestDataCollector restDataCollector, String simpleUrl, String appRealName, String subPath, String sessionToken) {

        this.appRealName = appRealName;
        this.subPath = subPath;
        Response response = restDataCollector.getMonitorResponse(getPath(), simpleUrl, sessionToken);
        JsonObject extraProperties = restDataCollector.getExtraProperties(response);

        Map<String, List<CollectedValueDto>> resultMap = null;
        resultMap = this.fetchValues(JsonUtils.getJsonEntities(extraProperties), resultMap, this.subPath);

        //Megnézzük, hoghy vannak-e gyermek objektumok, és jól lekérdezzük őket
        Set<String> childResourcesKeys = JsonUtils.getChildResourcesKeys(extraProperties);
        if (childResourcesKeys != null && !childResourcesKeys.isEmpty()) {
            for (String key : childResourcesKeys) {
                this.subPath = subPath + "/" + key;
                response = restDataCollector.getMonitorResponse(getPath(), simpleUrl, sessionToken);
                extraProperties = restDataCollector.getExtraProperties(response);
                resultMap = this.fetchValues(JsonUtils.getJsonEntities(extraProperties), resultMap, this.subPath);
            }
        }

        return resultMap;
    }

}
