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

import hu.btsoft.gfmon.corelib.exception.GfMonException;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.rest.GFMonitorRestClient;
import java.io.Serializable;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * GF Session token megszerzése
 * <p>
 * ------------------
 * ------------------ A GF administration-guide.pdf-ból infó:
 * ------------------
 * <p>
 * Timport javax.ws.rs.client.Client;
 * import javax.ws.rs.client.Entity;
 * import javax.ws.rs.client.WebTarget;
 * import javax.ws.rs.core.MediaType;
 * o Secure REST Interfaces by Using Session Tokens
 * <p>
 * Basic authentication requires a REST client to cache a user's credentials to
 * enable theclient to pass the credentials with each request. If you require a
 * REST client not to cache credentials, your client must use session tokens for
 * authentication.
 * <p>
 * 1.
 * Request a session token by using the GET method on the resource at http://host:port/management/sessions.
 * GlassFish Server uses basic authentication to authenticate the client, generates a session token, and passes the token to the client.
 * <p>
 * 2.
 * In each subsequent request that requires authentication, use the token toauthenticate the client.
 * -- a. Create a cookie that is named <b>gfresttoken</b> the value of which is the token.
 * Using REST Interfaces to Administer GlassFish Server General Administration 2-29
 * -- b. Send the cookie with the request.
 * <p>
 * 3.
 * When the token is no longer required, retire the token by using the DELETE method on the resource at http://host:port/management/sessions/{tokenvalue}.
 *
 * @author BT
 */
@Slf4j
public class SessionTokenAcquirer implements Serializable {

    @Inject
    @GFMonitorRestClient
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
     * @return GF session token -> gfresttoken
     *
     * @throws GfMonException ha hiba van
     */
    public String getSessionToken(String url, String userName, String plainPassword) throws GfMonException {

        if (StringUtils.isEmpty(userName)) {
            return null;
        }

        try {
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
        } catch (Exception e) {

            String msg;

            if (e instanceof NotAuthorizedException) {
                msg = String.format("A(z) '%s' szerverbe nem lehet bejelentkezni! (%s)", url, e.getMessage());

            } else if (e instanceof ProcessingException) {
                msg = String.format("A(z) '%s' szerver nem érhető el! (%s)", url, e.getMessage());

            } else {
                msg = String.format("A(z) '%s' szerver bejelentkezése során hiba lépett fel: %s", url, e.getCause().getMessage());
            }

            log.error(msg);

            throw new GfMonException(msg, e);
        }
    }

}
