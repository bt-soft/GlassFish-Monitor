/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    ApplicationCollectorDataUnitService.java
 *  Created: 2018.01.06. 16:08:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.application.ApplicationCollectorDataUnit;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * ApplicationCollectorDataUnit (CDU) entitások kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class ApplicationCollectorDataUnitService extends ServiceBase<ApplicationCollectorDataUnit> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ApplicationCollectorDataUnitService() {
        super(ApplicationCollectorDataUnit.class);
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
    public List<ApplicationCollectorDataUnit> findAll() {
        Query query = em.createNamedQuery("ApplicationCollectorDataUnit.findAll");
        List<ApplicationCollectorDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * Az összes mérhető Rest Path lekérdezése
     *
     * @return path lista
     */
    public List<String> getAllRestPathMasks() {
        Query query = em.createNamedQuery("ApplicationCollectorDataUnit.findAllRestPathMasks");
        List<String> result = query.getResultList();

        return result;
    }

    /**
     * Rest Path mask alapján keres
     *
     * @param restPathMask REST path maszk
     *
     * @return path lista
     */
    public List<ApplicationCollectorDataUnit> getAllPaths(String restPathMask) {
        Query query = em.createNamedQuery("ApplicationCollectorDataUnit.findAllRestPathMasks");
        query.setParameter("restPathMask", restPathMask);
        List<ApplicationCollectorDataUnit> result = query.getResultList();

        return result;
    }
}
