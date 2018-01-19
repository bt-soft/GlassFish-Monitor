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

import hu.btsoft.gfmon.corelib.model.entity.Config;
import hu.btsoft.gfmon.corelib.model.entity.ConfigValueType;
import hu.btsoft.gfmon.corelib.model.entity.server.Server;
import hu.btsoft.gfmon.corelib.model.service.ConfigService;
import hu.btsoft.gfmon.corelib.model.service.IConfigKeyNames;
import hu.btsoft.gfmon.corelib.model.service.ServerService;
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

    private static final String DEF_USERNAME = "default-config-creator";

    @Inject
    private PropertiesConfig propertiesConfig;

    @EJB
    private ConfigService configService;

    @EJB
    private ServerService serverService;

    /**
     * Default értékek beállítása, ha szükséges
     */
    public void checkDefaults() {

        boolean dropCalled = false;

        //Induláskor kell séma legyártás?
        if (propertiesConfig.getConfig().getBoolean(PropertiesConfig.STARTUP_JPA_DROPANDCREATE_KEY, false)) {
            configService.dropAndCreate();
            dropCalled = true;
        }

        //Megnézzük, hogy létezik-e bármilyen beállítás rekord az adatbázisban
        if (!configService.checkEntityTableExist()) {
            if (!dropCalled) {
                configService.dropAndCreate();
            }
            createDefaultConfig();
        }

    }

    /**
     * Default beállítások létrehozása az adatbázisban
     */
    private void createDefaultConfig() {
        log.trace("Default beállítások létrehozása");

        {//autostart
            Config config = new Config(IConfigKeyNames.CLASS_NAME, IConfigKeyNames.AUTOSTART, ConfigValueType.B, "true");
            configService.save(config, DEF_USERNAME);
        }
        {//sampleInterval
            Config config = new Config(IConfigKeyNames.CLASS_NAME, IConfigKeyNames.SAMPLE_INTERVAL, ConfigValueType.I, "60");
            configService.save(config, DEF_USERNAME);
        }

        {//Server 1
            //
            // Az itt megadott szervereknél a gyűjtendő adatok listáját ( List<CollectorDataUnit> ) a GFMonitorController az első mérés során állítja be
            // Alapesetben minden Entitás-t összegyűjt, amit a UI felületen a szerver mért adatainak beállításánál lehet testre szabni
            //
            Server server = new Server("localhost", 4848, "Lokális GlassFish Admin", null /*user*/, null /*passwd*/, true /*enabled*/);
//            Server server = new Server("localhost", 4848, "Lokális GlassFish Admin", "admin" /*user*/, "admin" /*passwd*/, true /*enabled*/);

            serverService.save(server, DEF_USERNAME);
        }

        {//Clearing limit days
            Config config = new Config(IConfigKeyNames.CLASS_NAME, IConfigKeyNames.SAMPLE_DATA_KEEP_DAYS, ConfigValueType.I, "90");
            configService.save(config, DEF_USERNAME);
        }

    }
}
