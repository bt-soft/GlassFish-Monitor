/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    GlassfishAuthenticator.java
 *  Created: 2017.12.23. 17:47:11
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.security;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author BT
 */
@Singleton
@Slf4j
public class GlassfishAuthenticator {

    // <editor-fold defaultstate="collapsed" desc="TrustManager">
    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType) {
        }
    }};
    // </editor-fold>

    /**
     *
     */
    @PostConstruct
    protected void init() {
        //Kikapcsoljuk az SSL host name ellenőrzését
        HttpsURLConnection.setDefaultHostnameVerifier((String string, SSLSession ssls) -> true);

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("GF authentikálási hiba", e);
        }
    }

    /**
     * REST BASIC authentikáció
     * Csak ha meg van adva az userName paraméter
     *
     * @param client        REST kliens
     * @param userName      user
     * @param plainPassword kódolatlan jelszó
     */
    public void addAuthenticator(Client client, String userName, String plainPassword) {

        //ha van userName de még nincs regisztrálva a kliens
        if (!StringUtils.isEmpty(userName) && !client.getConfiguration().isRegistered(BasicAuthenticatorFilter.class)) {
            //Regisztrálás!
            client.register(new BasicAuthenticatorFilter(userName, plainPassword));
        } else {
            init();
        }

    }

}
