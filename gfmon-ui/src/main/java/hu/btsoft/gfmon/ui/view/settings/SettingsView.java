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

import hu.btsoft.gfmon.corelib.exception.GfMonException;
import hu.btsoft.gfmon.engine.model.RuntimeSequenceGenerator;
import hu.btsoft.gfmon.engine.model.entity.Config;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPool;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.ServerSvrCollDataUnitJoiner;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.monitor.ApplicationsMonitor;
import hu.btsoft.gfmon.engine.monitor.GlassFishMonitorController;
import hu.btsoft.gfmon.engine.monitor.JdbcConnectionPoolMonitor;
import hu.btsoft.gfmon.engine.monitor.management.ServerUptime;
import hu.btsoft.gfmon.engine.monitor.management.ServerVersion;
import hu.btsoft.gfmon.engine.security.SessionTokenAcquirer;
import hu.btsoft.gfmon.ui.view.ViewBase;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.TabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

/**
 * Beállítások LAP JSF Managed Bean
 *
 * @author BT
 */
@Named(value = "settingsView")
@ViewScoped
@Slf4j
public class SettingsView extends ViewBase {

    @Inject
    private ServerVersion serverVersion;

    @Inject
    private ServerUptime serverUptime;

    @Inject
    private SessionTokenAcquirer sessionTokenAcquirer;

    @EJB
    private GlassFishMonitorController glassFishMonitorController;

    // --- Config -----------------------------------
    @EJB
    private ConfigService configService;

    @Getter
    private List<Config> configs;

    // --- Server -----------------------------------
    @EJB
    private ServerService serverService;

    @Getter
    private List<Server> servers;

    //Az adatbázisból törlendő szerverek listája
    private final List<Server> deleteServersFromDb = new LinkedList<>();

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

    // --- Applications
    @EJB
    private ApplicationsMonitor applicationsMonitor;

    //--- JDBC Connection pool
    @EJB
    private JdbcConnectionPoolMonitor jdbcConnectionPoolMonitor;

    @Getter
    @Setter
    private boolean currentMonitorControllerTimerStatus;

    /**
     * JSF ManagedBean init
     */
    @PostConstruct
    protected void loadAllFromDb() {

        //Kontroller státusz
        currentMonitorControllerTimerStatus = glassFishMonitorController.isRunningTimer();

        //Konfig rekordok betöltése
        configs = configService.findAll();

        //Szerverek betöltése
        refreshServers();
    }

    /**
     * minden változó törlése
     */
    private void clearAll() {

        configs = null;
        servers = null;
        editedServer = null;
        editServerDialogHeaderText = null;
        settingsDataChanged = false;

        deleteServersFromDb.clear();
    }

    /**
     * Szerver adatok lekérése az adatbázisból
     */
    public void refreshServers() {
        servers = serverService.findAllAndSetRuntimeSeqId();
        this.sortAllServerJoinersList();
    }

    /**
     * Minden frissítése
     */
    private void refreshAll() {
        loadAllFromDb();
        refreshApplications();
        refreshConnectionPools();
    }

    /**
     * Monitor timer állítgatása
     */
    public void toggleMonitorControllerTimerStatus() {
        if (currentMonitorControllerTimerStatus) {
            glassFishMonitorController.startTimer();
            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Monitor Timer elindítása OK");
        } else {
            glassFishMonitorController.stopTimer();
            addJsfMessage("growl", FacesMessage.SEVERITY_WARN, "A Monitor Timer leállítva!");
        }

        currentMonitorControllerTimerStatus = glassFishMonitorController.isRunningTimer();
    }

//<editor-fold defaultstate="collapsed" desc="Konfigurációs értékek getter/setter">
    /**
     * Konfigurációs entitás keresése
     *
     * @param keyName konfig kulcs neve
     *
     * @return Config entitás vagy null, ha nincs ilyen
     */
    private String getConfig(String keyName) {
        Config config = configs.stream()
                .filter(x -> keyName.equals(x.getKeyName()))
                .findFirst()
                .get();

        return config != null ? config.getKeyValue() : null;
    }

    /**
     * Konfig beállítása
     *
     * @param keyName konfig kulcs neve
     * @param value   értéke
     */
    private void setConfig(String keyName, String value) {
        Config config = configs.stream()
                .filter(x -> keyName.equals(x.getKeyName()))
                .findFirst()
                .get();
        if (config != null) {
            config.setKeyValue(value);
            settingsDataChanged = true;
        } else {
            log.warn("Nincs ilyen konfigurációs kulcs: {}", keyName);
        }
    }

    public String getAutoStart() {
        return this.getConfig(ConfigKeyNames.AUTOSTART);
    }

