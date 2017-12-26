/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    GFMonController.java
 *  Created: 2017.12.23. 11:55:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.model.entity.Server;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.model.service.SnapshotService;
import hu.btsoft.gfmon.engine.rest.CollectMonitorServiceModules;
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
import javax.inject.Inject;
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
public class GFMonController {

    @EJB
    private ConfigService configService;

    @EJB
    private ServerService serverService;

    @EJB
    private SnapshotService snapshotService;

    @Resource
    private TimerService timerService;

    private Timer timer;

    @Inject
    private SessionTokenAcquirer sessionTokenAcquirer;

    @Inject
    private SnapshotProvider snapshotProvider;

    @Inject
    private CollectMonitorServiceModules checkServerMonitorServiceState;

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

        int sampleIntervalSec = configService.getSampleInterval();

        //this.timer = this.timerService.createTimer(1_000 /* 1 mp múlva indul */, sampleIntervalSec * 1_000, new ScheduleExpression());
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
                String sessionToken = sessionTokenAcquirer.getSessionToken(url, userName, plainPassword);
                server.setSessionToken(sessionToken);
            }

            //Ha még nem tudjuk, hogy az adott szerveren mit lehet monitorozni., akkor azt most kigyűjtjük
            if (server.getMonitorableModules() == null) {

                // A monitorozandó GF példányok MonitoringService (module-monitoring-levels) ellenőrzése
                Set<String/*GF MoitoringService module name*/> monitorableModules = checkServerMonitorServiceState.checkMonitorStatus(server.getSimpleUrl(), server.getSessionToken());

                // Amely szervernek nincs engedélyezve egyetlen monitorozható modulja sem, azt jól inaktívvá tesszük
                if (monitorableModules == null) {
                    server.setActive(false);
                    server.setComment("A szerver MonitoringService szolgáltatása nincs engedélyezve, emiatt a monitorozása tiltva lett");
                    server.setModUser("GFMonController");
                    log.warn("{}: {}", server.getUrl(), server.getComment());

                    //Le is mentjük az adatbázisba az állapotot
                    serverService.save(server);

                    //Ezzel a szerverrel már nem foglalkozunk tovább, majd visszabillenthető a UI felületről a státusza
                    continue;
                }

                //Eltároljuk a monitorozható modulokat a memóriában
                server.setMonitorableModules(monitorableModules);
                log.trace("A(z) {} szerver monitorozható moduljai: {}", url, monitorableModules);
            }

            log.trace("Adatgyűjtés indul: {}", url);

            Set<SnapshotBase> snapshots = snapshotProvider.fetchSnapshot(server);

            if (snapshots == null || snapshots.isEmpty()) {
                log.warn("Nincsenek menthető pillanatfelvételek!");
                return;
            }

            //JPA mentés
            for (SnapshotBase snapshot : snapshots) {

                //Beállítjuk, hogy melyik szerver mérési ereménye ez a pillanatfelvétel
                snapshot.setServer(server);

                //lementjük az adatbázisba
                snapshotService.save(snapshot);
                snapshotService.detach(snapshot);

                log.trace("Snapshot: {}", snapshot);
            }

            ///eventBus.publish(IGFMonitorConstants.SOCKET_CHANNEL_NAME, "Kéx!");
        }
    }
}
