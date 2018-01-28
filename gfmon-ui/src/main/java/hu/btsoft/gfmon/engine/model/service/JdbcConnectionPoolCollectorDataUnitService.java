/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    JdbcConnectionPoolCollectorDataUnitService.java
 *  Created: 2018.01.06. 16:08:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPoolCollectorDataUnit;
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
public class JdbcConnectionPoolCollectorDataUnitService extends ServiceBase<JdbcConnectionPoolCollectorDataUnit> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public JdbcConnectionPoolCollectorDataUnitService() {
        super(JdbcConnectionPoolCollectorDataUnit.class);
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
    public List<JdbcConnectionPoolCollectorDataUnit> findAll() {
        Query query = em.createNamedQuery("JdbcConnectionPoolCollectorDataUnit.findAll");
        List<JdbcConnectionPoolCollectorDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * Az összes mérhető Rest Path lekérdezése
     *
     * @return path lista
     */
    public List<String> getAllRestPathMasks() {
        Query query = em.createNamedQuery("JdbcConnectionPoolCollectorDataUnit.findAllRestPathMasks");
        List<String> result = query.getResultList();

        return result;
    }

    /**
     * Rest Path mask alapján keres
     *
     * @param restPathMask REST PATH maszk
     *
     * @return path lista
     */
    public List<JdbcConnectionPoolCollectorDataUnit> getAllPaths(String restPathMask) {
        Query query = em.createNamedQuery("JdbcConnectionPoolCollectorDataUnit.findAllRestPathMasks");
        query.setParameter("restPathMask", restPathMask);
        List<JdbcConnectionPoolCollectorDataUnit> result = query.getResultList();

        return result;
    }
}
