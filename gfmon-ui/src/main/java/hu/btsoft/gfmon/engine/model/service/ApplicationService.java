/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationService.java
 *  Created: 2018.01.20. 9:47:57
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.application.Application;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * Alklamazuások entitásait kezelő osztály
 *
 * @author BT
 */
@Stateless
@Slf4j
public class ApplicationService extends ServiceBase<Application> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ApplicationService() {
        super(Application.class);
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
    public List<Application> findByServer(Long serverId) {
        Query query = em.createNamedQuery("Application.findByServerId");
        query.setParameter("serverId", serverId);
        List<Application> resultList = query.getResultList();

        return resultList;
    }

    /**
     * Az adott szerver adott alkalmazásának lekérdezése
     *
     * @param serverId     adott szerver id-je
     * @param appShortName alkalmzás rövid neve
     *
     * @return alkalmazások listája
     */
    public Application findByServerIdAndAppShortName(Long serverId, String appShortName) {
        Query query = em.createNamedQuery("Application.findByServerIdAndAppShortName");
        query.setParameter("serverId", serverId);
        query.setParameter("appShortName", appShortName);
        Application result = (Application) query.getSingleResult();

        return result;
    }
}
