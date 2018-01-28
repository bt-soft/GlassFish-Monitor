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
import hu.btsoft.gfmon.engine.model.entity.ModifiableEntityBase;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
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

    @Resource
    private EJBContext ejbContext;

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
     * EJB session user lekérdezése (ha lehet)
     *
     * @param defaultUser, ha nincs sessionContext, akkor ez lesz az user
     *
     * @return interaktív user, vagy null
     */
    protected String getSessionUser(String defaultUser) {
        if (ejbContext != null) {
            String user = ejbContext.getCallerPrincipal().getName();
            if (user != null && !"ANONYMOUS".equals(user)) {
                return user;
            }
        }

        return defaultUser;
    }

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
     * Entitás mentése/módosítása userrel
     *
     * @param entity entitás
     * @param user   módosító user
     *
     * @throws ConstraintViolationException validációs hiba
     * @throws PersistenceException         JPA hiba
     * @throws DatabaseException            DB hiba
     */
    public void save(T entity, String user) throws RuntimeException {

        if (entity == null) {
            log.warn("null az entitás!");
            return;
        }

        //Ha nincs sessionContext (pl.: nem WEB-ből/interaktívan hívták), akkor a paraméterben megadott userrel mentünk
        if (entity instanceof ModifiableEntityBase) {
            if (entity.getId() == null) {
                ((ModifiableEntityBase) entity).setCreatedBy(this.getSessionUser(user));

            } else {
                //Csak akkor mentünk, ha valóban van változás az entitásban
                T t = find(entity.getId());
                if (t != null && t.equals(entity)) {
                    return;
                }
                //Mehet a mentés
                //Ha nincs sessionContext, akkor a paraméterben megadott usereel mentünk
                ((ModifiableEntityBase) entity).setModifiedBy(this.getSessionUser(user));
            }
        }

        this.save(entity);
    }

    /**
     * Új entitás létrehozása vagy létező entitás update
     *
     * @param entity entitás példány
     *
     * @throws ConstraintViolationException validációs hiba
     * @throws PersistenceException         JPA hiba
     * @throws DatabaseException            DB hiba
     */
    public void save(T entity) throws RuntimeException {

        if (entity == null) {
            log.warn("null az entitás!");
            return;
        }

        String _user = this.getSessionUser(null);

        try {
            //Új entitás lesz?
            if (entity.getId() == null) {
                //Ha ki tudjuk nyerni az usert, és még nincs beállítva, akkor használjuk
                if (_user != null && entity instanceof ModifiableEntityBase && ((ModifiableEntityBase) entity).getCreatedBy() == null) {
                    ((ModifiableEntityBase) entity).setCreatedBy(_user);
                }
//                log.trace("Persist -> {}", entity);
                getEntityManager().persist(entity);
            } else {
                //Ha ki tudjuk nyerni az usert, és még nincs beállítva, akkor használjuk
                if (_user != null && entity instanceof ModifiableEntityBase && ((ModifiableEntityBase) entity).getModifiedBy() == null) {
                    ((ModifiableEntityBase) entity).setModifiedBy(_user);
                }
//                log.trace("Merge -> {}", entity);
                getEntityManager().merge(entity);
            }
        } catch (ConstraintViolationException e) {
            log.error("Entitás validációs hiba: ");
            e.getConstraintViolations().forEach(err -> log.error("errStr: {}", err));
            throw e;
        } catch (PersistenceException e) {
            log.error("Entitás mentés/update hiba", e);
            throw e;
        } catch (DatabaseException e) {
            log.error("Entitás mentés/update adatbázis hiba", e);
            throw e;
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
     *
     * @throws PersistenceException JPA hiba
     * @throws DatabaseException    DB hiba
     */
    public void remove(T entity) throws RuntimeException {

        if (entity == null) {
            log.warn("null az entitás!");
            return;
        }

        try {
            getEntityManager().remove(getEntityManager().merge(entity));

            //kiíratjuk az adatbázisba az entitás törlését
            getEntityManager().flush();
        } catch (PersistenceException e) {
            log.error("Entitás törlés hiba", e);
            throw e;
        } catch (DatabaseException e) {
            log.error("Entitás törlés adatbázis hiba", e);
            throw e;
        }

    }

    /**
     * Entitás betöltése az adatbázisból (Ha a memóriában entitásban változás történt, akor az most elveszik, az adatbázis állapota lesz a nyerő)
     *
     * @param entiy entitás példány
     */
    public void refresh(T entiy) {
        getEntityManager().refresh(entiy);
    }

    /**
     * Entitás keresése
     *
     * @param id entitás kulcsa
     *
     * @return entitás példány
     */
    public T find(Long id) {
        return getEntityManager().find(entityClass, id);
    }

    /**
     * Összes entitás keresése
     *
     * @return
     */
    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
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
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
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
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> root = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(root));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    /**
     * Ellenőrzi, hogy az entitás táblája létezik-e?
     *
     * @return true -> igen
     */
    public boolean checkEntityTableExist() {

        try {
            return count() > 0;

        } catch (NoResultException e) {
            //oks -> false lesz a visszatérési értékünk
        } catch (Exception e) {
            log.error("DB hiba", e);
        }

        return false;
    }

    /**
     * Adott entitás régi rekordjainak törlése
     *
     * @param entityType entitás class
     * @param beforeDate ennél régeddieket törölje
     *
     * @return törölt entitásrekordok száma
     */
    private int deleteEntityOldRecord(Class<T> entityType, Date beforeDate) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<T> delete = builder.createCriteriaDelete(entityType);
        Root<T> root = delete.from(entityType);

        Predicate predicate = builder.lessThanOrEqualTo(root.<Date>get("createdDate"), beforeDate);
        delete.where(predicate);

        int cnt = getEntityManager().createQuery(delete).executeUpdate();

        log.trace("Entitás: {}, törölt rekordok száma: {}", entityType.getSimpleName(), cnt);

        return cnt;
    }

    /**
     * régi rekordok törlése
     *
     * @param concreateEntityType törlendő entitás class
     * @param keepDays            a törlendő rekordok keletkezési dátuma ennél a napnál régebbi
     *
     * @return összes törölt rekordok száma
     */
    public int deleteOldRecords(Class<T> concreateEntityType, int keepDays) {

        int cnt = 0;
        LocalDate before = LocalDate.now().minusDays(keepDays);
        Date beforeDate = Date.from(before.atStartOfDay(ZoneId.systemDefault()).toInstant());

        cnt = getEntityManager().getMetamodel().getEntities().stream()
                .filter((entity) -> (entity.getClass().isInstance(concreateEntityType)))
                .map((e) -> this.deleteEntityOldRecord(concreateEntityType, beforeDate))
                .reduce(cnt, Integer::sum);

        return cnt;
    }

}
