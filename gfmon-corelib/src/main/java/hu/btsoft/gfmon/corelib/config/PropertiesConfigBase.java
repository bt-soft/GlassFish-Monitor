/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    PropertiesConfigBase.java
 *  Created: 2017.12.22. 19:01:25
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.config;

import hu.btsoft.gfmon.corelib.exception.GfMonRuntimeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 * app.config.properties kezelő osztály
 *
 * @author BT
 */
@Slf4j
public abstract class PropertiesConfigBase {

    private static final String FILE_NAME = "app.config.properties";

    @Getter
    protected Configuration config;

    /**
     * Konstruktor
     *
     * @throws GfMonRuntimeException
     */
    protected PropertiesConfigBase() {

        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder
                = new FileBasedConfigurationBuilder<>(PropertiesConfiguration.class)
                        .configure(params.properties().setFileName(FILE_NAME));
        try {

            //Betöltjük a konfigot
            config = builder.getConfiguration();
            log.trace("Konfigurációs állomány betöltése OK");

        } catch (ConfigurationException e) {
            throw new GfMonRuntimeException(String.format("Nem tölthető be a(z) '%s' konfig file!", FILE_NAME), e);
        }
    }
}
