/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationsDiscoverer.java
 *  Created: 2018.01.20. 9:58:16
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.management;

import hu.btsoft.gfmon.corelib.json.GFJsonUtils;
import hu.btsoft.gfmon.corelib.string.StrUtils;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.rest.RestClientBase;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.json.JsonObject;

/**
 * A szerver alkalmazás adatainak összegyűjtése
 * CDI Bean
 * <p>
 * 1) Lekérjük az alkalmazások listáját: http://localhost:4848/management/domain/applications/application/ - child resources
 * 2) alkalmazásonként leszedjük ajellemzőit (enabled, contetRoot, description) http://localhost:4848/management/domain/applications/application/TestEar
 * 3) alkalmazásonként megnézzük a modulokat: http://localhost:4848/management/domain/applications/application/TestEar/module
 * 4) modulonként megnézzük a modul motorjait (ejb, web, security, weld). Egy modul akkor EJB, ha nincs web modulja
 *
 *
 * @author BT
 */
public class ApplicationsDiscoverer extends RestClientBase {

    private static final String SUB_URL = "/management/domain/applications/application";

    /**
     * GF Szerver alkamazás neveinek listájának kigyűjtése
     *
     * @param simpleUrl    szerver url
     * @param userName     REST hívás usere
     * @param sessionToken GF session token
     *
     * @return A GF példány verzió adatai
     */
    private Map<String /* AppRealName */, String /* url */> getAppNamesUrlMap(String simpleUrl, String userName, String sessionToken) {
        //Válasz leszedése
        Map<String /* AppRealName */, String /* url */> map = GFJsonUtils.getChildResourcesMap(super.getRootJsonObject(simpleUrl, SUB_URL, userName, sessionToken));
        return map;
    }

    /**
     * Az alkalmazás részleteinek lekérdezése
     * <p>
     * 1) REST /management/domain/applications/application/{appname} URL-ről dolgozzuk fel
     * - contextRoot
     * - description
     * - enabled
     * <p>
     * 2) Leszedjük az alkalmazás modul neveit
     * - Ránézünk a REST /management/domain/applications/application/{appname}/module URL-re
     * - leszedjük a modul neveket
     * <p>
     * 3) Leszedjük a modulok engine-it:
     * - Ránézünk a REST /management/domain/applications/application/{appname}/module/{modulename}/engines URL-re
     * - leszedjük az engine neveket
     *
     * @param appNamesUrlEntry [az alkalmazás igazi neve, full URL] map entry
     * @param userName         REST hívás usere
     * @param sessionToken     GF session token
     */
    private List<Application> createApplications(Map.Entry<String /* AppRealName */, String /* url */> appNamesUrlEntry, String userName, String sessionToken) {

        //Válasz leszedése
        JsonObject rootJsonObject = super.getRootJsonObject(appNamesUrlEntry.getValue(), userName, sessionToken);
        JsonObject entities = GFJsonUtils.getEntities(rootJsonObject);
        if (entities == null) {
            return null;
        }

        String appRealName = StrUtils.deQuote(entities.get("name").toString());
        String ctxRoot = StrUtils.deQuote(entities.get("contextRoot").toString());
        String contextRoot = "null".equals(ctxRoot) ? null : ctxRoot;
        String description = StrUtils.deQuote(entities.get("description").toString());
        boolean enabled = Boolean.parseBoolean(StrUtils.deQuote(entities.get("enabled").toString()));

        List<Application> applications = new LinkedList<>();

        // -- Modulok leszedése
        String moduleUrl = GFJsonUtils.getChildResourcesValueByName(rootJsonObject, "module");
        Map<String /* appModuleName */, String /* url */> modulesMap = GFJsonUtils.getChildResourcesMap(super.getRootJsonObject(moduleUrl, userName, sessionToken));

        modulesMap.entrySet().stream().map((appModuleEntry) -> StrUtils.deQuote(appModuleEntry.getKey()))
                .map((appModuleRealName) -> {
                    String moduleEngineUrl = moduleUrl + "/" + appModuleRealName + "/engine";
                    Set<String> moduleEngines = GFJsonUtils.getChildResourcesKeys(super.getRootJsonObject(moduleEngineUrl, userName, sessionToken));

                    Application app = new Application();
                    app.setActive(false); //runtime összeszedett alkalmazást monitorozásra tiltottra teszünk default esetben

                    app.setAppRealName(appRealName);
                    app.setAppShortName(Application.createAppShortName(appRealName));
                    app.setModuleRealName(appModuleRealName);
                    app.setModuleShortName(Application.createAppShortName(appModuleRealName));
                    app.setModuleEngines(moduleEngines);
                    app.setContextRoot(contextRoot);
                    app.setDescription(description);
                    app.setEnabled(enabled);
                    return app;
                }).forEachOrdered((app) -> {
            applications.add(app);
        });

        return applications.isEmpty() ? null : applications;
    }

    /**
     * Az alkalmazások részletes adatainak listája
     *
     * @param simpleUrl    szerver URL-je
     * @param userName     rest user
     * @param sessionToken session token
     *
     * @return Az alkalmazások részletes adatai új(de inkább detached) entitásokban, vagy null
     */
    public List<Application> getServerAplications(String simpleUrl, String userName, String sessionToken) {

        //leszedjük az alkalmazások neveinek listáját
        Map<String /* AppRealName */, String /* url */> appNamesUrlMap = this.getAppNamesUrlMap(simpleUrl, userName, sessionToken);

        if (appNamesUrlMap == null) {
            return null;
        }

        List<Application> result = new LinkedList<>();

        appNamesUrlMap.entrySet().stream()
                .map((appNamesUrlEntry) -> this.createApplications(appNamesUrlEntry, userName, sessionToken))
                .filter((apps) -> (apps != null))
                .forEachOrdered((apps) -> {
                    result.addAll(apps);
                });
        return result.isEmpty() ? null : result;
    }
}
