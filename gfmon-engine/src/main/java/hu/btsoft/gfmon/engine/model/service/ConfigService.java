/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ConfigService.java
 *  Created: 2017.12.23. 15:08:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.Config;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * A GF monitor beállításait kezelő JPA szolgáltató osztály
 *
 * @author BT
 */
@Stateless
@Slf4j
public class ConfigService extends ServiceBase<Config> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ConfigService() {
        super(Config.class);
    }

    /**
     * EM elkérése
     *
     * @return em példány
     */
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Konfigurációs érték lekérése
     *
     * @param subKeyName alkulcs neve
     *
     * @return Config entitás
     */
    private Config getConfig(String subKeyName) {
        Query query = em.createNamedQuery("Config.findByKeyNames");
        query.setParameter("keyName", "settings");
        query.setParameter("subKeyName", subKeyName);

        Config config = (Config) query.getSingleResult();

        return config;
    }

    /**
     * Automatikusan kell indítani a méréseket a start után?
     *
     * @return true -> igen
     */
    public boolean isAutoStart() {
        Config config = getConfig("autoStart");
        return Boolean.parseBoolean(config.getKeyValue());
    }

    /**
     * Mérési időciklus lekérése [sec]
     *
     * @return időciklus másodpercekben
     */
    public int getSampleInterval() {
        Config config = getConfig("sampleInterval");
        return Integer.parseInt(config.getKeyValue());
    }
}
