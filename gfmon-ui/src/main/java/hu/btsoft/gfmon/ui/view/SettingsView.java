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
import hu.btsoft.gfmon.corelib.model.service.ConfigService;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;

/**
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

    @PostConstruct
    protected void init() {
        configs = configService.findAll();
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

        try {
            configs.stream()
                    .forEach((Config config) -> {
                        configService.save(config, GFMonJSFLib.getCurrentUser());
                    });
            updateJsfMessage("growl", FacesMessage.SEVERITY_INFO, "Konfig mentése OK");
        } catch (PersistenceException e) {
            updateJsfMessage("growl", FacesMessage.SEVERITY_ERROR, String.format("Konfig mentési hiba: %s", e.getMessage()));
        }
    }
}
