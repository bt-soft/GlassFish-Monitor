/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SvrSnapshotService.java
 *  Created: 2017.12.25. 17:15:04
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Server Snapshot entitás kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class SvrSnapshotService extends ServiceBase<SnapshotBase> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public SvrSnapshotService() {
        super(SnapshotBase.class);
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

//    /**
//     * Adott entitás régi rekordjainak törlése
//     *
//     * @param <T>        entitás típus
//     * @param entityType entitás class
//     * @param beforeDate ennél régeddieket törölje
//     *
//     * @return törölt entitásrekordok száma
//     */
//    private <T extends SnapshotBase> int deleteEntityOldRecord(Class<T> entityType, Date beforeDate) {
//        CriteriaBuilder builder = em.getCriteriaBuilder();
//        CriteriaDelete<T> delete = builder.createCriteriaDelete(entityType);
//        Root<T> root = delete.from(entityType);
//
//        Predicate predicate = builder.lessThanOrEqualTo(root.<Date>get("createdDate"), beforeDate);
//        delete.where(predicate);
//
//        int cnt = em.createQuery(delete).executeUpdate();
//
//        log.trace("Entitás: {}, törölt rekordok száma: {}", entityType.getSimpleName(), cnt);
//
//        return cnt;
//    }
    /**
     * régi rekordok törlése
     *
     * @param keepDays a törlendő rekordok keletkezési dátuma ennél a napnál régebbi
     *
     * @return összes törölt rekordok száma
     */
    public int deleteOldRecords(int keepDays) {

//        int cnt = 0;
//        LocalDate before = LocalDate.now().minusDays(keepDays);
//        Date beforeDate = Date.from(before.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        cnt = em.getMetamodel().getEntities().stream()
//                .filter((entity) -> (entity instanceof SnapshotBase))
//                .map((entity) -> (SnapshotBase) entity)
//                .map((snapshotEntity) -> deleteEntityOldRecord(snapshotEntity.getClass(), beforeDate))
//                .reduce(cnt, Integer::sum);
//
//        return cnt;
        return super.deleteOldRecords(SnapshotBase.class, keepDays);

    }

}
