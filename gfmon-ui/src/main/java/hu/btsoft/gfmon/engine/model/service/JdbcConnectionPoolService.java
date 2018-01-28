/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPoolService.java
 *  Created: 2018.01.28. 10:51:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPool;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * JdbcConnectionPool entitsokat kezelő osztály
 *
 * @author BT
 */
@Stateless
@Slf4j
public class JdbcConnectionPoolService extends ServiceBase<JdbcConnectionPool> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public JdbcConnectionPoolService() {
        super(JdbcConnectionPool.class);
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
     * Az adott szerver összes alkalmazásának lekérdezése
     *
     * @param serverId adott szerver id-je
     *
     * @return alkalmazások listája
     */
    public List<JdbcConnectionPool> findByServer(Long serverId) {
        Query query = em.createNamedQuery("JdbcConnectionPool.findByServerId");
        query.setParameter("serverId", serverId);
        List<JdbcConnectionPool> resultList = query.getResultList();

        return resultList;
    }

}
