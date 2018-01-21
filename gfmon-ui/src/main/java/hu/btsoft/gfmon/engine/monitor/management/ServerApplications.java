/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ServerApplications.java
 *  Created: 2018.01.20. 9:58:16
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.management;

import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.monitor.IndependentRestClientBase;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.json.JsonObject;

/**
 * A szerver alkalmazás listájának lekérdezése
 *
 * pl.:
 * http://localhost:4848/management/domain/applications/application
 *
 * @author BT
 */
public class ServerApplications extends IndependentRestClientBase {

    private static final String SUB_URL = "/management/domain/applications/application";

    /**
     * GF Szerver verzió adatok kigyűjtése
     *
     * @param simpleUrl    a GF szerver URL-je
     * @param sessionToken session token
     *
     * @return A GF példány verzió adatai
     */
    private Set<String> getApplicationsList(String simpleUrl, String sessionToken) {

        //Válasz leszedése
        JsonObject jsonObject = super.getJsonObject(simpleUrl, SUB_URL, sessionToken);
        if (jsonObject == null) {
            return null;
        }

        //extraProperties leszedése
        JsonObject extraProperties = jsonObject.getJsonObject("extraProperties");
        if (extraProperties == null) {
            return null;
        }

        JsonObject childResources = extraProperties.getJsonObject("childResources");

        if (childResources == null) {
            return null;
        }

        return childResources.keySet();
    }

    /**
     * Az alkalmazás részletes adatai új(de inkább detached) entitásban
     * A REST /management/domain/applications/application/{appname} URL-ről dolgozzuk fel
     *
     * @param simpleUrl    szerver url
     * @param sessionToken session token
     * @param appRealName  alklamazás neve
     *
     * @return alkalmazás részletes adatai
     */
    private Application getApplicationDetails(String simpleUrl, String sessionToken, String appRealName) {

        //Válasz leszedése
        JsonObject jsonObject = super.getJsonObject(simpleUrl, SUB_URL + "/" + appRealName, sessionToken);
        if (jsonObject == null) {
            return null;
        }

        //extraProperties leszedése
        JsonObject extraProperties = jsonObject.getJsonObject("extraProperties");
        if (extraProperties == null) {
            return null;
        }

        JsonObject childResources = extraProperties.getJsonObject("entity");

        //String name = childResources.getJsonString("name").getString();
        String contextRoot = childResources.get("contextRoot").toString();
        String description = childResources.get("description").toString();
        boolean enabled = Boolean.parseBoolean(childResources.get("enabled").toString());

        Application app = new Application();
        app.setAppRealName(appRealName);
        app.setAppShortName(Application.createAppShortName(appRealName));
        app.setContextRoot(contextRoot);
        app.setDescription(description);
        app.setEnabled(enabled);

        return app;

    }

    /**
     * Az alkalmazások részletes adatainak listája
     *
     * @param simpleUrl    szerver URL-je
     * @param sessionToken session token
     *
     * @return Az alkalmazások részletes adatai új(de inkább detached) entitásokban
     */
    public List<Application> getServerAplications(String simpleUrl, String sessionToken) {
        //leszedjük az alkalmazások listáját
        Set<String> appNames = this.getApplicationsList(simpleUrl, sessionToken);

        if (appNames == null) {
            return null;
        }

        List<Application> result = new LinkedList<>();

        appNames.stream()
                .map((appRealName) -> this.getApplicationDetails(simpleUrl, sessionToken, appRealName))
                .forEachOrdered((app) -> {
                    result.add(app);
                });

        return result;

    }
}
