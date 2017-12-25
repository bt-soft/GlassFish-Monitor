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

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * REST kliens létrehozása
 *
 * @author BT
 */
@Slf4j
public class RestClientProducer {

    /**
     * REST kliens példány létrehozása
     *
     * @return REST kliens
     */
    @Produces
    public Client newClient() {
        Client client = ClientBuilder.newClient();
        log.trace("REST kliens létrehozás: {}", client);
        return client;
    }

    /**
     * REST kliens lecsukása
     *
     * @param client REST kliens példány
     */
    public void closeClient(@Disposes Client client) {
        log.trace("REST kliens lecsukása: {}", client);
        client.close();
    }

}
