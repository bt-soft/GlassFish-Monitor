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
import hu.btsoft.gfmon.engine.monitor.SvrRestPathToSvrJpaEntityClassMap;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
     * A REST válaszokból kinyeri az adatneveket és leírásukat
     *
     * @param entities JSon entitás
     *
     * @return adatneves leírása
     */
    protected List<DataUnitDto> fetchDataUnits(JsonObject entities) {

        if (entities == null) {
            return null;
        }

        List<DataUnitDto> result = new LinkedList<>();

        //Végigmegyünk az entitásokon
        entities.keySet().stream()
                .map((entityName) -> entities.getJsonObject(entityName))
                .map((jsonValueEntity) -> {
                    DataUnitDto dto = new DataUnitDto();
                    dto.setRestPath(this.getPath());
                    Class entityClass = SvrRestPathToSvrJpaEntityClassMap.getJpaEntityClass(this.getPath());
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
     * @param sessionToken      GF session token
     *
     * @return mért adatok leírásának listája
     */
    @Override
    public List<DataUnitDto> collectDataUnits(RestDataCollector restDataCollector, String simpleUrl, String sessionToken) {

        //URL hívása
        Response response = restDataCollector.getMonitorResponse(this.getPath(), simpleUrl, sessionToken);
        //JsonObject entities = restDataCollector.getJsonEntities(response);

        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {} url hívására {} hibakód jött", simpleUrl, response.getStatusInfo().getReasonPhrase());
            return null;
        }

        //JSon válasz leszedése
        JsonObject rootJsonObject = response.readEntity(JsonObject.class);

        //Entitások kiszedése a jSon válaszból
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);

        //Mehet az értékek kinyerése az entities-ből
        return this.fetchDataUnits(entities);
    }

    /**
     * Monitorozandó entitások lekérése
     *
     * @param restDataCollector REST adatgyűjtóő példány
     * @param simpleUrl         simple url
     * @param sessionToken      session token
     * @param path              milyen path-ról kell a JSon entitás
     *
     * @return Json entitás
     */
    protected JsonObject getMonitoredEntities(RestDataCollector restDataCollector, String simpleUrl, String sessionToken, String path) {

        Response response = restDataCollector.getMonitorResponse(path, simpleUrl, sessionToken);

        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {} url hívására {} hibakód jött", simpleUrl, response.getStatusInfo().getReasonPhrase());
            return null;
        }

        //JSon válasz leszedése
        JsonObject rootJsonObject = response.readEntity(JsonObject.class);

        //Entitások kiszedése a jSon válaszból
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);

        return entities;
    }
}
