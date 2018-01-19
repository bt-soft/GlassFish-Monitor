/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    CollectorDataUnitService.java
 *  Created: 2018.01.06. 16:08:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.server.CollectorDataUnit;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * CollectorDataUnit (CDU) entitások kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class CollectorDataUnitService extends ServiceBase<CollectorDataUnit> {

    @PersistenceContext(unitName = "gfmon_PU")
    private EntityManager em;

    /**
     * Kontruktor
     */
    public CollectorDataUnitService() {
        super(CollectorDataUnit.class);
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
    public List<CollectorDataUnit> findAll() {
        Query query = em.createNamedQuery("CollectorDataUnit.findAll");
        List<CollectorDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * A szerver ID alapján kikeresi az összes CDU-t
     *
     * @param serverId szerver id-je
     *
     * @return a szerver összes CDU list
     */
    public List<CollectorDataUnit> findByServerId(Long serverId) {
        Query query = em.createNamedQuery("CollectorDataUnit.findByServerId");
        query.setParameter("serverId", serverId);
        List<CollectorDataUnit> queryResult = query.getResultList();

        return queryResult;
    }

    /**
     * A szerver ID alapján kikeresi az aktív CDU-kat
     *
     * @param serverId szerver id-je
     *
     * @return a szerveren aktív CDU lista
     */
    public List<CollectorDataUnit> findByActiveAndServerId(Long serverId) {
        Query query = em.createNamedQuery("CollectorDataUnit.findByActiveAndServerId");
        query.setParameter("serverId", serverId);
        List<CollectorDataUnit> queryResult = query.getResultList();

        return queryResult;
    }

//    /**
//     * Entitás lista -> DTO map
//     *
//     * @param entities entitás lista
//     *
//     * @return dto lista vagy null
//     */
//    private List<DataUnitDto> mapEntityToDto(List<CollectorDataUnit> entities) {
//        if (entities == null) {
//            return null;
//        }
//
//        List<DataUnitDto> result = new LinkedList<>();
//        entities.stream().map(
//                (entity) -> new DataUnitDto(entity.getRestPath(), entity.getEntityName(), entity.getDataName(), entity.getUnit(), entity.getDescription())
//        ).forEachOrdered((dto) -> {
//            result.add(dto);
//        });
//
//        return result;
//    }
    /**
     * Az összes mérhető Rest Path lekérdezése
     *
     * @return path lista
     */
    public List<String> getAllPaths() {
        Query query = em.createNamedQuery("CollectorDataUnit.findAllPaths");
        List<String> result = query.getResultList();

        return result;
    }

//    /**
//     * Path alapján kikeresi a CDU-kat
//     *
//     * @param restPath path
//     *
//     * @return CDU lista
//     */
//    public List<DataUnitDto> findByPath(String restPath) {
//        Query query = em.createNamedQuery("CollectorDataUnit.findByPath");
//        query.setParameter("restPath", restPath);
//        List<CollectorDataUnit> queryResult = query.getResultList();
//
//        return mapEntityToDto(queryResult);
//    }
}
