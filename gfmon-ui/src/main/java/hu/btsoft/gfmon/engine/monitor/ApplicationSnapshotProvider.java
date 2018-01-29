/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationSnapshotProvider.java
 *  Created: 2018.01.21. 10:52:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.app.AppServletStatistic;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.app.AppStatistic;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbBeanCacheStat;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbBeanMethodStat;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbBeanPoolStat;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbStat;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbTimerStat;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.app.AppServletStatisticCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.app.AppWebStatisticCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.ejb.AppEjbBeanCacheStatCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.ejb.AppEjbBeanMethodCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.ejb.AppEjbBeanPoolCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.ejb.AppEjbCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.ejb.AppEjbTimersCollector;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy szerver összes alkalmazásainak monitoradat begyűjtését végző CDI bean
 *
 * @author BT
 */
@Slf4j
public class ApplicationSnapshotProvider {

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private AppWebStatisticCollector appWebStatisticCollector;

    @Inject
    private AppServletStatisticCollector appServletStatisticCollector;

    @Inject
    private AppEjbCollector appEjbCollector;

    @Inject
    private AppEjbBeanPoolCollector appEjbBeanPoolCollector;

    @Inject
    private AppEjbBeanMethodCollector appEjbBeanMethodCollector;

    @Inject
    private AppEjbTimersCollector appEjbTimersCollector;

    @Inject
    private AppEjbBeanCacheStatCollector appEjbBeanCacheStatCollector;

    @Inject
    private JSonEntityToSnapshotEntityMapper jSonEntityToSnapshotEntityMapper;

    private Set<DataUnitDto> collectDataUnits;

    /**
     * Alkalmazás path-jának kitalálása
     * Ha több modulból áll, akkor érdekes
     * <p>
     * Anomália:
     * TODO: ezzel még kell kezdeni valamit....
     * - http://localhost:4848/monitoring/domain/server/applications/TestEar-ear/TestEar-ejb-0_0_3.jar
     * - http://localhost:4848/monitoring/domain/server/applications/TestEar-ear/TestEar-web-0.0.3.war
     *
     * @param app alkalmazás DB entitás
     *
     * @return modul path
     */
    private String getModulePath(Application app) {
        String appRealName = app.getAppRealName();
        String appModuleRealName = app.getModuleRealName();

        if (appRealName.equals(appModuleRealName)) {
            return appRealName;
        }

        if (appModuleRealName.contains(".")) {
            int lastIndex = appModuleRealName.lastIndexOf(".");
            String begin = appModuleRealName.substring(0, lastIndex);
            String end = appModuleRealName.substring(lastIndex, appModuleRealName.length());

            appModuleRealName = begin.replaceAll("\\.", "_");
            appModuleRealName += end;
        }

        return appRealName + "/" + appModuleRealName;
    }

    /**
     * Alkalmazás WEB statisztika kigyűjtése
     *
     * @param app          alkalmazás DB entitás
     * @param fullUrl      teljes URL
     * @param sessionToken session token
     * @param userName     user neve
     *
     * @return Web statisztika entitások vagy null
     */
    private Set<AppSnapshotBase> collectWebStatistics(Application app, String fullUrl, String userName, String sessionToken) {
        Set<AppSnapshotBase> snapshots = new HashSet<>();

        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(fullUrl, userName, sessionToken);
        List<CollectedValueDto> valuesList = appWebStatisticCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

        //Ha kell dataUnitokat is gyűjteni
        if (collectDataUnits != null) {
            List<DataUnitDto> dataUnits = appWebStatisticCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
            if (dataUnits != null && !dataUnits.isEmpty()) {
                collectDataUnits.addAll(dataUnits);
            }
        }

        AppStatistic appStatistic = (AppStatistic) jSonEntityToSnapshotEntityMapper.map(valuesList);
        if (appStatistic != null) {
            appStatistic.setApplication(app);
            snapshots.add(appStatistic);
        }

        //Miylen Servlet-jei vannak?
        Map<String, String> servletsMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        if (servletsMap != null && !servletsMap.isEmpty()) {
            for (String servletName : servletsMap.keySet()) {

                String serrvletFullUrl = servletsMap.get(servletName);

                rootJsonObject = restDataCollector.getRootJsonObject(serrvletFullUrl, userName, sessionToken);
                valuesList = appServletStatisticCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                //Ha kell dataUnitokat is gyűjteni
                if (collectDataUnits != null) {
                    List<DataUnitDto> dataUnits = appServletStatisticCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                    if (dataUnits != null && !dataUnits.isEmpty()) {
                        collectDataUnits.addAll(dataUnits);
                    }
                }

                AppServletStatistic appServletStatistic = (AppServletStatistic) jSonEntityToSnapshotEntityMapper.map(valuesList);

                if (appServletStatistic != null) {
                    appServletStatistic.setServletName(servletName); //A szervlet neve
                    appServletStatistic.setAppStatistic(appStatistic); //melyik alkalmazás statisztikához tartozik
                    snapshots.add(appServletStatistic);
                }
            }
        }

        return snapshots.isEmpty() ? null : snapshots;
    }

