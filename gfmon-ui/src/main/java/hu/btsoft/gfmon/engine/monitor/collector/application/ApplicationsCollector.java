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

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.server.ApplicationServer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.server.ApplicationServerSubComponent;
import hu.btsoft.gfmon.engine.monitor.JSonEntityToApplicationSnapshotEntityMapper;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.RestDataCollector;
import hu.btsoft.gfmon.engine.monitor.collector.application.server.AppServerCollector;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

/**
 * CollectorBase leszármazottja, hogy tudjon REST hívásoka küldözgetni a szerver felé,
 * hogy meg tudja állapítani, hogy milyen kollektort is kellene/lehet indítani
 *
 * @author BT
 */
@Slf4j
public class ApplicationsCollector {

    public static final String APP_SERVER_TOKENIZED_PATH = "/applications/{appRealName}/server";
    public static final String APP_SERVER_CHILDRESOURCES_TOKENIZED_PATH = "/applications/{appRealName}/server/{childResourcesPath}";

    @Inject
    private RestDataCollector restDataCollector;

    @Inject
    private JSonEntityToApplicationSnapshotEntityMapper jSonEntityToApplicationSnapshotEntityMapper;

    @Inject
    private AppServerCollector appServerCollector;

    private boolean inRecursiveCall;

    /**
     * Egy alkalamazás "server/*" dolgainak összeszedése
     *
     * @param simpleUrl    simple url
     * @param sessionToken session token
     *
     * @return snapshot halmaz, vagy null
     */
    private Set<AppSnapshotBase> collectServerSnapshots(String simpleUrl, String sessionToken, String appName) {

        Set<AppSnapshotBase> snapshots = new HashSet<>();

        //Server path cuccok
        Map<String, String> uriParams = new HashMap<>();
        uriParams.put("{appRealName}", appName);
        List<CollectedValueDto> valuesList = appServerCollector.execute(restDataCollector, simpleUrl, sessionToken, APP_SERVER_TOKENIZED_PATH, uriParams);

        ApplicationServer appServerSnapshot = (ApplicationServer) jSonEntityToApplicationSnapshotEntityMapper.map(valuesList);
        if (appServerSnapshot != null) {
            appServerSnapshot.setPathSuffix("server");
            snapshots.add(appServerSnapshot);
        }

        //Megnézzük, hogy vannak-e gyermek objektumok, és jól lekérdezzük őket
        String resourceUri = String.format("/applications/%s/server", appName);
        Response response = restDataCollector.getMonitorResponse(resourceUri, simpleUrl, sessionToken);
        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {} url hívására {} hibakód jött", simpleUrl, response.getStatusInfo().getReasonPhrase());
            return null;
        }

        //JSon válasz leszedése
        JsonObject rootJsonObject = response.readEntity(JsonObject.class);
        Set<String> childResourcesKeys = GFJsonUtils.getChildResourcesKeys(rootJsonObject);

        if (childResourcesKeys != null && !childResourcesKeys.isEmpty()) {
            for (String childResourcesPath : childResourcesKeys) {

                uriParams.clear();
                uriParams.put("{appRealName}", appName);
                uriParams.put("{childResourcesPath}", childResourcesPath);
                valuesList = appServerCollector.execute(restDataCollector, simpleUrl, sessionToken, APP_SERVER_CHILDRESOURCES_TOKENIZED_PATH, uriParams);

                ApplicationServerSubComponent appServerChildSnapshot = (ApplicationServerSubComponent) jSonEntityToApplicationSnapshotEntityMapper.map(valuesList);
                if (appServerChildSnapshot != null) {
                    appServerChildSnapshot.setPathSuffix("server/" + childResourcesPath);
                    appServerChildSnapshot.setApplicationServer(appServerSnapshot);
                }

                snapshots.add(appServerChildSnapshot);
            }
        }

        //Üres a mért eredmények?
        if (snapshots.isEmpty()) {
            log.warn("A(z) '{}' szerver '{}' alkalmazásának 'server' mérési eredményei üresek!", simpleUrl, appName);
            return null;
        }

        return snapshots;
    }

    /**
     * Egy alkalmzás adatainak összegyűjtése
     * (rekurzív hivás!)
     *
     * @param simpleUrl    simlpe url
     * @param sessionToken session token
     * @param appRealName  alkalmazás igazi neve (változhat, pl EAR esetén)
     * @param snapshots    pillenetfelvétel új entitások
     */
    private void startCollectors(String simpleUrl, String sessionToken, String appRealName, Set<AppSnapshotBase> snapshots) {

        //Lekérdezzük az alkalmazás 'childResources'-ét
        String resourceUri = String.format("/applications/%s", appRealName);
        Response response = restDataCollector.getMonitorResponse(resourceUri, simpleUrl, sessionToken);
        //Response státuszkód ellenőrzése
        if (response.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            log.warn("A(z) {} url hívására {} hibakód jött", simpleUrl, response.getStatusInfo().getReasonPhrase());
            return;
        }

        //JSon válasz leszedése
        JsonObject rootJsonObject = response.readEntity(JsonObject.class);
        Set<String> childResourcesKeys = GFJsonUtils.getChildResourcesKeys(rootJsonObject);
        if (childResourcesKeys == null) {
            return;
        }

        //Meg kell nézni, hogy van-e "server" childResourcesKey
        // - Ha van, akkor ez egy 'sima' webes alkalmazás, mehetünk tovább
        // - Ha nincs, akkor ez egy EAR
        // -- 1) meg kell hívni rekurzívan magunkat
        // -- 2) itt sem lesz server, hanem egy .jar és/vagy egy .war  -> reccnt = 1
        // -- 3) meg kell hívni rekurzívan magunkat -> reccnt = 2
        // -- 4) Ha van "server" kulcs akkor ez egy .war
        // -- 5) Ha nincs, akkor ez egy EJB és jelet a Bean dolgokat kivadászni
        //
        if (!inRecursiveCall && !childResourcesKeys.contains("server")) {
            for (String key : childResourcesKeys) {
                String subAppName = String.format("%s/%s", appRealName, key);
                //Rekurzív hívás!
                inRecursiveCall = true;
                startCollectors(simpleUrl, sessionToken, subAppName, snapshots);
                inRecursiveCall = false;
                return;
            }
        }

        childResourcesKeys.forEach((key) -> {
            if ("server".equals(key)) {
                //Server path cuccok
                Set<AppSnapshotBase> collectedServerSnapshots = collectServerSnapshots(simpleUrl, sessionToken, appRealName);
                if (collectedServerSnapshots != null) {
                    snapshots.addAll(collectedServerSnapshots);
                }
            } else {
                //EJB cuccok
                log.trace("Bean -> appRealName: {}, key: {}", appRealName, key);
            }
        });

    }

    /**
     * Egy szerver egy alkalmazás adatainak a kigyűjtése
     *
     * @param simpleUrl    szevrer url
     * @param sessionToken session token
     * @param appRealName  akalmazás igazi neve
     *
     * @return kigyűjtött adatok
     */
    public Set<AppSnapshotBase> start(String simpleUrl, String sessionToken, String appRealName) {

        Set<AppSnapshotBase> snapshots = new HashSet<>();
        this.startCollectors(simpleUrl, sessionToken, appRealName, snapshots);
        return snapshots.isEmpty() ? null : snapshots;
    }

}
