/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerService.java
 *  Created: 2017.12.23. 15:48:48
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.Server;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Monitorozandó szerverek adatait kezelő JPA szolgáltató osztály
 *
 * @author BT
 */
@Stateless
@Slf4j
public class ServerService extends ServiceBase<Server> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ServerService() {
        super(Server.class);
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
     * A futási idejű Entitás értékekek törlése
     * (pl.: sessionToken, readyForMonitoring, stb..)
     *
     * @param modifier módosító user
     */
    public void clearRuntimeValues(String modifier) {

        super.findAll().stream().map((server) -> {
            server.setSessionToken(null);
            return server;
        }).map((server) -> {
            server.setMonitoringServiceReady(null);
            return server;
        }).map((server) -> {
            server.setModifiedBy(modifier);
            return server;
        }).forEachOrdered((server) -> {
            super.save(server);
        });

    }

    /**
     * Kieginfó módosítása
     *
     * @param entity            Server entitás
     * @param modifier          módosító user
     * @param additionalMessage az adatbázisba írandó kieginfo
     */
    public void updateAdditionalMessage(Server entity, String modifier, String additionalMessage) {

        //Módosító user
        entity.setModifiedBy(modifier);

        //Kieginfo
        entity.setAdditionalInformation(additionalMessage);

        //Az esetleges optimisticLocking elkerülése végett a Version-t átmásoljuk az adatbzisból imént felolvasott értékre
        Server lastVersion = super.find(entity.getId());
        entity.setOptLockVersion(lastVersion.getOptLockVersion());

        //Le is mentjük az adatbázisba az állapotot
        super.save(entity);
    }

    /**
     * Kieginfo törlése
     *
     * @param entity   Server entitás
     * @param modifier módosító user
     */
    public void clearAdditionalMessage(Server entity, String modifier) {

        //Rákeresünk, hogy ne legyen optimisticLocking
        Server lastVersion = super.find(entity.getId());

        //Ha nme üres a kieginfo-ja, akkor most töröljük!
        if (!StringUtils.isEmpty(lastVersion.getAdditionalInformation())) {
            lastVersion.setAdditionalInformation(null);
            super.save(lastVersion);
        }
    }
}
