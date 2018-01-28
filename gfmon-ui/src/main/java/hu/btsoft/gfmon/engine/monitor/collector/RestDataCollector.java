/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RestDataCollector.java
 *  Created: 2017.12.26. 8:50:12
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector;

import hu.btsoft.gfmon.engine.rest.RestDataCollectorBase;

/**
 * Monitorozott adatok REST begyűjtése
 *
 * @author BT
 */
public class RestDataCollector extends RestDataCollectorBase {

    /**
     * A szerver url-jéhez képest hol tatlálható a megszerzendő JSon adat
     *
     * @return sub uri
     */
    @Override
    public String getSubUri() {
        return "/monitoring/domain/server/";
    }
}
