/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    MonitorsBase.java
 *  Created: 2018.01.19. 19:59:57
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.exception.GfMonException;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
import java.util.concurrent.Future;
import javax.ejb.Asynchronous;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Server és Application monitor kontrollerek ős osztálya
 *
 * @author BT
 */
@Slf4j
public abstract class MonitorsBase {

    @Inject
    private SessionTokenAcquirer sessionTokenAcquirer;

    @Inject
    protected ConfigService configService;

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    protected abstract String getDbModificationUser();

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    protected abstract String getControllerName();

    /**
     * A timer indítása előtti események
     */
    public void beforeStartTimer() {
    }

    /**
     * A timer leállítása utáni lépések
     */
    public void afterStopTimer() {
    }

    /**
     * Bejelentkezés a szerverbe
     *
     * @param server szerver entitás
     *
     * @return true -> sikeres bejelentkezés
     */
    protected boolean acquireSessionToken(Server server) {

        //Van már sessionToken
        if (!StringUtils.isEmpty(server.getSessionToken())) {
            return true;
        }

        String url = server.getUrl();
        String userName = server.getUserName();
        String plainPassword = server.getPlainPassword();

        //Ha nincs userName, akkor nem is kell sessionToken!
        if (StringUtils.isEmpty(userName)) {
            return true;
        }

        //Ha még nincs SessionToken, akkor csinálunk egyet
        try {
            String sessionToken = sessionTokenAcquirer.getSessionToken(url, userName, plainPassword);
            server.setSessionToken(sessionToken);
            return true;

        } catch (GfMonException e) {

            //Beírjuk a hibaüzenetet a szerver példányba
            server.setAdditionalInformation(e.getMessage());

            //Töröljük a tokent is
            server.setSessionToken(null);
        }

        return false;
    }

    /**
     * Monitorozás indul
     */
    public abstract Future<Void> startMonitoring();

    /**
     * Rendszeres napi karbantartás az adatbázisban
     */
    @Asynchronous
    public void dailyJob() {
    }
}
