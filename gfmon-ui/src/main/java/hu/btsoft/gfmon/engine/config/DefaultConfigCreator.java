/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    DefaultConfigCreator.java
 *  Created: 2017.12.23. 14:42:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.config;

import hu.btsoft.gfmon.corelib.string.StrUtils;
import hu.btsoft.gfmon.engine.model.entity.Config;
import hu.btsoft.gfmon.engine.model.entity.application.AppCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPoolCollDataUnit;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.service.ApplicationCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.ConfigKeyNames;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ConnPoolCollectorDataUnitService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import hu.btsoft.gfmon.engine.model.service.SvrCollectorDataUnitService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Default beállításokat létrehozó EJB bean
 *
 * @author BT
 */
@Stateless
@Slf4j
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) //A BEAN-be záródik a tranzakció
public class DefaultConfigCreator {

    private static final String DB_MODIFICATOR_USER = "default-config-creator";

    @Inject
    private PropertiesConfig propertiesConfig;

    @EJB
    private ConfigService configService;

    @EJB
    private ServerService serverService;

    @EJB
    private ApplicationCollectorDataUnitService applicationCollectorDataUnitService;

    @EJB
    private ConnPoolCollectorDataUnitService connPoolCollectorDataUnitService;

    @EJB
    private SvrCollectorDataUnitService svrCollectorDataUnitService;

    /**
     * Default értékek beállítása, ha szükséges
     */
    public void checkDefaults() {

        //Induláskor kell séma legyártás?
//        if (propertiesConfig.getConfig().getBoolean(PropertiesConfig.STARTUP_JPA_DROPANDCREATE_KEY, false)) {
//            configService.dropAndCreate();
//        }
        // CDU-k legyártása, ha szükséges
        if ("static".equalsIgnoreCase(propertiesConfig.getConfig().getString(PropertiesConfig.STARTUP_JPA_CDU_BUILD_MODE))) {
            buildStaticCdus();
        }

        createDefaultConfig();

    }

    /**
     * Default beállítások létrehozása az adatbázisban
     */
    private void createDefaultConfig() {
        log.trace("Default beállítások létrehozása");

        {//autostart
            Config config = new Config(ConfigKeyNames.CLASS_NAME, ConfigKeyNames.AUTOSTART, ConfigValueType.B, "false");
            configService.save(config, DB_MODIFICATOR_USER);
        }
        {//sampleInterval
            Config config = new Config(ConfigKeyNames.CLASS_NAME, ConfigKeyNames.SAMPLE_INTERVAL, ConfigValueType.I, "60");
            configService.save(config, DB_MODIFICATOR_USER);
        }

        {//Server 1
            //
            // Az itt megadott szervereknél a gyűjtendő adatok listáját ( List<CollectorDataUnit> ) a GFMonitorController az első mérés során állítja be
            // Alapesetben minden Entitás-t összegyűjt, amit a UI felületen a szerver mért adatainak beállításánál lehet testre szabni
            //
            Server server = new Server("localhost", 4848, "Lokális GlassFish Admin", null /* user */, null /* passwd */, ".*" /* regExpFilter */, true /* enabled */);
            serverService.save(server, DB_MODIFICATOR_USER); //lementjük a szervert és a CDU összerendelést is
            if ("static".equalsIgnoreCase(propertiesConfig.getConfig().getString(PropertiesConfig.STARTUP_JPA_CDU_BUILD_MODE))) {
                serverService.assignServerToCduIntoDb(server, DB_MODIFICATOR_USER); //Összerendeljük a szerver <-> CDU-kat az adatbázisban is
            }

        }

        {//Clearing limit days
            Config config = new Config(ConfigKeyNames.CLASS_NAME, ConfigKeyNames.SAMPLE_DATA_KEEP_DAYS, ConfigValueType.I, propertiesConfig.getConfig().getString(PropertiesConfig.DEFAULT_DATA_RETENTION_IN_DAYS, "90"));
            configService.save(config, DB_MODIFICATOR_USER);
        }

    }

    /**
     * CDU-k legyártása korábban létrehozott tábla export állományokból
     * Az export file rekordszerkezete:
     * A delimiter: ; (pontosvessző), minden adat idézőjelek között van
     * <p>
     * "restpath";"entityName";"dataName";"unit";"description"
     * <p>
     */
    private void buildStaticCdus() {

        // App CDU
        if (!applicationCollectorDataUnitService.checkEntityTableExist()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/static-db-data/app_cdu.export")))) {

                log.trace("APP_CDU tábla feltöltése");
                String line = br.readLine();
                while (line != null) {
                    String[] data = line.split(";");
                    AppCollectorDataUnit cdu = new AppCollectorDataUnit(StrUtils.deQuote(data[0]),
                            StrUtils.deQuote(data[1]),
                            StrUtils.deQuote(data[2]),
                            StrUtils.deQuote(data[3]),
                            StrUtils.deQuote(data[4]));
                    applicationCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
                    line = br.readLine();
                }

            } catch (IOException e) {
                log.error("app_cdu.export fájl olvasási hiba!", e);
            }
        }

        // ConnPool CDU
        if (!connPoolCollectorDataUnitService.checkEntityTableExist()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/static-db-data/connpool_cdu.export")))) {

                log.trace("CONNPOOL_CDU tábla feltöltése");
                String line = br.readLine();
                while (line != null) {
                    String[] data = line.split(";");
                    ConnPoolCollDataUnit cdu = new ConnPoolCollDataUnit(StrUtils.deQuote(data[0]),
                            StrUtils.deQuote(data[1]),
                            StrUtils.deQuote(data[2]),
                            StrUtils.deQuote(data[3]),
                            StrUtils.deQuote(data[4]));
                    connPoolCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
                    line = br.readLine();
                }

            } catch (IOException e) {
                log.error("connpool_cdu.export fájl olvasási hiba!", e);
            }
        }

        // Svr CDU
        if (!svrCollectorDataUnitService.checkEntityTableExist()) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("/static-db-data/srv_cdu.export")))) {

                log.trace("SVR_CDU tábla feltöltése");
                String line = br.readLine();
                while (line != null) {
                    String[] data = line.split(";");
                    SvrCollectorDataUnit cdu = new SvrCollectorDataUnit(StrUtils.deQuote(data[0]),
                            StrUtils.deQuote(data[1]),
                            StrUtils.deQuote(data[2]),
                            StrUtils.deQuote(data[3]),
                            StrUtils.deQuote(data[4]));
                    svrCollectorDataUnitService.save(cdu, DB_MODIFICATOR_USER);
                    line = br.readLine();
                }

            } catch (IOException e) {
                log.error("srv_cdu.export.export fájl olvasási hiba!", e);
            }
        }

    }
}
