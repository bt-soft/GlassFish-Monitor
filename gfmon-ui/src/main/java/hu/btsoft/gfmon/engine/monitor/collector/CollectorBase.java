/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    CollectorBase.java
 *  Created: 2018.01.21. 11:09:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.monitor.RestPathToJpaEntityClassMap;
import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Slf4j
public abstract class CollectorBase implements ICollectorBase {

    /**
     * Timestamp -> Date konverzió
     *
     * @param value dátum timestamp formátumban
     *
     * @return java Date objektum vagy null
     */
    protected Date long2Date(long value) {

        if (value == -1) {
            return null;
        }

        Date result = null;
        try {
            result = new Date(value);
        } catch (Exception e) {
            log.warn("Dátum konverziós hiba: {}", value, e);
        }
        return result;
    }

    /**
     * A Json válasz rendben van?
     *
     * @param jsonObject
     *
     * @return true -> igen
     */
    private boolean isSuccessJsonResponse(JsonObject jsonObject) {
        String exitCode = jsonObject.getString("exit_code");
        return ("SUCCESS".equals(exitCode));
    }

    /**
     * A REST válaszokból kinyeri az adatneveket és leírásukat
     *
     * @param entities JSon entitás
     *
     * @return adatneves leírása
     */
    public List<DataUnitDto> fetchDataUnits(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<DataUnitDto> result = new LinkedList<>();

        //Végigmegyünk a JSon entitásokon
        entities.keySet().stream()
                .map((entityName) -> entities.getJsonObject(entityName))
                .map((jsonValueEntity) -> {
                    DataUnitDto dto = new DataUnitDto();
                    dto.setRestPath(this.getPath());
                    Class entityClass = RestPathToJpaEntityClassMap.getJpaEntityClass(this.getPath());
                    dto.setEntityName(entityClass != null ? entityClass.getSimpleName() : "unknown");
                    dto.setDataName(jsonValueEntity.getJsonString("name").getString());
                    dto.setUnit(jsonValueEntity.getJsonString("unit").getString());
                    dto.setDescription(jsonValueEntity.getJsonString("description").getString());
                    return dto;
                }).forEachOrdered((dto) -> {
            result.add(dto);
        });

        return result;

    }

    /**
     * A mért adatok neve/mértékegysége/leírása lista
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param userName          REST hívás usere
     * @param sessionToken      GF session token
     *
     * @return mért adatok leírásának listája
     */
    @Override
    public List<DataUnitDto> collectDataUnits(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken) {

        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(simpleUrl, restDataCollector.getSubUri() + this.getPath(), userName, sessionToken);
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);

