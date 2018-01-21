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

import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ProcessingException;
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
    SessionTokenAcquirer sessionTokenAcquirer;

    @EJB
    protected ServerService serverService;

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

        //ha még nincs SessionToken, akkor csinálunk egyet
        try {
            String sessionToken = sessionTokenAcquirer.getSessionToken(url, userName, plainPassword);

            server.setSessionToken(sessionToken);

        } catch (Exception e) {

            String logMsg;
            String dbMsg;
            if (e instanceof NotAuthorizedException) {
                logMsg = "a(z) '{}' szerverbe nem lehet bejelentkezni!";
                dbMsg = "Nem lehet bejelentkezni!";

            } else if (e instanceof ProcessingException) {
                logMsg = "a(z) '{}' szerver nem érhető el!";
                dbMsg = "A szerver nem érhető el!";

            } else {
                logMsg = String.format("a(z) '{}' szerver monitorozása ismeretlen hibára futott: %s", e.getCause().getMessage());
                dbMsg = "Ismeretlen hiba: " + e.getCause().getMessage();
            }

            log.error("GFMon {} modul: " + logMsg, getControllerName(), server.getUrl());

            //Beírjuk az üzenetet az adatbázisba is
            serverService.updateAdditionalMessage(server, getDbModificationUser(), dbMsg);

            return false;
        }

        return true;
    }

    /**
     * Monitorozás indul
     */
    public abstract void startMonitoring();

    /**
     * Rendszeres napi karbantartás az adatbázisban
     */
    @Asynchronous
    public void dailyJob() {
    }
}
