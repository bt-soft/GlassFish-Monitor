/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    RestClientProducer.java
 *  Created: 2017.12.23. 11:49:16
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.rest;

import java.io.Serializable;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * REST kliens létrehozása
 *
 * @author BT
 */
@Slf4j
public class RestClientProducer implements Serializable {

    /**
     * REST kliens példány létrehozása
     *
     * @param injectionPoint hova lesz az injektálás? (csak a logoláshoz kell)
     *
     * @return REST kliens
     */
    @Produces
    @GFMonitorRestClient
    public Client newClient(InjectionPoint injectionPoint) {
        Client client = ClientBuilder.newClient();
        log.trace("REST kliens létrehozás: {}, osztály: {}", client, injectionPoint.getMember().getDeclaringClass().getName());
        return client;
    }

    /**
     * REST kliens lecsukása
     *
     * @param client REST kliens példány
     */
    public void closeClient(@Disposes @GFMonitorRestClient Client client) {
        log.trace("REST kliens lecsukása: {}", client);
        client.close();
    }

}
