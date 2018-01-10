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
package hu.btsoft.gfmon.corelib.model.service;

import hu.btsoft.gfmon.corelib.model.RuntimeSequenceGenerator;
import hu.btsoft.gfmon.corelib.model.entity.server.CollectorDataUnit;
import hu.btsoft.gfmon.corelib.model.entity.server.Server;
import hu.btsoft.gfmon.corelib.model.entity.server.ServerCollDataUnitJoiner;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

    @EJB
    private CollectorDataUnitService collectorDataUnitService;

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
     * Az összes ismert DataCollectorUnit hozzáadása a aszerverhez
     *
     * @param server    szerver példány
     * @param createdBy létrehozó user
     */
    public void addDefaultAllCollectorDataUnits(Server server, String createdBy) {

        List<CollectorDataUnit> allCdus = collectorDataUnitService.findAll();

        if (allCdus != null && !allCdus.isEmpty()) {

            //Ha zsír új a szerver (pl.: GUI felületen újonnan vették fel), akkor most jól lementjük,
            // hogy a kapcolótáblát fel tudjuk építeni
            if (server.getId() == null) {
                if (server.getJoiners() == null) {
                    server.setJoiners(new LinkedList<>());
                }
                super.save(server, createdBy);
            }

            //Hozzáadjuk az összes DataUnit-et egy Join tábla segítségével, default esetben minden CDU aktív
            allCdus.forEach((cdu) -> {

                //Létrehozuk a kapcsolótábla entitását
                ServerCollDataUnitJoiner joiner = new ServerCollDataUnitJoiner(server, cdu, createdBy, Boolean.TRUE);
                em.persist(joiner);

                //Behuzalozzuk aszerverbe és le is mentjük
                server.getJoiners().add(joiner);
                em.merge(server);

                //behuzalozzuk a CDU-ba és le is mentjük
                cdu.getJoiners().add(joiner);
                em.merge(cdu);
            });
        }

        int a = 1;
    }

    /**
     * Összes szerver lekérdezése
     * A rendezés miatt nem az ös findAll() metódusát használjuk
     *
     * @return az összes szerver listája
     */
    @Override
    public List<Server> findAll() {
        Query query = em.createNamedQuery("Server.findAll");
        List<Server> resultList = query.getResultList();

        return resultList;
    }

    /**
     * Összes aktív szerver lekérdezése
     *
     * @return aktív szerverek listája
     */
    public List<Server> findAllActiveServer() {
        Query query = em.createNamedQuery("Server.findAllActive");
        List<Server> resultList = query.getResultList();

        return resultList;
    }

    /**
     * A futási idejű Entitás értékekek törlése
     * - Lekéri az összes entitást az adatbázisból
     * - Törli a runtime értékeket
     * - majd visszamenti az adatbázisba
     *
     * (pl.: sessionToken, readyForMonitoring, stb..)
     *
     * @param modifier módosító user
     */
    public void clearRuntimeValuesAndSave(String modifier) {

        super.findAll().stream().map((server) -> {
            server.setSessionToken(null);
            server.setMonitoringServiceReady(null);
            server.setRuntimeSeqId(null);
            server.setModifiedBy(modifier);
            return server;
        }).forEachOrdered((server) -> {
            super.save(server);
        });
    }

    /**
     * Lekéri az adatbázisból az összes rekordot, és a runtimeSeqId feltölti értékekkel
     *
     * @return a runtimeSeqId-vel feltöltött adatbázisrekordok listája
     */
    public List<Server> findAllAndSetRuntimeSeqId() {

        List<Server> servers = super.findAll();
        servers.forEach((server) -> {
            server.setRuntimeSeqId(RuntimeSequenceGenerator.getNextLong());
        });

        return servers;
    }

    /**
     * Kieginfó módosítása
     *
     * @param entity            Server entitás
     * @param modifier          módosító user
     * @param additionalMessage az adatbázisba írandó kieginfo
     */
    public void updateAdditionalMessage(Server entity, String modifier, String additionalMessage) {

        //Kieginfo
        entity.setAdditionalInformation(additionalMessage);

        //Az esetleges optimisticLocking elkerülése végett a Version-t átmásoljuk az adatbzisból imént felolvasott értékre
        Server lastVersion = super.find(entity.getId());
        entity.setOptLockVersion(lastVersion.getOptLockVersion());

        //Le is mentjük az adatbázisba az állapotot
        super.save(entity, modifier);
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
            super.save(lastVersion, modifier);
        }
    }
}
