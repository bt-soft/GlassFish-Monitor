/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    ConnPoolCollectorDataUnitService.java
 *  Created: 2018.01.06. 16:08:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.model.dto.DataUnitDto;
import hu.btsoft.gfmon.engine.model.entity.connpool.ConnPoolCollDataUnit;
import java.util.List;
import java.util.Set;
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
public class ConnPoolCollectorDataUnitService extends ServiceBase<ConnPoolCollDataUnit> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ConnPoolCollectorDataUnitService() {
        super(ConnPoolCollDataUnit.class);
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
    public List<ConnPoolCollDataUnit> findAll() {
        Query query = em.createNamedQuery("ConnPoolCollDataUnit.findAll");
        List<ConnPoolCollDataUnit> queryResult = query.getResultList();
        return queryResult;
    }

    /**
     * Az összes mérhető Rest Path lekérdezése
     *
     * @return path lista
     */
    public List<String> getAllRestPathMasks() {
        Query query = em.createNamedQuery("ConnPoolCollDataUnit.findAllRestPathMasks");
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
    public List<ConnPoolCollDataUnit> getAllPaths(String restPathMask) {
        Query query = em.createNamedQuery("ConnPoolCollDataUnit.findAllRestPathMasks");
        query.setParameter("restPathMask", restPathMask);
        List<ConnPoolCollDataUnit> result = query.getResultList();

        return result;
    }

    /**
     * Összegyűjtött adatnevek mentése
     *
     * @param dataUnits   összegyűjtött adatnevek halmaza
     * @param creatorUser létrehozó user
     */
    public void saveCollectedDataUnits(Set<DataUnitDto> dataUnits, String creatorUser) {

        log.info("JDBC ConnectionPool monitor adatnevek táblájának felépítése indul");
        long start = Elapsed.nowNano();

        //Végigmegyünk az összes adatneven és jól beírjuk az adatbázisba őket
        dataUnits.stream()
                .map((dto) -> new ConnPoolCollDataUnit(dto.getRestPath(), dto.getEntityName(), dto.getDataName(), dto.getUnit(), dto.getDescription()))
                .forEachOrdered((cdu) -> {
                    super.save(cdu, creatorUser);
                });

        log.info("JDBC ConnectionPool monitor adatnevek felépítése OK, adatnevek: {}db, elapsed: {}", dataUnits.size(), Elapsed.getElapsedNanoStr(start));
    }

}
