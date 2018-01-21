/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationsCollector.java
 *  Created: 2018.01.21. 13:32:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application;

import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.monitor.JSonEntityToApplicationSnapshotEntityMapper;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * CollectorBase leszármazottja, hogy tudjon REST hívásoka küldözgetni a szerver felé,
 * hogy meg tudja állapítani, hogy milyen kollektort is kellene/lehet indítani
 *
 * @author BT
 */
@Slf4j
public class ApplicationsCollector extends CollectorBase {

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JSonEntityToApplicationSnapshotEntityMapper jSonEntityToApplicationSnapshotEntityMapper;

    @Inject
    private Instance<IAppServerCollector> appServerCollectors;

    @Override
    public String getPath() {
        return null; //Itt nem használjuk
    }

    /**
     * Egy allamzás adatainak összegyűjtése
     *
     * @param server
     * @param appRealName
     *
     * @return
     */
    private Set<AppSnapshotBase> startCollectors(String simpleUrl, String sessionToken, String appRealName) {

        //Lekérdezzük az alkalmazás 'childResources'-ét
        String appPath = String.format("/applications/%s", appRealName);
        Response response = restDataCollector.getMonitorResponse(appPath, simpleUrl, sessionToken);
        Set<String> childResourcesKeys = restDataCollector.getChildResourcesKeys(response);

        if (childResourcesKeys == null) {
            return null;
        }

        Set<AppSnapshotBase> snapshots = null;

        List<CollectedValueDto> valuesList = null;
        for (String key : childResourcesKeys) {
            if ("server".equals(key)) {
                //Server path cuccok
                valuesList = appServerCollectors.get().execute(restDataCollector, simpleUrl, appRealName, sessionToken);

//        //Megnézzük, hoghy vannak-e gyermek objektumok, és jól lekérdezzük őket
//        Set<String> childResourcesKeys = JsonUtils.getChildResourcesKeys(extraProperties);
//        if (childResourcesKeys != null && !childResourcesKeys.isEmpty()) {
//            for (String key : childResourcesKeys) {
//                this.subPath = subPath + "/" + key;
//                response = restDataCollector.getMonitorResponse(this.getPathWithRealAppName(), simpleUrl, sessionToken);
//                extraProperties = restDataCollector.getExtraProperties(response);
//                resultMap = this.fetchValues(JsonUtils.getJsonEntities(extraProperties), resultMap, this.subPath);
//            }
//        }
            } else {
                //EJB cuccok

            }

            //Üres a mért eredmények Map-je
            if (valuesList == null || valuesList.isEmpty()) {
                log.warn("A(z) '{}' szerver '{}' alkalmazásának '{}' mérési eredményei üresek!", simpleUrl, appRealName, key);
                continue;
            }

            //Betoljuk az eredményeket a snapshot entitásba
            if (snapshots == null) {
                snapshots = new HashSet<>();
            }
            jSonEntityToApplicationSnapshotEntityMapper.map(valuesList, snapshots);

        }

        return snapshots;
    }

    /**
     * Egy szerver egy alkalmazásadatainak a kigyűjtése
     *
     * @param simpleUrl    szevrer url
     * @param sessionToken session token
     * @param appRealName  akalmazás igazi neve
     *
     * @return kigyűjtött adatok
     */
    public Set<AppSnapshotBase> start(String simpleUrl, String sessionToken, String appRealName) {

        Set<AppSnapshotBase> snapshots = this.startCollectors(simpleUrl, sessionToken, appRealName);
        return snapshots;
    }

}
