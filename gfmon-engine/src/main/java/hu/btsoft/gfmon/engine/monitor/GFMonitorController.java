/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    GFMonitorController.java
 *  Created: 2017.12.23. 11:55:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.cdi.CdiUtils;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.entity.Server;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.model.service.SnapshotService;
import hu.btsoft.gfmon.engine.monitor.runtime.ServerMonitoringServiceStatus;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.ProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * GF monitor vezérlő CDI bean
 *
 * @author BT
 */
@Singleton
@Startup
@DependsOn("Bootstrapper")
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class GFMonitorController {

    private static final String DB_MODIFICATORY_USER = "GF-Monitor-Controller";

    @EJB
    private ConfigService configService;

    @EJB
    private ServerService serverService;

    @EJB
    private SnapshotService snapshotService;

    @Resource
    private TimerService timerService;

    private Timer timer;

//    @Inject
//    private SessionTokenAcquirer sessionTokenAcquirer;
//
//
//    @Inject
//    private SnapshotProvider snapshotProvider;
    /**
     * GFMon engine indítása
     */
    @PostConstruct
    protected void initApp() {

        if (!configService.isAutoStart()) {
            log.debug("Az automatikus indítás kikapcsolva");
            return;
        }

        log.trace("GFMon Mérés indul");
        startTimer();
    }

    /**
     * Timer indítása
     */
    public void startTimer() {

        if (isRunningTimer()) {
            log.warn("A Timer ({}) már fut", timer);
            return;
        }

        //Runtime értékek törlése
        serverService.clearRuntimeValues(DB_MODIFICATORY_USER);

        //Mérési periódusidő leszedése a konfigból
        int sampleIntervalSec = configService.getSampleInterval();

        //Timer felhúzása
        this.timer = this.timerService.createIntervalTimer(1_000, // késleltetés
                sampleIntervalSec * 1_000, //intervallum
                new TimerConfig("GFMon-Timer", false) //ne legyen perzisztens a timer!
        );

        log.trace("Timer felhúzva {} másodpercenként", sampleIntervalSec);
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
            log.error("Nem állítható le a Timer: {}", this.timer, e);
        } finally {
            this.timer = null;
        }

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
     * A monitorozási mintavétel indítása
     */
    @Timeout
    protected void timeOut() {

        long start = Elapsed.nowNano();

        //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookup-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
        SnapshotProvider snapshotProvider = CdiUtils.lookup(SnapshotProvider.class);

        log.trace("this: {}, snapshotProvider: {}", this, snapshotProvider);

        int checkedServerCnt = 0;
        for (Server server : serverService.findAll()) {

            //Az inaktív szerverekkel nem foglalkozunk
            if (!server.isActive()) {
                continue;
            }

            String url = server.getUrl();
            String userName = server.getUserName();
            String plainPassword = server.getPlainPassword();

            //ha még nincs SessionToken, akkor csinálunk egyet
            if (StringUtils.isEmpty(server.getSessionToken())) {

                try {
                    //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookup-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
                    SessionTokenAcquirer sessionTokenAcquirer = CdiUtils.lookup(SessionTokenAcquirer.class);

                    String sessionToken = sessionTokenAcquirer.getSessionToken(url, userName, plainPassword);

                    server.setSessionToken(sessionToken);

                } catch (Exception e) {

                    String logMsg;
                    String dbMsg;
                    if (e instanceof NotAuthorizedException) {
                        logMsg = "A(z) '{}' szerverbe nem lehet bejelnetkezni!";
                        dbMsg = "Nem lehet bejelentkezni!";

                    } else if (e instanceof ProcessingException) {
                        logMsg = "A(z) '{}' szerver nem érhető el!";
                        dbMsg = "A szerver nem érhető el!";

                    } else {
                        logMsg = String.format("A(z) '{}' szerver monitorozása ismeretlen hibára futott: %s", e.getCause().getMessage());
                        dbMsg = "Ismeretlen hiba: " + e.getCause().getMessage();
                    }

                    log.error(logMsg, server.getUrl());

                    //Beírjuk az üzenetet az adatbázisba is
                    serverService.updateAdditionalMessage(server, DB_MODIFICATORY_USER, dbMsg);

                    //jöhet a következő szerver
                    continue;
                }
            }

            //Ha még nem tudjuk, hogy az adott szerveren be van-e kapcsolva a MonitoringService
            if (server.getMonitoringServiceReady() == null || !server.getMonitoringServiceReady()) {

                //Mivel egy @Singleton Bean-ban vagyunk, emiatt kézzel lookup-oljuk a CDI Bean-t, hogy ne fogjon le egy Rest kliesnt állandó jelleggel
                ServerMonitoringServiceStatus serverMonitoringServiceStatus = CdiUtils.lookup(ServerMonitoringServiceStatus.class);

                // A monitorozandó GF példányok MonitoringService (module-monitoring-levels) ellenőrzése
                Set<String> monitorableModules = serverMonitoringServiceStatus.checkMonitorStatus(server.getSimpleUrl(), server.getSessionToken());

                // Amely szervernek nincs engedélyezve egyetlen monitorozható modulja sem, azt jól inaktívvá tesszük
                if (monitorableModules == null) {

                    //letiltjuk
                    server.setActive(false);

                    //Beírjuk az üzenetet az adatbázisba is
                    String kieginfo = "A szerver MonitoringService szolgáltatása nincs engedélyezve, emiatt a monitorozása le lett tiltva!";
                    serverService.updateAdditionalMessage(server, DB_MODIFICATORY_USER, kieginfo);

                    //logot is írunk
                    log.warn("{}: {}", server.getUrl(), kieginfo);

                } else {
                    //Megjegyezzük, hogy a szerver moitorozható
                    server.setMonitoringServiceReady(true);
                    log.trace("A(z) {} szerver monitorozható moduljai: {}", url, monitorableModules);
                }

                //lementjük az adatbázisba a szerver megváltozott állapotát
                serverService.save(server);

                //Ha incs mit monitorozini rajta, akkor már nem foglalkozunk vele tovább, majd visszabillenthető a státusza a UI felületről
                if (!server.getMonitoringServiceReady()) {
                    continue;
                }
            }

            log.trace("Adatgyűjtés indul: {}", url);

            Set<SnapshotBase> snapshots = snapshotProvider.fetchSnapshot(server);
            checkedServerCnt++;

            //Töröljük a kieginfót, ha van
            serverService.clearAdditionalMessage(server, DB_MODIFICATORY_USER);

            if (snapshots == null || snapshots.isEmpty()) {
                log.warn("Nincsenek menthető pillanatfelvételek!");
                return;
            }

            //JPA mentés
            snapshots.stream().map((snapshot) -> {
                //Beállítjuk, hogy melyik szerver mérési ereménye ez a pillanatfelvétel
                snapshot.setServer(server);
                return snapshot;
            }).map((snapshot) -> {
                //lementjük az adatbázisba
                snapshotService.save(snapshot);
                return snapshot;
            }).forEachOrdered((snapshot) -> {
                log.trace("Snapshot: {}", snapshot);
            });

            //Kiíratjuk a változásokat az adatbázisba
            snapshotService.flush();
        }

        log.trace("Monitor {} db szerverre, elapsed: {}", checkedServerCnt, Elapsed.getNanoStr(start));
    }
}
