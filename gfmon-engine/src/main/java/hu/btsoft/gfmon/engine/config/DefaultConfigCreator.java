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

import hu.btsoft.gfmon.engine.model.entity.Config;
import hu.btsoft.gfmon.engine.model.entity.Server;
import hu.btsoft.gfmon.engine.model.service.ConfigService;
import hu.btsoft.gfmon.engine.model.service.ServerService;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
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

    private static final String DEF_USERNAME = "DefaultConfigCreator";

    @EJB
    private ConfigService configService;

    @EJB
    private ServerService serverService;

    /**
     * Default értékek beállítása, ha szükséges
     */
    public void checkDefaults() {
        //Megnézzük, hogy létezik-e bármilyen beállítás rekord az adatbázisban
        List<Config> all = configService.findAll();

        //Van már konfig
        if (all != null && !all.isEmpty()) {
            return;
        }

        createDefaults();
    }

    /**
     * Default beállítások létrehozása az adatbázisban
     */
    private void createDefaults() {
        log.trace("Default beállítások létrehozása");
        {//autostart
            Config config = new Config(ConfigService.KEYCLASS_NAME, ConfigService.KEY_AUTOSTART, ConfigValueType.B, "true");
            config.setCreatedBy(DEF_USERNAME);
            configService.save(config);
        }
        {//sampleInterval
            Config config = new Config(ConfigService.KEYCLASS_NAME, ConfigService.KEY_SAMPLEINTERVAL, ConfigValueType.I, "60");
            config.setCreatedBy(DEF_USERNAME);
            configService.save(config);
        }

        {//Server 1
            Server server = new Server("localhost", 4848, "Lokális GlassFish Admin", null /*user*/, null /*passwd*/, true /*enabled*/);
//            Server server = new Server("localhost", 4848, "Lokális GlassFish Admin", "admin" /*user*/, "admin" /*passwd*/, true /*enabled*/);
            server.setCreatedBy(DEF_USERNAME);
            serverService.save(server);
        }

    }
}
