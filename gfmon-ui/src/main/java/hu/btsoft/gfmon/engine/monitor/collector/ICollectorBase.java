/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ICollectorBase.java
 *  Created: 2018.01.21. 11:12:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector;

import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.monitor.collector.server.ServerMonitorValueDto;
import java.util.List;
import java.util.Set;

/**
 *
 * @author BT
 */
public interface ICollectorBase {

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni? pl.: "server/http-service/server/request"
     *
     * @return path
     */
    String getPath();

    /**
     * A mért adatok neve/mértékegysége/leírása lista
     *
     * @param restDataCollector REST Data Collector példány
     * @param simpleUrl         A GF szerver url-je
     * @param sessionToken      GF session token
     *
     * @return mért adatok leírásának listája
     */
    List<DataUnitDto> collectDataUnits(RestDataCollector restDataCollector, String simpleUrl, String sessionToken);

    /**
     * Adatgyűjtés végrehajtása
     *
     * @param restDataCollector   REST Data Collector példány
     * @param simpleUrl           A GF szerver url-je
     * @param sessionToken        GF session token
     * @param collectedDatatNames kigyűjtendő adatnevek halmaza
     *
     * @return adatgyűjtés eredménye (JSon entitás - értékek Lista)
     */
    List<ServerMonitorValueDto> execute(RestDataCollector restDataCollector, String simpleUrl, String sessionToken, Set<String> collectedDatatNames);

}
