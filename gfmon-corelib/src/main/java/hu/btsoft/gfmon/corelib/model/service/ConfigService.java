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
package hu.btsoft.gfmon.corelib.model.service;

import hu.btsoft.gfmon.corelib.model.entity.Config;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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

    public final static String KEYCLASS_NAME = "settings";
    public final static String KEY_AUTOSTART = "autoStart";
    public final static String KEY_SAMPLEINTERVAL = "sampleInterval";

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
        int originalLevel = session.getSessionLog().getLevel();
        boolean originalShouldLogExceptionStackTrace = session.getSessionLog().shouldLogExceptionStackTrace();

//        session.getSessionLog().setLevel(0); // 0 -> ALL, 8 -> OFF
//        session.getSessionLog().setShouldLogExceptionStackTrace(false);
//
        SchemaManager schemaManager = new SchemaManager(session);
        schemaManager.replaceDefaultTables();

//        session.getSessionLog().setLevel(originalLevel);
//        session.getSessionLog().setShouldLogExceptionStackTrace(originalShouldLogExceptionStackTrace);
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
        query.setParameter("keyClassName", KEYCLASS_NAME);
        query.setParameter("keyName", keyName);

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
