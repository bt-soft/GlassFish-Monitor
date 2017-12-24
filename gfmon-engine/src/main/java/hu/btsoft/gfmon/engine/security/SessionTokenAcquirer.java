/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SessionTokenAcquirer.java
 *  Created: 2017.12.23. 17:44:27
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.security;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;

/**
 * GF Session token megszerzése
 *
 * A GF administration-guide.pdf-ból infó:
 * <p>
 * To Secure REST Interfaces by Using Session Tokens
 * <p>
 * Basic authentication requires a REST client to cache a user's credentials to
 * enable theclient to pass the credentials with each request. If you require a
 * REST client not to cache credentials, your client must use session tokens for
 * authentication.
 * <p>
 * 1. Request a session token by using the GET method on the resource at
 * http://host:port/management/sessions. GlassFish Server uses basic
 * authentication to authenticate the client, generates a session token, and
 * passes the token to the client.
 * <p>
 * 2. In each subsequent request that requires authentication, use the token
 * toauthenticate the client. a. Create a cookie that is named gfresttoken the
 * value of which is the token. Using REST Interfaces to Administer GlassFish
 * Server General Administration 2-29 b. Send the cookie with the request. 3.
 * When the token is no longer required, retire the token by using the DELETE
 * method on the resource at http://host:port/management/sessions/{tokenvalue}.
 *
 * @author BT
 */
public class SessionTokenAcquirer {

    @Inject
    protected Client client;

    @Inject
    private GlassfishAuthenticator glassfishAuthenticator;

    /**
     * GF REST session token lekérése a GF-től
     *
     * @param url           szerver url
     * @param userName      user
     * @param plainPassword kódolatlan jelszó
     *
     * @return GF session token
     */
    public String getSessionToken(String url, String userName, String plainPassword) {

        glassfishAuthenticator.addAuthenticator(client, userName, plainPassword);

        WebTarget managementResource = this.client.target(url + "/management/");
        JsonObject result = managementResource
                .path("sessions")
                .request(MediaType.APPLICATION_JSON)
                .header("X-Requested-By", IGFMonEngineConstants.SHORT_APP_NAME)
                .post(Entity.entity(Json.createObjectBuilder().build(), MediaType.APPLICATION_JSON), JsonObject.class);

        JsonObject extraProps = result.getJsonObject("extraProperties");
        String token = extraProps.getString("token");

        //Visszatérünk a session token-el
        return token;
    }

}
