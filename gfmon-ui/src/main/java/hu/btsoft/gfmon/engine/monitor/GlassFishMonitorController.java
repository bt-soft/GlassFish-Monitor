/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    GlassFishMonitorController.java
 *  Created: 2018.01.21. 9:42:45
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.cdi.CdiUtils;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.inject.Instance;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Singleton
@Startup
@DependsOn("Bootstrapper")
@Slf4j
@RolesAllowed("APP_GFMON_ADMIN")
@ConcurrencyManagement(ConcurrencyManagementType.BEAN) //Ne legyen lock a UI felületen, a mérés közben
@Lock(LockType.WRITE)
public class GlassFishMonitorController {

    @EJB
    protected ConfigService configService;

    @Resource
    private TimerService timerService;

    protected Timer timer;

    //  A singleton miatt nem célszerű itt injektálni,
    // mert nem érvényesül a StateLess, inkább singleton lesz az is ...
//    @Inject
//    private Instance<MonitorsBase> monitors;
//
    /**
     * GFMon engine indítása
     */
    @PostConstruct
    protected void init() {

        if (!configService.getBoolean(ConfigKeyNames.AUTOSTART)) {
            log.debug("Az automatikus indítás kikapcsolva");
            return;
        }

        log.trace("Mérés indul");
        startTimer();
    }

    /**
     * Timer indítása
     */
    public void startTimer() {

        if (isRunningTimer()) {
            log.warn("A Timer már fut!", timer);
            return;

        }

        //A singleton vezérlés miatt inkább mindig lookup-olunk, mert csak így lesz StateLess a monitor vezérlő
        Instance<MonitorsBase> monitors = CdiUtils.lookupAll(MonitorsBase.class
        );
        if (monitors != null) {
            monitors.forEach((monitor) -> {
                monitor.beforeStartTimer();
            });
        }

        //Mérési periódusidő leszedése a konfigból
        int sampleIntervalSec = configService.getInteger(ConfigKeyNames.SAMPLE_INTERVAL);

        //Timer felhúzása
        this.timer = this.timerService.createIntervalTimer(5_000, // 5mp múlva induljon az adatgyűjtés
                sampleIntervalSec * 1_000, // ennyi másodpercenként mérünk
                new TimerConfig("GFMon-Timer", false) //ne legyen perzisztens a timer!
        );

        log.trace("A(z) {} timer felhúzva {} másodpercenként", this.timer, sampleIntervalSec);
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
            log.trace("A(z) {} timer leállítva", this.timer);
        } catch (IllegalStateException | EJBException e) {
            log.error("Nem állítható le a Timer: {}", this.timer, e);
        } finally {
            this.timer = null;
        }

        //A singleton vezérlés miatt inkább mindig lookup-olunk, mert csak így lesz StateLess a monitor vezérlő
        Instance<MonitorsBase> monitors = CdiUtils.lookupAll(MonitorsBase.class);
        if (monitors != null) {
            monitors.forEach((monitor) -> {
                monitor.afterStopTimer();
            });
        }
        log.trace("Monitorozó példányok leállítva");
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
    @RolesAllowed("APP_GFMON_USER")
    @Lock(LockType.READ)
    public boolean isRunningTimer() {
        return (this.timer != null);
    }

    /**
     * A monitorozási mintavétel indítása
     */
    @Timeout
    protected void timeOut() {

        //long start = Elapsed.nowNano();
        log.trace("----- Monitoring start --------------------------------------------------------------------------------");

        //A singleton vezérlés miatt inkább mindig lookup-olunk, mert csak így lesz StateLess a monitor vezérlő
        Instance<MonitorsBase> monitors = CdiUtils.lookupAll(MonitorsBase.class);
        if (monitors != null) {
            monitors.forEach((monitor) -> {
                try {
                    monitor.startMonitoring();
                } catch (Exception e) {
                    log.error(String.format("%s -> Hiba a monitorozott adatok begyűjtése közben", monitor.getControllerName()), e);
                }
            });
        }

        //log.trace("----- Monitoring End, elapsed: {} ---------------------------", Elapsed.getElapsedNanoStr(start));
    }

    /**
     * Automatikus takarítás minden nap éjfélkor fut le
     */
    @Schedule(hour = "00", minute = "00", second = "00")
    protected void doDailyPeriodicCleanup() {

        //A singleton vezérlés miatt inkább mindig lookup-olunk, mert csak így lesz StateLess a monitor vezérlő
        Instance<MonitorsBase> monitors = CdiUtils.lookupAll(MonitorsBase.class
        );
        if (monitors != null) {
            monitors.forEach((monitor) -> {
                try {
                    monitor.dailyJob();
                } catch (Exception e) {
                    log.error(String.format("%s -> Hiba a napi takarítás közben", monitor.getControllerName()), e);
                }
            });
        }

    }
}