    /**
     * Alkalmazás EJB statisztika kigyűjtése
     *
     * @param app          alkalmazás DB entitás
     * @param fullUrl      teljes URL
     * @param sessionToken session token
     * @param userName     user neve
     * @param beanName     Bean neve
     *
     * @return EJB statisztika entitások vagy null
     */
    private Set<AppSnapshotBase> collectEjbStatistics(Application app, String fullUrl, String userName, String sessionToken, String beanName) {
        Set<AppSnapshotBase> snapshots = new HashSet<>();

        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(fullUrl, userName, sessionToken);
        List<CollectedValueDto> valuesList = appEjbCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

        //Ha kell dataUnitokat is gyűjteni
        if (collectDataUnits != null) {
            List<DataUnitDto> dataUnits = appEjbCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
            if (dataUnits != null && !dataUnits.isEmpty()) {
                collectDataUnits.addAll(dataUnits);
            }
        }

        EjbStat ejbStat = (EjbStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
        if (ejbStat == null) {
            log.warn("Null az '{}' alkalmazás '{}' ejbStatisztikája!", app, fullUrl);
            return null;
        }

        ejbStat.setApplication(app);
        ejbStat.setEjbName(beanName);
        snapshots.add(ejbStat);

        //Milyen más EJB statisztikái vannak?
        Map<String, String> ejbStatisticsMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        if (ejbStatisticsMap != null && !ejbStatisticsMap.isEmpty()) {
            for (String ejbStatName : ejbStatisticsMap.keySet()) {

                String ejbStatFullUrl = ejbStatisticsMap.get(ejbStatName);
                rootJsonObject = restDataCollector.getRootJsonObject(ejbStatFullUrl, userName, sessionToken);

                switch (ejbStatName) {
                    case "bean-methods":
                        Map<String, String> beanMethodsMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
                        if (beanMethodsMap != null && !beanMethodsMap.isEmpty()) {
                            for (String beanMethodName : beanMethodsMap.keySet()) {
                                String beanMethodFullUrl = beanMethodsMap.get(beanMethodName);
                                rootJsonObject = restDataCollector.getRootJsonObject(beanMethodFullUrl, userName, sessionToken);
                                valuesList = appEjbBeanMethodCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                                //Ha kell dataUnitokat is gyűjteni
                                if (collectDataUnits != null) {
                                    List<DataUnitDto> dataUnits = appEjbBeanMethodCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                                    if (dataUnits != null && !dataUnits.isEmpty()) {
                                        collectDataUnits.addAll(dataUnits);
                                    }
                                }

                                EjbBeanMethodStat ejbBeanMethodStat = (EjbBeanMethodStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
                                if (ejbBeanMethodStat != null) {
                                    ejbBeanMethodStat.setMethodName(beanMethodName);
                                    ejbBeanMethodStat.setEjbStat(ejbStat);
                                    if (ejbStat.getEjbBeanMethodStats() == null) {
                                        ejbStat.setEjbBeanMethodStats(new LinkedList<>());
                                    }
                                    ejbStat.getEjbBeanMethodStats().add(ejbBeanMethodStat);

                                    snapshots.add(ejbBeanMethodStat);
                                }
                            }
                            break;
                        }

                    case "bean-pool":
                        valuesList = appEjbBeanPoolCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                        //Ha kell dataUnitokat is gyűjteni
                        if (collectDataUnits != null) {
                            List<DataUnitDto> dataUnits = appEjbBeanPoolCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                            if (dataUnits != null && !dataUnits.isEmpty()) {
                                collectDataUnits.addAll(dataUnits);
                            }
                        }

                        EjbBeanPoolStat ejbBeanPoolStat = (EjbBeanPoolStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
                        if (ejbBeanPoolStat != null) {
                            ejbBeanPoolStat.setEjbStat(ejbStat);
                            if (ejbStat.getEjbBeanPoolStats() == null) {
                                ejbStat.setEjbBeanPoolStats(new LinkedList<>());
                            }
                            ejbStat.getEjbBeanPoolStats().add(ejbBeanPoolStat);

                            snapshots.add(ejbBeanPoolStat);
                        }
                        break;

                    case "timers":
                        valuesList = appEjbTimersCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                        //Ha kell dataUnitokat is gyűjteni
                        if (collectDataUnits != null) {
                            List<DataUnitDto> dataUnits = appEjbTimersCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                            if (dataUnits != null && !dataUnits.isEmpty()) {
                                collectDataUnits.addAll(dataUnits);
                            }
                        }

                        EjbTimerStat ejbTimersStat = (EjbTimerStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
                        if (ejbTimersStat != null) {
                            ejbTimersStat.setEjbStat(ejbStat);
                            if (ejbStat.getEjbTimersStats() == null) {
                                ejbStat.setEjbTimersStats(new LinkedList<>());
                            }
                            ejbStat.getEjbTimersStats().add(ejbTimersStat);

                            snapshots.add(ejbTimersStat);
                        }
                        break;

                    case "bean-cache":
                        valuesList = appEjbBeanCacheStatCollector.fetchValues(GFJsonUtils.getEntities(rootJsonObject), null);

                        //Ha kell dataUnitokat is gyűjteni
                        if (collectDataUnits != null) {
                            List<DataUnitDto> dataUnits = appEjbBeanCacheStatCollector.fetchDataUnits(GFJsonUtils.getEntities(rootJsonObject));
                            if (dataUnits != null && !dataUnits.isEmpty()) {
                                collectDataUnits.addAll(dataUnits);
                            }
                        }

                        EjbBeanCacheStat ejbBeanCacheStat = (EjbBeanCacheStat) jSonEntityToSnapshotEntityMapper.map(valuesList);
                        if (ejbBeanCacheStat != null) {
                            ejbBeanCacheStat.setEjbStat(ejbStat);
                            if (ejbStat.getEjbBeanCacheStat() == null) {
                                ejbStat.setEjbBeanCacheStat(new LinkedList<>());
                            }
                            ejbStat.getEjbBeanCacheStat().add(ejbBeanCacheStat);
                            snapshots.add(ejbBeanCacheStat);
                        }
                        break;

                    default:
                        log.warn("Nincs lekezelve a(z) '{}' bean statisztika! (fullUrl: '{}')", ejbStatName, ejbStatFullUrl);
                }
            }
        }

        return snapshots.isEmpty() ? null : snapshots;
    }

    /**
     * Egy szerver egy alkalmazás teljes statisztika kigyűjtése
     *
     *
     * @param app Application DB entitás
     *
     * @return kigyűjtött adatok
     */
    private Set<AppSnapshotBase> start(Application app) {

        Set<AppSnapshotBase> snapshots = null;

        Server server = app.getServer();
        String simpleUrl = server.getSimpleUrl();
        String userName = server.getUserName();
        String sessionToken = server.getSessionToken();

        //Megnézzük, hogy milyen statisztikái vannak
        String resourceUri = restDataCollector.getSubUri() + "applications/" + this.getModulePath(app);
        JsonObject rootJsonObject = restDataCollector.getRootJsonObject(simpleUrl, resourceUri, userName, sessionToken);
        Map<String/* 'server', vagy a bean neve */, String /* full URL */> childResourcesMap = GFJsonUtils.getChildResourcesMap(rootJsonObject);
        if (childResourcesMap == null || childResourcesMap.isEmpty()) {
            return null;
        }

        for (String key : childResourcesMap.keySet()) {

            Set<AppSnapshotBase> statistics;

            if ("server".equals(key)) {
                statistics = this.collectWebStatistics(app, childResourcesMap.get(key), userName, sessionToken);
            } else {
                statistics = this.collectEjbStatistics(app, childResourcesMap.get(key), userName, sessionToken, key);
            }

            if (statistics != null && !statistics.isEmpty()) {
                if (snapshots == null) {
                    snapshots = new HashSet<>();
                }

                snapshots.addAll(statistics);
            }
        }

        return snapshots;
    }

    /**
     * Az összes alkalmazás kollektor adatait összegyűjti, majd egy új alkalmazás Snapshot entitásba rakja az eredményeket
     *
     * @param server    a monitorozandó Server entitása
     * @param dataUnits ha nem null, akko ki kell gyűjteni a mért értékek mértékegységét is
     *
     * @return alkalmazás Snapshot példányok halmaza, az adatgyűjtés eredménye (new/detach entitás)
     */
    public Set<AppSnapshotBase> fetchSnapshot(Server server, Set<DataUnitDto> dataUnits) {

        this.collectDataUnits = dataUnits;

        long start = Elapsed.nowNano();

        Set<AppSnapshotBase> snapshots = null;

        //Véégigmegyünk a szerver alkalmazásain
        for (Application app : server.getApplications()) {

            //Ha monitorozásra aktív, akkor meghívjuk rá az adatgyűjtőt
            if (app.getActive() != null && Objects.equals(app.getActive(), Boolean.TRUE)) {

                Set<AppSnapshotBase> appSnapshots = this.start(app);

                if (appSnapshots != null && !appSnapshots.isEmpty()) {

                    if (snapshots == null) {
                        snapshots = new LinkedHashSet<>();
                    }
                    snapshots.addAll(appSnapshots);
                }
            }
        }

        log.info("Alkalmazások statisztika kigyűjtése elapsed: {}", Elapsed.getElapsedNanoStr(start));

        return snapshots;
    }

}
