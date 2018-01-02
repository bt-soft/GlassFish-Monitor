/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    SettingsView.java
 *  Created: 2017.12.31. 22:25:59
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.ui.view;

import hu.btsoft.gfmon.core.jsf.GFMonJSFLib;
import hu.btsoft.gfmon.corelib.model.entity.Config;
import hu.btsoft.gfmon.corelib.model.entity.Server;
import hu.btsoft.gfmon.corelib.model.service.ConfigService;
import hu.btsoft.gfmon.corelib.model.service.ServerService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;

/**
 * Beállítások LAP JSF Managed Bean
 *
 * @author BT
 */
@Named(value = "settingsView")
@ViewScoped
@Slf4j
public class SettingsView extends ViewBase {

    @EJB
    private ConfigService configService;

    private List<Config> configs;

    @EJB
    private ServerService serverService;

    @Getter
    private List<Server> servers;

    /**
     * A táblázatban kiválasztott szerver (viewDetail)
     */
    @Getter
    @Setter
    private Server selectedServer;

    /**
     * A szerver adatainak módosítása (editDetail)
     */
    @Getter
    @Setter
    private Server modifiedServer;

    /**
     * JSF ManagedBean init
     */
    @PostConstruct
    protected void loadAllFromDb() {
        configs = configService.findAll();
        refreshServers();
    }

    /**
     * minden változó törlése
     */
    private void clearAll() {
        selectedServer = null;
        modifiedServer = null;
    }

    /**
     * Szerver adatok lekérése az adatbázisból
     */
    public void refreshServers() {
        servers = serverService.findAll();
    }

    /**
     * Konfigurációs entitás keresése
     *
     * @param keyName konfig kulcs neve
     *
     * @return Config entitás vagy null, ha nincs ilyen
     */
    private Config findConfig(String keyName) {
        return configs.stream()
                .filter(x -> keyName.equals(x.getKeyName()))
                .findFirst()
                .get();
    }

    /**
     * AutoStart lekérése
     *
     * @return true/false vagy null, ha nincs ilyen konfig
     */
    public Boolean getAutoStart() {

        Config config = findConfig(ConfigService.KEY_AUTOSTART);

        return config != null ? Boolean.parseBoolean(config.getKeyValue()) : null;
    }

    /**
     * AutoStart beállítása
     *
     * @param value beállítandó érték
     */
    public void setAutoStart(Boolean value) {
        Config config = findConfig(ConfigService.KEY_AUTOSTART);

        if (config != null) {
            config.setKeyValue(value.toString());
        }
    }

    /**
     * SampleInterval lekérése
     *
     * @return sec-ben a mintavétel, vagy null, ha nincs ilyen
     */
    public Integer getSampleInterval() {
        Config config = findConfig(ConfigService.KEY_SAMPLEINTERVAL);

        return config != null ? Integer.parseInt(config.getKeyValue()) : null;
    }

    /**
     * SampleInterval beállítása
     *
     * @param value új intervallum sec-ben
     */
    public void setSampleInterval(Integer value) {
        Config config = findConfig(ConfigService.KEY_SAMPLEINTERVAL);

        if (config != null) {
            config.setKeyValue(value.toString());
        }
    }

    /**
     * Konfig mentése
     */
    public void saveConfig() {

        String currentUser = GFMonJSFLib.getCurrentUser();

        try {
            //Config mentése
            configs.stream()
                    .forEach((Config config) -> {
                        configService.save(config, currentUser);
                    });

            //Szerverek mentése
            servers.stream()
                    .forEach((Server server) -> {
                        serverService.save(server, currentUser);
                    });

            //Újra betöltünk mindent
            clearAll();
            configs = null;
            servers = null;
            loadAllFromDb();

            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Beállítások adatbázisba mentése OK");

        } catch (PersistenceException e) {
            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, String.format("Konfig mentési hiba: %s", e.getMessage()));

        }
    }

    /**
     * Az aktuálisan editált modifiedServer host neve alapján kitalálja az IP címet
     * és beírja az modifiedServer példányba
     */
    public void fillIpByHostName() {
        //Ha nincs kitöltve a host
        if (modifiedServer == null || StringUtils.isEmpty(modifiedServer.getHostName())) {
            return;
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(modifiedServer.getHostName());
            modifiedServer.setHostName(inetAddress.getHostName());
            modifiedServer.setIpAddress(inetAddress.getHostAddress());

        } catch (UnknownHostException ex) {
            modifiedServer.setIpAddress(null);
            addJsfMessage("growl", FacesMessage.SEVERITY_WARN, String.format("Ismeretlen host név: %s!", modifiedServer.getHostName()));
        }

    }

    /**
     * A parancshandler
     *
     * @param cmd műveleti parancs
     */
    public void commandHandler(SettingsCommands cmd) {

        switch (cmd) {
            case COPY_SELECTED:
                if (selectedServer == null) {
                    break;
                }
                ModelMapper mapper = new ModelMapper();
                modifiedServer = new Server();
                mapper.map(selectedServer, modifiedServer);
                break;

            case SAVE_MODIFIED:
                servers.remove(selectedServer); //A korábbi szerver példányt töröljük a litából
                servers.add(modifiedServer); //Az editált szerver példány megy be helyette
                selectedServer = modifiedServer;    //ez lesz kiválasztva
                addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "A szerver módosítása OK");
                break;

            case DELETE_SELECTED:
                servers.remove(selectedServer); //A kiválasztot szerver példányt töröljük a listából
                selectedServer = null;
                addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "A szerver törlése OK");
                break;

            case CANCEL_MODIFY:
                modifiedServer = null;
                addJsfMessage("growl", FacesMessage.SEVERITY_WARN, "A szerver módosítása eldobva");
                break;
        }
    }
}
