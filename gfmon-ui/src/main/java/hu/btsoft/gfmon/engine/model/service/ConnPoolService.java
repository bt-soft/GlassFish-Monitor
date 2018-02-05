/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolService.java
 *  Created: 2018.01.28. 10:51:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPoolCollDataUnit;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPoolConnPoolCollDataUnitJoiner;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * ConnPool entitsokat kezelő osztály
 *
 * @author BT
 */
@Stateless
@Slf4j
public class ConnPoolService extends ServiceBase<ConnPool> {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ConnPoolCollectorDataUnitService connPoolCollectorDataUnitService;

    /**
     * Kontruktor
     */
    public ConnPoolService() {
        super(ConnPool.class);
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
    public List<ConnPool> findByServer(Long serverId) {
        Query query = em.createNamedQuery("ConnPool.findByServerId");
        query.setParameter("serverId", serverId);
        List<ConnPool> resultList = query.getResultList();

        return resultList;
    }

    /**
     * Application <-> Cdu összerendelés csak a memóriában
     *
     * @param connPool    connection pool
     * @param creatorUser módosító user
     */
    public void assignConnPoolToCdu(ConnPool connPool, String creatorUser) {

        List<ConnPoolCollDataUnit> allCdus = connPoolCollectorDataUnitService.findAll();

        if (allCdus != null && !allCdus.isEmpty()) {

            //Hozzáadjuk az összes DataUnit-et egy Join tábla segítségével, default esetben minden CDU aktív
            allCdus.forEach((cdu) -> {

                //Létrehozuk a kapcsolótábla entitását
                ConnPoolConnPoolCollDataUnitJoiner joiner = new ConnPoolConnPoolCollDataUnitJoiner(connPool, cdu, creatorUser, Boolean.TRUE);

                //Behuzalozzuk a szerverbe és le is mentjük
                connPool.getJoiners().add(joiner);

                //behuzalozzuk a CDU-ba és le is mentjük
                cdu.getJoiners().add(joiner);
            });

//            log.trace("A(z) '{}1 szerver '{}' JDBC ConnectionPool CDU összerendelése a memóriában OK", connPool.getServer().getSimpleUrl(), connPool.getPoolName());
        }
    }

    /**
     * Application <-> Cdu összerendelés és adatbázis mentés
     *
     * @param connPool    connection pool
     * @param creatorUser módosító user
     */
    public void assignConnPoolToCduIntoDb(ConnPool connPool, String creatorUser) {

        List<ConnPoolCollDataUnit> allCdus = connPoolCollectorDataUnitService.findAll();

        if (allCdus != null && !allCdus.isEmpty()) {

            //Ha zsír új a ConnectionPool (pl.: GUI felületen újonnan vették fel), akkor most jól lementjük,
            // hogy a kapcolótáblát fel tudjuk építeni
            if (connPool.getId() == null) {
                if (connPool.getJoiners() == null) {
                    connPool.setJoiners(new LinkedList<>());
                }
                super.save(connPool, creatorUser);
            }

            //Hozzáadjuk az összes DataUnit-et egy Join tábla segítségével, default esetben minden CDU aktív
            allCdus.forEach((cdu) -> {

                //Létrehozuk a kapcsolótábla entitását
                ConnPoolConnPoolCollDataUnitJoiner joiner = new ConnPoolConnPoolCollDataUnitJoiner(connPool, cdu, creatorUser, Boolean.TRUE);
                em.persist(joiner);

                //Behuzalozzuk a szerverbe és le is mentjük
                connPool.getJoiners().add(joiner);
                em.merge(connPool);

                //behuzalozzuk a CDU-ba és le is mentjük
                cdu.getJoiners().add(joiner);
                em.merge(cdu);
            });

//            log.trace("A(z) '{}1 szerver '{}' JDBC ConnectionPool CDU összerendelése és adatbázisba mentése OK", connPool.getServer().getSimpleUrl(), connPool.getPoolName());
        }
    }

}
