/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServiceBase.java
 *  Created: 2017.12.23. 11:58:31
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.exceptions.DatabaseException;

/**
 * JPA Service ős osztály
 *
 * @param <T> entitás típus
 *
 * @author BT
 */
@Slf4j
public abstract class ServiceBase<T extends EntityBase> {

    private final Class<T> entityClass;

    /**
     * Kontruktor
     *
     * @param entityClass entitás típus
     */
    public ServiceBase(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    /**
     * JPA cache -> adatbázis szinkronizálás
     */
    public void flush() {
        //kiíratjuk az adatbázisba a változásokat
        getEntityManager().flush();
    }

    /**
     * Cache frissítés
     */
    public void evict() {
        getEntityManager().getEntityManagerFactory().getCache().evict(entityClass);
    }

    /**
     * Új entitás létrehozása vagy létező entitás update
     *
     * @param entity entitás példány
     */
    public void save(T entity) {
        try {
            //Új elntitás lesz?
            if (entity.getId() == null) {
                getEntityManager().persist(entity);
            } else {
                getEntityManager().merge(entity);
            }
        } catch (PersistenceException e) {
            log.error("Entitás mentés/update hiba", e);
        } catch (DatabaseException e) {
            log.error("Entitás mentés/update adatbázis hiba", e);
        }
    }

    /**
     * Entitás leválasztása a PU-ról
     *
     * @param entity entitás
     */
    public void detach(T entity) {
        getEntityManager().detach(entity);
    }

    /**
     * Entitás törlése
     *
     * @param entity entitás példány
     */
    public void remove(T entity) {
        try {
            getEntityManager().remove(getEntityManager().merge(entity));

            //kiíratjuk az adatbázisba az entitás törlését
            getEntityManager().flush();
        } catch (PersistenceException e) {
            log.error("Entitás törlés hiba", e);
        } catch (DatabaseException e) {
            log.error("Entitás törlés adatbázis hiba", e);
        }

    }

    /**
     * Entitás keresése
     *
     * @param id entitás kulcsa
     *
     * @return entitás példány
     */
    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Összes entitás keresése
     *
     * @return
     */
    public List<T> findAll() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Entitás lekérdezése szélső értékekkel
     *
     * @param range max-min paraméterek
     *
     * @return entitás lista
     */
    public List<T> findRange(int[] range) {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    /**
     * Entitás számosságának lekérdezése
     *
     * @return entitás számossága
     */
    public int count() {
        javax.persistence.criteria.CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        javax.persistence.criteria.Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

}
