/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationCollectorBase.java
 *  Created: 2018.01.21. 11:47:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application.server;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.IApplicationCollector;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Alkalmazás adatgyűjtés ős osztály
 * <p>
 * http://localhost:4848/monitoring/domain/server/applications/{appRealName}/???
 *
 * @author BT
 */
@Slf4j
public abstract class ApplicationCollectorBase extends CollectorBase implements IApplicationCollector {

    //private static final Pattern RESOURCE_URI_SUFFIX = Pattern.compile("^\\/?applications\\/(?<realAppName>[^\\/]+)\\/(?<suffix>.+)$");
    protected String tokenizedUri;

    /**
     * Csak az azonosításhoz kell, más jelentősége nincs
     *
     * @return
     */
    @Override
    public String getPath() {
        return tokenizedUri;
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @return az összegyűjtött adatok listájának map-je a kategóriával/hellyel kulcsolva
     */
    private List<CollectedValueDto> fetchValues(JsonObject entities) {

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

            //Path
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

        return resultList;
    }

    /**
     * REST path létrehozása tokenizált értékekből
     *
     * @param tokenizedUri tokenizálz Uri
     * @param uriParams    token paraméterek
     *
     * @return REST uri path
     */
    private String makeRestUriPath(String tokenizedUri, Map<String, String> uriParams) {

        String result = tokenizedUri;

        for (String paramName : uriParams.keySet()) {
            result = result.replace(paramName, uriParams.get(paramName));
        }

        return result;
    }

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param userName          A REST hívás usere
     * @param sessionToken      GF session token
     * @param tokenizedUri      monitorozott REST erőforrás uri: maszkolt REST uri
     * @param uriParams         maszk paraméterek
     *
     * @return application új entitás snapshotok listája
     *
     */
    @Override
    public List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken, String tokenizedUri, Map<String, String> uriParams) {

        this.tokenizedUri = tokenizedUri;
        log.trace("A letiltando path-ot majd kezelni!");
        JsonObject entities = super.getMonitoredEntities(restDataCollector, simpleUrl, userName, sessionToken, makeRestUriPath(tokenizedUri, uriParams), null);
        return this.fetchValues(entities);
    }

}
