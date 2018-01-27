/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerCollectorBase.java
 *  Created: 2017.12.24. 17:21:48
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.List;
import java.util.Set;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * A GF REST interfészén keresztül adatokat gyűjtő kollektorok ős osztálya
 *
 * @author BT
 */
@Slf4j
public abstract class ServerCollectorBase extends CollectorBase implements IServerCollector {

    /**
     * REST JSon monitor adatok összegyűjtése
     *
     * @param restDataCollector  REST adatgyűjtó példány
     * @param simpleUrl          a szerver url-je
     * @param userName           REST hívás usere
     * @param sessionToken       GF session token
     * @param collectedDataNames kigyűjtendő adatnevek halmaza
     * @param erroredPaths       hibára futott URL-ek halmaza
     *
     * @return Json entitás - értékek Lista
     */
    @Override
    public List<CollectedValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken, Set<String> collectedDataNames, Set<String> erroredPaths) {

        //Ha nem kell ebből az adatgyűjtőből semmi adat, akkor meg sem hívjuk;
        if (collectedDataNames == null) {
            return null;
        }

        JsonObject entities = super.getMonitoredEntities(restDataCollector, simpleUrl, userName, sessionToken, erroredPaths);
        return super.fetchValues(entities, collectedDataNames);
    }

}
