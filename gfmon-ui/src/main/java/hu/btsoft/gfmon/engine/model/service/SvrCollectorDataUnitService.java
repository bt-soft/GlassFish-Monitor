/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    SvrCollectorDataUnitService.java
 *  Created: 2018.01.06. 16:08:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.server.SvrCollectorDataUnit;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * SvrCollectorDataUnit (CDU) entitások kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class SvrCollectorDataUnitService extends ServiceBase<SvrCollectorDataUnit> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public SvrCollectorDataUnitService() {
        super(SvrCollectorDataUnit.class);
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
     * Az összes CDU entitás lekérdezése A rendezés miatt nem az ös findAll() metódusát használjuk
     *
     * @return összes CDU entitás lista
     */
    @Override
    public List<SvrCollectorDataUnit> findAll() {
        Query query = em.createNamedQuery("SvrCollectorDataUnit.findAll");
        List<SvrCollectorDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * A szerver ID alapján kikeresi az összes CDU-t
     *
     * @param serverId szerver id-je
     *
     * @return a szerver összes CDU list
     */
    public List<SvrCollectorDataUnit> findByServerId(Long serverId) {
        Query query = em.createNamedQuery("SvrCollectorDataUnit.findByServerId");
        query.setParameter("serverId", serverId);
        List<SvrCollectorDataUnit> queryResult = query.getResultList();

        return queryResult;
    }

    /**
     * A szerver ID alapján kikeresi az aktív CDU-kat
     *
     * @param serverId szerver id-je
     *
     * @return a szerveren aktív CDU lista
     */
    public List<SvrCollectorDataUnit> findByActiveAndServerId(Long serverId) {
        Query query = em.createNamedQuery("SvrCollectorDataUnit.findByActiveAndServerId");
        query.setParameter("serverId", serverId);
        List<SvrCollectorDataUnit> queryResult = query.getResultList();

        return queryResult;
    }

    /**
     * Az összes mérhető Rest Path lekérdezése
     *
     * @return path lista
     */
    public List<String> getAllPaths() {
        Query query = em.createNamedQuery("SvrCollectorDataUnit.findAllPaths");
        List<String> result = query.getResultList();

        return result;
    }
}
