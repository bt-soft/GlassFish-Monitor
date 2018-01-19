/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    BasicAuthenticatorFilter.java
 *  Created: 2017.12.23. 17:52:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.DatatypeConverter;

/**
 * HTTP BASIC authentikáció
 *
 * @author BT
 */
public class BasicAuthenticatorFilter implements ClientRequestFilter {

    private final String userName;
    private final String plainPassword;

    /**
     * Konstruktor
     *
     * @param userName      user
     * @param plainPassword kódolatlan jelszó
     */
    public BasicAuthenticatorFilter(String userName, String plainPassword) {
        this.userName = userName;
        this.plainPassword = plainPassword;
    }

    /**
     * HTTP BASIC auth beillesztése a request header-be
     *
     * @param requestContext req
     *
     * @throws IOException -
     */
    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        MultivaluedMap<String, Object> headers = requestContext.getHeaders();
        headers.add("Authorization", this.getBasicAuthentication());
    }

    /**
     * BASIC auth request paraméter legyártása
     *
     * @return
     */
    private String getBasicAuthentication() {
        String token = String.format("%s:%s", this.userName, this.plainPassword);

        try {
            return "BASIC " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Enkódolási hiba", e);
        }
    }

}
