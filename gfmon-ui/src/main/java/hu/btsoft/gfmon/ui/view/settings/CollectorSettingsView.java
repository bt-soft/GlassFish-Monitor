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

import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.ServerCollDataUnitJoiner;
import hu.btsoft.gfmon.engine.model.service.ServerService;
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
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Named(value = "collectorSettingsView")
@ViewScoped
@Slf4j
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
        if (servers != null && !servers.isEmpty()) {
            selectedServer = servers.get(0); //kiválasztjuk az elsőt
            selectedServerId = selectedServer.getId();
            sortSelectedServerJoinersList();
        }
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

        sortSelectedServerJoinersList();
    }

    /**
     * A kiválasztott szerver joiner tábláját lerendezzük a DCU-k path és adatnevei szerint
     */
    private void sortSelectedServerJoinersList() {

        selectedServer.getJoiners().sort((o1, o2) -> {
            ServerCollDataUnitJoiner j1 = (ServerCollDataUnitJoiner) o1;
            ServerCollDataUnitJoiner j2 = (ServerCollDataUnitJoiner) o2;

            //Először a Path szerint hasonlítjuk össze
            int result = j1.getCollectorDataUnit().getRestPath().compareTo(j2.getCollectorDataUnit().getRestPath());

            //Ha a Path azonos, akkor az adatnév szerint hasonlítunk
            if (result == 0) {
                result = j1.getCollectorDataUnit().getDataName().compareTo(j2.getCollectorDataUnit().getDataName());
            }

            return result;
        });
    }

    /**
     * A kiválasztott szerver összes cdu adatának aktív flagjának állítgatása
     *
     * @param newActiveFlag 'true'/'false'
     */
    public void allActiveFlagSetter(boolean newActiveFlag) {

        selectedServer.getJoiners()
                .forEach((joiner) -> {
                    joiner.setActive(newActiveFlag);
                });
    }

    /**
     * Beállítások mentése
     */
    public void saveSettings() {

        servers.forEach((server) -> {
            serverService.updateJoiners(server, currentUser);
        });

        //Újra betöltjük az adatbázisból
        servers = serverService.findAll();
        this.selectedServerChanged();

        addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Adatbázisba mentés OK");
    }

}
