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
package hu.btsoft.gfmon.ui.view.settings;

import hu.btsoft.gfmon.corelib.model.RuntimeSequenceGenerator;
import hu.btsoft.gfmon.corelib.model.entity.Config;
import hu.btsoft.gfmon.corelib.model.entity.server.Server;
import hu.btsoft.gfmon.corelib.model.service.ConfigService;
import hu.btsoft.gfmon.corelib.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.corelib.model.service.ServerService;
import hu.btsoft.gfmon.ui.view.ViewBase;
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
import org.primefaces.context.RequestContext;

/**
 * Beállítások LAP JSF Managed Bean
 *
 * @author BT
 */
@Named(value = "settingsView")
@ViewScoped
@Slf4j
public class SettingsView extends ViewBase {

    // --- Config -----------------------------------
    @EJB
    private ConfigService configService;

    private List<Config> configs;

    @Getter
    @Setter
    private Boolean configAutoStart;

    @Getter
    @Setter
    private Integer configSampleInterval;

    @Getter
    @Setter
    private Integer configSampleDataKeepDays;

    // --- Server -----------------------------------
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
    private Server editedServer;

    /**
     * editServerDialog fejléc szövege
     */
    @Getter
    private String editServerDialogHeaderText;

    /**
     * Változott valamilyen adat, amit menteni kellene?
     */
    @Getter
    private boolean settingsDataChanged;

    /**
     * Módosítás van folyamatban?
     */
    private boolean underModifyProcess;

