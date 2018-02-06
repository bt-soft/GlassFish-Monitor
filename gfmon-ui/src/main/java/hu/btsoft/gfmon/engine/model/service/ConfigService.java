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
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;

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
     * Programmatikus Séma DropAndCreate
     */
    public void dropAndCreate() {
        log.trace("Séma: programmatikus Drop & Create!");

        ServerSession session = em.unwrap(ServerSession.class);
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceDefaultTables();
    }

    /**
     * Konfigurációs érték lekérése
     *
     * @param keyName a konfig kulcs neve
     *
     * @return Config entitás
     */
    private Config getConfig(String keyName) {
        Query query = em.createNamedQuery("Config.findByKeyNames");
        query.setParameter("keyClassName", ConfigKeyNames.CLASS_NAME);
        query.setParameter("keyName", keyName);

        try {
            Config config = (Config) query.getSingleResult();
            return config;
        } catch (NoResultException e) {
            //nincs ilyen bejegyzése
        }

        return null;

    }

    /**
     * Boolean érték leszedése
     *
     * @param keyName konfig kulcs neve
     *
     * @return boolean érték, vagy null, ha nincs ilyen kulcs
     */
    public Boolean getBoolean(String keyName) {
        Config config = getConfig(keyName);
        return config == null ? null : Boolean.parseBoolean(config.getKeyValue());
    }

    /**
     * Integer érték leszedése
     *
     * @param keyName konfig kulcs neve
     *
     * @return Integer érték, vagy null, ha nincs ilyen kulcs
     */
    public Integer getInteger(String keyName) {
        Config config = getConfig(keyName);
        return config == null ? null : Integer.parseInt(config.getKeyValue());
    }

    /**
     * String érték leszedése
     *
     * @param keyName konfig kulcs neve
     *
     * @return String érték, vagy null, ha nincs ilyen kulcs
     */
    public String getString(String keyName) {
        Config config = getConfig(keyName);
        return config == null ? null : config.getKeyValue();
    }
}
