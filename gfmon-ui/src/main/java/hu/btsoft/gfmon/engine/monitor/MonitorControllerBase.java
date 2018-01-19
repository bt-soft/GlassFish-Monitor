/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    MonitorControllerBase.java
 *  Created: 2018.01.19. 19:59:57
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.cdi.CdiUtils;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Schedule;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
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
public abstract class MonitorControllerBase {

    @EJB
    protected ConfigService configService;

    @EJB
    protected ServerService serverService;

    @Resource
    private TimerService timerService;

    protected Timer timer;

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    public abstract String getDbModificationUser();

    /**
     * Az adatbázisban módosítást végző user azonosítójának elkérése
     *
     * @return módosító user
     */
    public abstract String getControllerName();

    /**
     * GFMon engine indítása
     */
    @PostConstruct
    protected void init() {

        if (!configService.getBoolean(IConfigKeyNames.AUTOSTART)) {
            log.debug("GFMon {} modul: Az automatikus indítás kikapcsolva", getControllerName());
            return;
        }

        log.trace("GFMon {} modul: mérés indul", getControllerName());
        startTimer();
    }

    /**
     * Timer indítása
     */
    public void startTimer() {

        if (isRunningTimer()) {
            log.warn("GFMon {} modul: a Timer ({}) már fut", getControllerName(), timer);
            return;
        }

        //Leszármazott metódusának meghívása, ha van
        beforeStartTimer();

        //Mérési periódusidő leszedése a konfigból
        int sampleIntervalSec = configService.getInteger(IConfigKeyNames.SAMPLE_INTERVAL);

        //Timer felhúzása
        this.timer = this.timerService.createIntervalTimer(1_000, // késleltetés
                sampleIntervalSec * 1_000, //intervallum
                new TimerConfig(String.format("GFMon-%s-Timer", getControllerName()), false) //ne legyen perzisztens a timer!
        );

        log.trace("GFMon {} modul: a timer felhúzva {} másodpercenként", getControllerName(), sampleIntervalSec);
    }

    /**
     * A timer indítása előtti események
     */
    protected void beforeStartTimer() {
    }

    /**
     * Timer leállítása
     */
    @PreDestroy
    public void stopTimer() {

        //Már áll a timer?
        if (timer == null) {
            return;
        }

        try {
            this.timer.cancel();
        } catch (IllegalStateException | EJBException e) {
            log.error("GFMon {} modul: Nem állítható le a Timer: {}", getControllerName(), this.timer, e);
        } finally {
            this.timer = null;
        }

        //Leszármazott metódusának meghívása, ha van
        afterStopTimer();
    }

    /**
     * A timer leállítása utáni lépések
     */
    protected void afterStopTimer() {
    }

    /**
     * Timer újraindítása
     */
    public void restartTimer() {
        stopTimer();
        startTimer();
    }

    /**
     * A timer fut?
     *
     * @return
     */
    public boolean isRunningTimer() {
        return (this.timer != null);
    }

    /**
     * Bejelentkezés a szerverbe
     *
     * @param server szerver entitás
     *
     * @return true -> sikeres bejelentkezés
     */
    protected boolean acquireSessionToken(Server server) {

        //Van már sessionToken:
        if (!StringUtils.isEmpty(server.getSessionToken())) {
            return true;
        }

        String url = server.getUrl();
        String userName = server.getUserName();
        String plainPassword = server.getPlainPassword();

        //ha még nincs SessionToken, akkor csinálunk egyet
        try {
            //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookupOne-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
            SessionTokenAcquirer sessionTokenAcquirer = CdiUtils.lookupOne(SessionTokenAcquirer.class);

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
     * A monitorozási mintavétel indítása
     */
    @Timeout
    protected void timeOut() {
        try {
            startMonitoring();
        } catch (Exception e) {
            log.error(String.format("GFMon {} modul: Hiba a napi monitorozott adatok begyűjtése közben", getControllerName()), e);
        }
    }

    /**
     * Monitorozás indul
     */
    protected void startMonitoring() {

    }

    /**
     * Automatikus takarítás minden nap éjfélkor fut le
     */
    @Schedule(hour = "00", minute = "00", second = "00")
    protected void doDailyPeriodicCleanup() {
        try {
            dailyCleanUp();
        } catch (Exception e) {
            log.error(String.format("GFMon {} modul: Hiba a napi takarítás közben", getControllerName()), e);
        }
    }

    /**
     * Rendszeres napi tisztítás az adatbázisban
     */
    protected abstract void dailyCleanUp();
}
