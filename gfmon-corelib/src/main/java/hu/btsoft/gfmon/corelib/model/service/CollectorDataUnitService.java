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
package hu.btsoft.gfmon.corelib.model.service;

import hu.btsoft.gfmon.corelib.model.dto.DataUnitDto;
import hu.btsoft.gfmon.corelib.model.entity.CollectorDataUnit;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * CollectorDataUnit entitások kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class CollectorDataUnitService extends ServiceBase<CollectorDataUnit> {

    @PersistenceContext
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
     * Az összes entitás lekérdezése
     *
     * @return path lista
     */
    public List<CollectorDataUnit> getAll() {
        Query query = em.createNamedQuery("CollectorDataUnit.findAll");
        List<CollectorDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * Entitás lista -> DTO map
     *
     * @param entities entitás lista
     *
     * @return dto lista vagy null
     */
    private List<DataUnitDto> mapEntityToDto(List<CollectorDataUnit> entities) {
        if (entities == null) {
            return null;
        }

        List<DataUnitDto> result = new LinkedList<>();
        entities.stream().map(
                (entity) -> new DataUnitDto(entity.getRestPath(), entity.getEntityName(), entity.getDataName(), entity.getUnit(), entity.getDescription())
        ).forEachOrdered((dto) -> {
            result.add(dto);
        });

        return result;
    }

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

    /**
     *
     * @param restPath
     *
     * @return
     */
    public List<DataUnitDto> findByPath(String restPath) {
        Query query = em.createNamedQuery("CollectorDataUnit.findByPath");
        query.setParameter("restPath", restPath);
        List<CollectorDataUnit> queryResult = query.getResultList();

        return mapEntityToDto(queryResult);
    }
}
