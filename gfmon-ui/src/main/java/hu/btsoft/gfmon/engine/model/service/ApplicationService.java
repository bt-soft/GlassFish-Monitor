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

import hu.btsoft.gfmon.engine.model.entity.application.AppCollectorDataUnit;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.ApplicationAppCollDataUnitJoiner;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
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

    @EJB
    private ApplicationCollectorDataUnitService applicationCollectorDataUnitService;

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

    /**
     * Application <-> CDU összerendelés a memóriában
     *
     * @param app         alkalmazás
     * @param creatorUser létrehozó user
     */
    public void assignApplicationToCdu(Application app, String creatorUser) {
        List<AppCollectorDataUnit> allCdus = applicationCollectorDataUnitService.findAll();

        if (allCdus != null && !allCdus.isEmpty()) {

            //Hozzáadjuk az összes DataUnit-et egy Join tábla segítségével, default esetben minden CDU aktív
            allCdus.forEach((cdu) -> {

                //Létrehozuk a kapcsolótábla entitását
                ApplicationAppCollDataUnitJoiner joiner = new ApplicationAppCollDataUnitJoiner(app, cdu, creatorUser, Boolean.TRUE);

                //Behuzalozzuk a szerverbe és le is mentjük
                app.getJoiners().add(joiner);

                //behuzalozzuk a CDU-ba és le is mentjük
                cdu.getJoiners().add(joiner);
            });

//            log.trace("A(z) '{}1 szerver '{}' alkalmazás CDU összerendelése a memóriában OK", app.getServer().getSimpleUrl(), app.getAppRealName());
        }
    }

    /**
     * Application <-> Cdu összerendelés és adatbázisba mentés
     *
     * @param app         alkalmazás
     * @param creatorUser létrehozó user
     */
    public void assignApplicationToCduIntoDb(Application app, String creatorUser) {

        List<AppCollectorDataUnit> allCdus = applicationCollectorDataUnitService.findAll();

        if (allCdus != null && !allCdus.isEmpty()) {

            //Ha zsír új az alkalmazás (pl.: GUI felületen újonnan vették fel), akkor most jól lementjük,
            // hogy a kapcolótáblát fel tudjuk építeni
            if (app.getId() == null) {
                if (app.getJoiners() == null) {
                    app.setJoiners(new LinkedList<>());
                }
                super.save(app, creatorUser);
            }

            //Hozzáadjuk az összes DataUnit-et egy Join tábla segítségével, default esetben minden CDU aktív
            allCdus.forEach((cdu) -> {

                //Létrehozuk a kapcsolótábla entitását
                ApplicationAppCollDataUnitJoiner joiner = new ApplicationAppCollDataUnitJoiner(app, cdu, creatorUser, Boolean.TRUE);
                em.persist(joiner);

                //Behuzalozzuk a szerverbe és le is mentjük
                app.getJoiners().add(joiner);
                em.merge(app);

                //behuzalozzuk a CDU-ba és le is mentjük
                cdu.getJoiners().add(joiner);
                em.merge(cdu);
            });

//            log.trace("A(z) '{}1 szerver '{}' alkalmazás CDU összerendelése és adatbázisba mentése OK", app.getServer().getSimpleUrl(), app.getAppRealName());
        }
    }
}