        //Mehet az értékek kinyerése az entities-ből
        return this.fetchDataUnits(entities);
    }

    /**
     * A REST válaszokból kinyeri az értékeket
     * Csak a collectedDatatNames halmazban szereplő adatnevekkel foglalkozunk
     *
     * @param entities            JSon entitás
     * @param collectedDatatNames kigyűjtendő adatnevek halmaza
     *
     * @return értékek listája
     */
    protected List<CollectedValueDto> fetchValues(JsonObject entities, Set<String> collectedDatatNames) {

        if (entities == null) {
            return null;
        }

        List<CollectedValueDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        for (String entityName : entities.keySet()) {
            JsonObject jsonValueEntity = entities.getJsonObject(entityName);

            //Leszedjük az adatnevet és megvizsgáljuk, hogy kell-e gyűjteni egyáltalán ezt az adatnév értéket?
            String dataName = jsonValueEntity.getJsonString("name").getString();
            if (collectedDatatNames != null && !collectedDatatNames.contains(dataName)) {
                continue;
            }

            String unitName = jsonValueEntity.getJsonString("unit").getString();
            if (unitName == null) {
                log.error("A(z) '{}' JSon entitásnak nincs 'unit' értéke!", entityName);
                continue;
            }

            //Unit type kitalálása
            // A típusok a JSonEntityToSnapshotEntityMapperBase.fieldMapper()-ben vannak felhasználva
            ValueUnitType valueUnitType = ValueUnitType.fromValue(jsonValueEntity.getJsonString("unit").getString());

            //Ha COUNT az unit, de van 'lowwatermark' és 'highwatermark' -> COUNT_CURR_LW_HW lesz a típus
            //Ha COUNT az unit, de van 'lowwatermark' és 'highwatermark' és 'lowerbound' meg 'upperbound'  -> COUNT_CURR_LW_HW_LB_UB lesz a típus
            if ((valueUnitType == ValueUnitType.COUNT || valueUnitType == ValueUnitType.MILLISECOND) //A JdbcConnectionPool-ban van MILLESECOND Hi/Lo értékkel :-/
                    && jsonValueEntity.getJsonNumber("lowwatermark") != null
                    && jsonValueEntity.getJsonNumber("highwatermark") != null) {

                if (jsonValueEntity.getJsonNumber("lowerbound") != null
                        && jsonValueEntity.getJsonNumber("upperbound") != null) {
                    valueUnitType = ValueUnitType.COUNT_CURR_LW_HW_LB_UB;
                } else {
                    //Sima LowWtermark/HighWaterMark
                    valueUnitType = ValueUnitType.COUNT_CURR_LW_HW;
                }
            } else if (valueUnitType == ValueUnitType.UNIT
                    && jsonValueEntity.getJsonNumber("mintime") != null
                    && jsonValueEntity.getJsonNumber("maxtime") != null
                    && jsonValueEntity.getJsonNumber("totaltime") != null) {
                valueUnitType = ValueUnitType.COUNT_MT_MT_TT;
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
                case MILLISECONDS:
                case NANOSECOND:
                case COUNT:
                case BYTES:
                    dto.setCount(jsonValueEntity.getJsonNumber("count").longValue());
                    break;

                case COUNT_CURR_LW_HW_LB_UB:
                    dto.setLowerBound(jsonValueEntity.getJsonNumber("lowerbound").longValue());
                    dto.setUpperBound(jsonValueEntity.getJsonNumber("upperbound").longValue());
                //NINCS BREAK!!! -> csorogjon rá a COUNT_CURR_LW_HW-ra!

                case COUNT_CURR_LW_HW:
                    dto.setCurrent(jsonValueEntity.getJsonNumber("current").longValue());
                    dto.setLowWatermark(jsonValueEntity.getJsonNumber("lowwatermark").longValue());
                    dto.setHighWatermark(jsonValueEntity.getJsonNumber("highwatermark").longValue());
                    break;

                case LIST:
                case STRING:
                    dto.setCurrent(jsonValueEntity.getJsonString("current").getString());
                    break;

                case UNIT:
                    log.warn("Ezt ellenőrizni! -> entityName: '{}', unitName: '{}', valueUnitType: '{}' !", entityName, unitName, valueUnitType);
                    dto.setCurrent(jsonValueEntity.getJsonNumber("count"));
                    break;

                case COUNT_MT_MT_TT:
                    dto.setCount(jsonValueEntity.getJsonNumber("count").longValue());
                    dto.setMinTime(jsonValueEntity.getJsonNumber("mintime").longValue());
                    dto.setMaxTime(jsonValueEntity.getJsonNumber("maxtime").longValue());
                    dto.setTotalTime(jsonValueEntity.getJsonNumber("totaltime").longValue());
                    break;

                default:
                    log.warn("Nincs lekezelve a JSon entitás, entityName: '{}', unitName: '{}', valueUnitType: '{}' !", entityName, unitName, valueUnitType);
                    break;
            }

            result.add(dto);
        }

        return result;
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
     * Monitorozott entitások leszedése
     *
     * @param response REST response
     *
     * @return Json entitások
     */
    private JsonObject getEntitiesFromResponse(Response response) {
        //JSon válasz leszedése
        JsonObject rootJsonObject = response.readEntity(JsonObject.class);
        if (!isSuccessJsonResponse(rootJsonObject)) {
            log.error("hiba a JSon válaszban! Message:'{}', Command:'{}', ExitCode:'{}'",
                    rootJsonObject.getString("message"), rootJsonObject.getString("command"), rootJsonObject.getString("exit_code"));
            return null;
        }

        //Entitások kiszedése a jSon válaszból
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);

        return entities;
    }

    /**
     * Monitorozandó entitások lekérése
     *
     * @param restDataCollector REST adatgyűjtő példány
     * @param fullUrl           teljes URL
     * @param sessionToken      session token
     * @param erroredPaths      hibára futott URL-ek halmaza
     *
     * @return Json entitás
     */
    protected JsonObject getMonitoredEntities(RestDataCollector restDataCollector, String fullUrl, String sessionToken, Set<String> erroredPaths) {

        //REST response lekérése
        Response response = restDataCollector.getResponse(fullUrl, sessionToken);

        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) '{}' url hívására {} hibakód jött", fullUrl, response.getStatusInfo().getReasonPhrase());
            if (erroredPaths != null) {
                erroredPaths.add(this.getPath());
            }
            return null;
        }

        return this.getEntitiesFromResponse(response);
    }

    /**
     * Monitorozandó entitások lekérése
     *
     * @param restDataCollector REST adatgyűjtő példány
     * @param simpleUrl         simple url
     * @param userName          REST hívás usere
     * @param sessionToken      session token
     * @param erroredPaths      hibára futott URL-ek halmaza
     *
     * @return Json entitás
     */
    protected JsonObject getMonitoredEntities(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken, Set<String> erroredPaths) {

        //REST response lekérése
        Response response = restDataCollector.getResponse(this.getPath(), simpleUrl, userName, sessionToken);

        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {}/{} url hívására {} hibakód jött", simpleUrl, this.getPath(), response.getStatusInfo().getReasonPhrase());
            if (erroredPaths != null) {
                erroredPaths.add(this.getPath());
            }
            return null;
        }

        return this.getEntitiesFromResponse(response);
    }

    /**
     * Monitorozandó entitások lekérése
     *
     * @param restDataCollector REST adatgyűjtő példány
     * @param simpleUrl         simple url
     * @param userName          REST hívás usere
     * @param sessionToken      session token
     * @param uriParams         path URI paraméterek
     * @param erroredPaths      hibára futott URL-ek halmaza
     *
     * @return Json entitás
     */
    protected JsonObject getMonitoredEntities(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken, Map<String, String> uriParams, Set<String> erroredPaths) {

        String uriPath = makeRestUriPath(this.getPath(), uriParams);

        //REST response lekérése
        Response response = restDataCollector.getResponse(uriPath, simpleUrl, userName, sessionToken);

        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {}/{} url hívására {} hibakód jött", simpleUrl, uriPath, response.getStatusInfo().getReasonPhrase());
            if (erroredPaths != null) {
                erroredPaths.add(uriPath);
            }
            return null;
        }

        return this.getEntitiesFromResponse(response);
    }
}
