/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Bootstrapper.java
 *  Created: 2017.12.23. 15:00:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.boot;

import hu.btsoft.gfmon.engine.config.DefaultConfigCreator;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Singleton
@Startup
@Slf4j
public class Bootstrapper {

    @EJB
    private DefaultConfigCreator defaultConfigCreator;

    /**
     * GFMon engine indítása
     */
    @PostConstruct
    protected void initApp() {
        //Default értékek beállítása
        defaultConfigCreator.checkDefaults();
    }
}