    public void setAutoStart(String value) {
        this.setConfig(ConfigKeyNames.AUTOSTART, value);
    }

    public String getSampleInterval() {
        return this.getConfig(ConfigKeyNames.SAMPLE_INTERVAL);
    }

    public void setSampleInterval(String value) {
        this.setConfig(ConfigKeyNames.SAMPLE_INTERVAL, value);
    }

    public String getKeepDays() {
        return this.getConfig(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS);
    }

    public void setKeepDays(String value) {
        this.setConfig(ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS, value);
    }
//</editor-fold>

    /**
     * Beállítások mentése
     */
    public void saveSettings() {

        try {
            //Config mentése
            configs.forEach((Config config) -> {
                configService.save(config, currentUser);
            });

            //Szerver tényleges törlése, ha a deleteServersFromDb-ben van törléendő
            if (deleteServersFromDb != null && !deleteServersFromDb.isEmpty()) {
                deleteServersFromDb.forEach((Server server) -> {
                    serverService.remove(server);
                });
            }

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
                    serverService.assignServerToCdu(server, currentUser);
                }

                //CDU-k mentése
///                serverService.updateJoiners(server, currentUser);
                //Szerver mentése, az alkalmazások update automatikusan megtörténik
                serverService.save(server, currentUser);
            });

            //Újra betöltünk mindent
            clearAll();
            refreshAll();

            addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Adatbázisba mentés OK");

        } catch (PersistenceException e) {
            addJsfMessage("growl", FacesMessage.SEVERITY_ERROR, String.format("Konfig mentési hiba: %s", e.getMessage()));

        }
    }

    /**
     * Az aktuálisan editált editedServer host neve alapján kitalálja az IP címet és beírja az editedServer példányba
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
     * Az új szerver duplikált? (hostName + IpAddress + portNumber)
     *
     * @param newServer új szerver adatok
     *
     * @return true -> duplikált false -> valóban új szerver
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
        editedServer = new Server("localhost", 4848, "Local GlassFish", null, null, ".*", true);
        editedServer.setRuntimeSeqId(RuntimeSequenceGenerator.getNextLong());
        selectedServer = null;
        editServerDialogHeaderText = "Új szerver felvétele";
    }

    /**
     * Új vagy módosított szerver mentése a listába
     */
    public void saveEditedServer() {

        //Duplikált a mentendő szerver adata?
        if (isDuplicateNewServer(editedServer)) {
            FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba", "A megadott szerver adatok már léteznek!");
            RequestContext.getCurrentInstance().showMessageInDialog(facesMessage);

            //A dialógust nyitva hagyjuk
            return;
        }

        //Ez egy módosítás?
        if (underModifyProcess && selectedServer != null) {
            servers.remove(selectedServer); //A korábbi szerver példányt töröljük a listából
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
    public void modifySelectedServerBegin() {
        if (selectedServer == null) {
            return;
        }
        editedServer = selectedServer;
        editServerDialogHeaderText = "Szerver módosítása";
        underModifyProcess = true;
    }

    /**
     * Módosítások eldobása
     */
    public void cancelEditedServer() {
        editedServer = null;
        addJsfMessage("growl", FacesMessage.SEVERITY_WARN, "A adatok eldobva");
        underModifyProcess = false;
    }

    /**
     * Kiválasztott szerver törlése a listából
     */
    public void deleteSelectedServer() {
        servers.remove(selectedServer); //A kiválasztot szerver példányt töröljük a listából

        //Ha a szerver szerepel már az adatbázisban, akkor egy külön listában vezetjük
        if (selectedServer.getId() != null) {
            deleteServersFromDb.add(selectedServer);
        }

        selectedServer = null;
        addJsfMessage("growl", FacesMessage.SEVERITY_INFO, "A szerver törlése OK");
        underModifyProcess = false;
        settingsDataChanged = true;

    }

    /**
     * A kiválasztott szerver verzió információinak elkérése
     *
     * @return verzió info
     */
    public String getSelectedServerVersion() {

        if (selectedServer == null) {
            return null;
        }
        if (StringUtils.isEmpty(selectedServer.getSessionToken())) {
            return "Még nincs adat";
        }
        Map<String, String> serverVersionInfo = serverVersion.getServerVersionInfo(selectedServer.getSimpleUrl(), selectedServer.getUserName(), selectedServer.getSessionToken());
        if (serverVersionInfo != null) {
            String versionStr = serverVersionInfo.get("full-version");
            if (!StringUtils.isEmpty(versionStr)) {
                return versionStr;
            }
        }

        return "Nem kérdezhető le";
    }

    /**
     * A kiválasztott szerver uptime információinak elkérése
     *
     * @return uptime info
     */
    public String getSelectedServerUptime() {

        if (selectedServer == null) {
            return null;
        }
        if (StringUtils.isEmpty(selectedServer.getSessionToken())) {
            return "Még nincs adat";
        }
        String uptimeStr = serverUptime.getServerUptime(selectedServer.getSimpleUrl(), selectedServer.getUserName(), selectedServer.getSessionToken());

        return StringUtils.isEmpty(uptimeStr) ? "Nem kérdezhető le" : uptimeStr;
    }

    /**
     * A kiválasztott szerver összes alkalmazás adatának aktív flagjának állítgatása
     *
     * @param newActiveFlag 'true'/'false'
     */
    public void toggleAllAppActiveFlag(boolean newActiveFlag) {

        selectedServer.getApplications()
                .forEach((app) -> {
                    app.setActive(newActiveFlag);
                });
    }

    /**
     * Alkalmazások frissítése
     */
    public void refreshApplications() {

        //nincs szerver
        if (selectedServer == null) {
            return;
        }

        //Ha még zsír új a szerver, akkor nem turkálunk az adatbázisban
        if (StringUtils.isEmpty(selectedServer.getSessionToken())) {

            //Ha még nincs SessionToken, akkor csinálunk egyet
            try {
                String sessionToken = sessionTokenAcquirer.getSessionToken(selectedServer.getUrl(), selectedServer.getUserName(), selectedServer.getPlainPassword());
                selectedServer.setSessionToken(sessionToken);
            } catch (GfMonException e) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba", e.getMessage());
                RequestContext.getCurrentInstance().showMessageInDialog(facesMessage);
                return;
            }
        }

        //Ha a szerver még nincs az adatbázisban, akkor on-the-fly kérdezzük le az alkalmazásait
        if (selectedServer.getId() == null) {
            List<Application> applicationsList = applicationsMonitor.getApplicationsList(selectedServer);
            //Beállítjuk, hogy ki vette fel őket
            if (applicationsList != null) {
                applicationsList.stream()
                        .filter((app) -> (app.getCreatedBy() == null))
                        .forEachOrdered((app) -> {
                            //beállítjuk, hogy melyik szerveren fut az alkalmazás
                            app.setCreatedBy(currentUser);
                        });
            }

            selectedServer.setApplications(applicationsList);
            return;
        }

        //
        // A kiválasztott szerver az adatbázisban van, így abban kel az alkalmazások karbantartását elvégezni
        //
        //Feltérképezzük a szerver alkalmazásait -> ez beírja az adatbázisba azt, amit épp lát
        applicationsMonitor.maintenanceServerAplicationInDataBase(selectedServer);

        //Újra kikeressük az adatbázisból a szervert, hogy az alkalmazások lista frissűljön
        Server refreshedServer = (Server) serverService.find(selectedServer.getId());

        //Ugyan arra a runtimeSequId-re állítjuk
        refreshedServer.setRuntimeSeqId(selectedServer.getRuntimeSeqId());

        //Kicseréljük a listában az újonnan felolvasott szervert
        for (int ndx = 0; ndx < servers.size(); ndx++) {
            if (Objects.equals(servers.get(ndx).getId(), selectedServer.getId())) {
                servers.set(ndx, refreshedServer);

                //Átállítjuk a selectedServer referenciát is, hogy az összes gyerek tábla is frissűlhessen
                selectedServer = refreshedServer;
                break;
            }
        }
    }

    /**
     * JDBC ConncetionPool frissítése
     */
    public void refreshConnectionPools() {

        if (selectedServer == null) {
            return;
        }

        //Ha még zsír új a szerver, akkor nem turkálunk az adatbázisban
        if (StringUtils.isEmpty(selectedServer.getSessionToken())) {

            //Ha még nincs SessionToken, akkor csinálunk egyet
            try {
                String sessionToken = sessionTokenAcquirer.getSessionToken(selectedServer.getUrl(), selectedServer.getUserName(), selectedServer.getPlainPassword());
                selectedServer.setSessionToken(sessionToken);
            } catch (GfMonException e) {
                FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hiba", e.getMessage());
                RequestContext.getCurrentInstance().showMessageInDialog(facesMessage);
                return;
            }
        }
        //Ha a szerver még nincs az adatbázisban, akkor on-the-fly kérdezzük le az alkalmazásait
        if (selectedServer.getId() == null) {
            List<JdbcConnectionPool> jdbcConnectionPools = jdbcConnectionPoolMonitor.getJdbcConnectionPoolsList(selectedServer);
            //Beállítjuk, hogy ki vette fel őket
            if (jdbcConnectionPools != null) {
                jdbcConnectionPools.stream()
                        .filter((app) -> (app.getCreatedBy() == null))
                        .forEachOrdered((app) -> {
                            app.setCreatedBy(currentUser); //beállítjuk, hogy melyik szerveren fut az alkalmazás
                        });
            }

            selectedServer.setJdbcConnectionPools(jdbcConnectionPools);
            return;
        }

        //
        // A kiválasztott szerver az adatbázisban van, így abban kel a JDBC ConnectionPool karbantartását elvégezni
        //
        //Feltérképezzük a szerver JDBC COnnectionPool-jait -> ez beírja az adatbázisba azt, amit épp lát
        jdbcConnectionPoolMonitor.maintenanceServerJdbcResourcesInDataBase(selectedServer);

        //Újra kikeressük az adatbázisból a szervert, hogy a JDBC lista frissűljön
        Server refreshedServer = (Server) serverService.find(selectedServer.getId());

        //Ugyan arra a runtimeSequId-re állítjuk
        refreshedServer.setRuntimeSeqId(selectedServer.getRuntimeSeqId());

        //Kicseréljük a listában az újonnan felolvasott szervert
        int ndx = servers.indexOf(selectedServer);
        servers.set(ndx, refreshedServer);

        //Átállítjuk a selectedServer referenciát is, hogy az alkalmazások tábla is frissűlhessen
        selectedServer = refreshedServer;
    }

    /**
     * A kiválasztott szerver összes JDBC ConnectionPool adatának aktív flagjának állítgatása
     *
     * @param newActiveFlag 'true'/'false'
     */
    public void toggleAllConPoolActiveFlag(boolean newActiveFlag) {

        selectedServer.getJdbcConnectionPools()
                .forEach((app) -> {
                    app.setActive(newActiveFlag);
                });
    }

    @Getter
    private int childCategoriesTabViewIndex = 0;

    public void onChildCategoriesTabView(final TabChangeEvent event) {
        TabView tv = (TabView) event.getComponent();
        childCategoriesTabViewIndex = tv.getActiveIndex();
    }

    //--------------------------------------------------------
    /**
     * Az összes szerver joiner tábláját lerendezzük a szerverDCU-k path és adatnevei szerint
     * <p>
     * Mivel kapcsolótábla van a Server és a DCU között, így a szerver DCU listájánál nincs a kezünkben a mező
     * amivel kiadhatnánk a JPA @OrderBy("jpaProperty DESC") annotációt.
     * Emiatt kézzel rendezünk
     */
    private void sortAllServerJoinersList() {

        servers.forEach((server) -> {
            server.getJoiners().sort((o1, o2) -> {
                ServerSvrCollDataUnitJoiner j1 = (ServerSvrCollDataUnitJoiner) o1;
                ServerSvrCollDataUnitJoiner j2 = (ServerSvrCollDataUnitJoiner) o2;

                //Először a Path szerint hasonlítjuk össze
                int result = j1.getSvrCollectorDataUnit().getRestPath().compareTo(j2.getSvrCollectorDataUnit().getRestPath());

                //Ha a Path azonos, akkor az adatnév szerint hasonlítunk
                if (result == 0) {
                    result = j1.getSvrCollectorDataUnit().getDataName().compareTo(j2.getSvrCollectorDataUnit().getDataName());
                }

                return result;
            });
        });
    }

    /**
     * A kiválasztott szerver összes cdu adatának aktív flagjának állítgatása
     *
     * @param newActiveFlag 'true'/'false'
     */
    public void toggleAllDcuActiveFlag(boolean newActiveFlag) {

        selectedServer.getJoiners()
                .forEach((joiner) -> {
                    joiner.setActive(newActiveFlag);
                    joiner.setAdditionalMessage(null);  //töröljük a kieginfót
                });
    }

    /**
     * Van figyelmeztető üzenet?
     *
     * @return true -> van
     */
    public boolean warningExisInDcus() {
        if (selectedServer != null) {
            for (ServerSvrCollDataUnitJoiner joiner : selectedServer.getJoiners()) {
                if (joiner.getAdditionalMessage() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Egy joiner-t állítgattak -> töröljük a kieginfót!
     *
     * @param svrCollectorDataUnitId a joiner id-je
     */
    public void joinerActiveChanged(Long svrCollectorDataUnitId) {

        for (ServerSvrCollDataUnitJoiner joiner : selectedServer.getJoiners()) {
            if (joiner.getSvrCollectorDataUnit().getId().equals(svrCollectorDataUnitId)) {
                joiner.setModifiedBy(currentUser);
                joiner.setAdditionalMessage(null);
            }
        }
    }

}
