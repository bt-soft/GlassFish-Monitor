/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    CollectorSettingsView.java
 *  Created: 2018.01.06. 18:09:36
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.ui.view.settings;

import hu.btsoft.gfmon.corelib.model.entity.server.Server;
import hu.btsoft.gfmon.corelib.model.service.ServerService;
import hu.btsoft.gfmon.ui.view.ViewBase;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author BT
 */
@Named(value = "collectorSettingsView")
@ViewScoped
public class CollectorSettingsView extends ViewBase {

    @EJB
    private ServerService serverService;

    @Getter
    private List<Server> servers;

    @Getter
    @Setter
    private Long selectedServerId;

    @Getter
    @Setter
    private Server selectedServer;

    /**
     * Init
     */
    @PostConstruct
    protected void init() {
        servers = serverService.findAll();
        selectedServer = servers.get(0); //kiválasztjuk az elsőt
        selectedServerId = selectedServer.getId();
    }

    /**
     * A kiválasztott szerver összes cdu adatának aktív flagjának állítgatása
     *
     * @param newActiveFlag 'true'/'false'
     */
    public void allActiveFlagSetter(boolean newActiveFlag) {

        selectedServer.getCollectorDataUnits()
                .stream()
                .forEach((cdu) -> {
                    cdu.setActive(newActiveFlag);
                });
    }

    /**
     * Szerver kiválasztása az ID alapján
     */
    public void selectedServerChanged() {
        selectedServer = servers.stream()
                .parallel()
                .filter(server -> Objects.equals(server.getId(), selectedServerId))
                .findAny()
                .orElse(null);
    }

    /**
     * Beállítások mentése
     */
    public void saveSettings() {

        servers.stream().forEach((server) -> {
            serverService.save(server, currentUser);
        });

        addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Adatbázisba mentés OK");
    }
}