    /**
     * JSF ManagedBean init
     */
    @PostConstruct
    protected void loadAllFromDb() {

        //Konfig rekordok betöltése
        configs = configService.findAll();

        Config config = findConfig(IConfigKeyNames.AUTOSTART);
        configAutoStart = config != null ? Boolean.parseBoolean(config.getKeyValue()) : null;

        config = findConfig(IConfigKeyNames.SAMPLE_INTERVAL);
        configSampleInterval = config != null ? Integer.parseInt(config.getKeyValue()) : null;

        config = findConfig(IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
        configSampleDataKeepDays = config != null ? Integer.parseInt(config.getKeyValue()) : null;

        //Szerverek betöltése
        refreshServers();
    }

    /**
     * minden változó törlése
     */
    private void clearAll() {

        configs = null;
        configAutoStart = null;
        configSampleInterval = null;
        configSampleDataKeepDays = null;

        servers = null;
        selectedServer = null;
        editedServer = null;
        editServerDialogHeaderText = null;
        settingsDataChanged = false;
    }

    /**
     * Szerver adatok lekérése az adatbázisból
     */
    public void refreshServers() {
        servers = serverService.findAllAndSetRuntimeSeqId();
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
     * Beállítások mentése
     */
    public void saveSettings() {

        {//A mentendő Config rekord beállítása

            //SampleInterval beállítása
            Config config = findConfig(IConfigKeyNames.SAMPLE_INTERVAL);
            if (config != null) {
                config.setKeyValue(configSampleInterval.toString());
                settingsDataChanged = true;
            }

            //AutoStart beállítása
            config = findConfig(IConfigKeyNames.AUTOSTART);
            if (config != null) {
                config.setKeyValue(configAutoStart.toString());
                settingsDataChanged = true;
            }

            //KeepDays beállítása
            config = findConfig(IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
            if (config != null) {
                config.setKeyValue(configSampleDataKeepDays.toString());
                settingsDataChanged = true;
            }
        }
        try {
            //Config mentése
            configs.forEach((Config config) -> {
                configService.save(config, currentUser);
            });

            //Szerverek mentése
            servers.forEach((Server server) -> {
                //A JSF "" string lecserélése null-ra
                if (StringUtils.isEmpty(server.getUserName())) {
                    server.setUserName(null);
                    server.setPlainPassword(null);
                }

                //Beállítjuk a mérendő adatok listáját
                if (server.getJoiners() == null || server.getJoiners().isEmpty()) {
                    //Default esetben mindent mérjünk rajta!
                    serverService.addDefaultAllCollectorDataUnits(server, currentUser);
                }
                serverService.save(server, currentUser);
            });

            //Újra betöltünk mindent
            clearAll();
            loadAllFromDb();

            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Adatbázisba mentés OK");

        } catch (PersistenceException e) {
            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, String.format("Konfig mentési hiba: %s", e.getMessage()));

        }
    }

    /**
     * Az aktuálisan editált editedServer host neve alapján kitalálja az IP címet
     * és beírja az editedServer példányba
     */
    public void fillIpByHostName() {
        //Ha nincs kitöltve a host
        if (editedServer == null || StringUtils.isEmpty(editedServer.getHostName())) {
            return;
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(editedServer.getHostName());
            editedServer.setHostName(inetAddress.getHostName());
            editedServer.setIpAddress(inetAddress.getHostAddress());

        } catch (UnknownHostException ex) {
            editedServer.setIpAddress(null);
            addJsfMessage("growl", FacesMessage.SEVERITY_WARN, String.format("Ismeretlen host név: %s!", editedServer.getHostName()));
        }
    }

    /**
     * Az új szerver duplikált?
     * (hostName + IpAddress + portNumber)
     *
     * @param newServer új szerver adatok
     *
     * @return true -> duplikált
     *         false -> valóban új szerver
     */
    private boolean isDuplicateNewServer(Server newServer) {

        return servers.stream().anyMatch((server) -> ( //
                (!server.getRuntimeSeqId().equals(newServer.getRuntimeSeqId())) // nem saját maga
                && server.getHostName().equalsIgnoreCase(newServer.getHostName()) //Ha azonos a neve
                && server.getIpAddress().equalsIgnoreCase(newServer.getIpAddress()) //azonos az IP címe
                && server.getPortNumber() == newServer.getPortNumber() //azonos a portja
                ));
    }

    /**
     * Új szerver felvételének megkezdése
     */
    public void newServerBegin() {
        editedServer = new Server("localhost", 4848, "Local GlassFish", null, null, true);
        editedServer.setRuntimeSeqId(RuntimeSequenceGenerator.getNextLong());
        selectedServer = null;
        editServerDialogHeaderText = "Új szerver felvétele";
    }

    /**
     * Új vagy módosított szerver mentése a listába
     */
    public void save() {

        //Duplikált a mentendő szerver adata?
        if (isDuplicateNewServer(editedServer)) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba", "A megadott szerver adatok már léteznek!");
            RequestContext.getCurrentInstance().showMessageInDialog(facesMessage);

            //A dialógust nyitva hagyjuk
            return;
        }

        boolean isNewServer = editedServer.getId() == null;

        //Ez egy módosítás?
        if (underModifyProcess && selectedServer != null) {
            servers.remove(selectedServer); //A korábbi szerver példányt töröljük a litából
        }

        //Mentjük a változtatást
        servers.add(editedServer); //Az editált szerver példány megy be helyette
        selectedServer = editedServer;    //ez lesz kiválasztva

        addJsfMessage("growl", FacesMessage.SEVERITY_INFO, underModifyProcess ? "A szerver módosítása OK" : "Az új szerver felvétele OK");
        underModifyProcess = false;

        //Beállítjuk,  hogy változott valamilyen adat
        settingsDataChanged = true;

        //becsukjuk a dialógust
        RequestContext.getCurrentInstance().execute("PF('editServerDetailsDlg').hide();");
    }

    /**
     * Módosítást kezdünk
     */
    public void modifyBegin() {
        if (selectedServer == null) {
            return;
        }
        ModelMapper mapper = new ModelMapper();
        editedServer = new Server();
        mapper.map(selectedServer, editedServer);
        editServerDialogHeaderText = "Szerver módosítása: " + selectedServer.getUrl();

        underModifyProcess = true;
    }

    /**
     * Módosítások eldobása
     */
    public void cancel() {
        editedServer = null;
        addJsfMessage("growl", FacesMessage.SEVERITY_WARN, "A adatok eldobva");
        underModifyProcess = false;
    }

    /**
     * Kiválasztott szerver törlése a listából
     */
    public void delete() {
        servers.remove(selectedServer); //A kiválasztot szerver példányt töröljük a listából
        selectedServer = null;
        addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "A szerver törlése OK");
        underModifyProcess = false;
        settingsDataChanged = true;
    }

}
