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
import java.util.List;

/**
 *
 * @author BT
 */
public interface ICollectorBase {

    /**
     * Az adatgyűjtést a szerver URL-jéhez képest melyik uri-n kell elvégezni? pl.: "server/http-service/server/request"
     *
     * @return path monitorozott path
     */
    String getPath();

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
    List<DataUnitDto> collectDataUnits(RestDataCollector restDataCollector, String simpleUrl, String userName, String sessionToken);
}
