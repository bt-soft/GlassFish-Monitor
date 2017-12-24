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
package hu.btsoft.gfmon.engine.measure;

import hu.btsoft.gfmon.engine.model.entity.Snapshot;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
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

    @Resource
    private TimerService timerService;

    private Timer timer;

    @Inject
    private SessionTokenAcquirer sessionTokenAcquirer;

    @Inject
    private SnapshotProvider snapshotProvider;

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

        //Ellenőrizzük, hogy a monitorozandó szervereknek egyáltalán be engedélyezve van, hogy nézegessük őket
        this.chechkServersMonitorServiceStatus();

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
     * A monitorozandó GF példányok MonitoringServeice (module-monitoring-levels) ellenőrzése
     * Amely szervernek nincs engedélyezve a monitorozhatósága, azt jól inaktívvá tesszük
     * http://localhost:4848/management/domain/configs/config/server-config/monitoring-service/module-monitoring-levels
     */
    private void chechkServersMonitorServiceStatus() {
        //Csak azokat nézzük át, amelyek jelenleg nézegetnénk
        serverService.findAll().stream().filter((server) -> (server.isActive())).forEachOrdered((server) -> {

        }
    }

    /**
     * A monitorozási mintavétel indítása
     */
    @Timeout
    protected void timeOut() {

        serverService.findAll().stream().filter((server) -> (server.isActive())).forEachOrdered((server) -> {
            String url = server.getUrl();
            String userName = server.getUserName();
            String plainPassword = server.getPlainPassword();

            log.trace("Mérés indul: {}", url);

            //ha még nincs SessionToken, akkor csinálunk egyet
            if (StringUtils.isEmpty(server.getSessionToken())) {
                String sessionToken = sessionTokenAcquirer.getSessionToken(url, userName, plainPassword);
                server.setSessionToken(sessionToken);
            }

            Snapshot snapshot = snapshotProvider.fetchSnapshot(server);
            log.trace("Snapshot: {}", snapshot);

            //snapshot.setServerId(server.getId());
//
//em.persist(snapshot);
//em.flush();
//em.detach(snapshot);
///eventBus.publish(IGFMonitorConstants.SOCKET_CHANNEL_NAME, "Kéx!");
        });
    }
}
