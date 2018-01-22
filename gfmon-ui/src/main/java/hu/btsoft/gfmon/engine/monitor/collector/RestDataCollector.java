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
import javax.ws.rs.core.Response;

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
    protected String getSubUri() {
        return "/monitoring/domain/server/";
    }

    /**
     * REST válasz olvasása
     *
     * @param resourceUri  monitorozott rest erőforrás URI
     * @param simpleUrl    a GF szerver url-je
     * @param sessionToken a GF session token-je
     *
     * @return REST válasz
     */
    public Response getMonitorResponse(String resourceUri, String simpleUrl, String sessionToken) {

        super.simpleUrl = simpleUrl;
        super.sessionToken = sessionToken;

        return getMonitorResponse(resourceUri);
    }

}
