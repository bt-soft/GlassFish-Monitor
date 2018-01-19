/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    PropertiesConfig.java
 *  Created: 2018.01.10. 17:44:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.config;

import hu.btsoft.gfmon.corelib.config.PropertiesConfigBase;
import javax.inject.Singleton;

/**
 * app.config.properties alapú beállítások
 *
 * @author BT
 */
@Singleton
public class PropertiesConfig extends PropertiesConfigBase {

    public final static String STARTUP_JPA_DROPANDCREATE_KEY = "startup.jpa.drop.and.create";
}
