/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcResourcesSnapshotService.java
 *  Created: 2018.01.28. 10:58:09
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.service;

import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcResourceSnapshotBase;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Stateless
@Slf4j
public class JdbcResourcesSnapshotService extends ServiceBase<JdbcResourceSnapshotBase> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public JdbcResourcesSnapshotService() {
        super(JdbcResourceSnapshotBase.class);
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
     * régi rekordok törlése
     *
     * @param keepDays a törlendő rekordok keletkezési dátuma ennél a napnál régebbi
     *
     * @return összes törölt rekordok száma
     */
    public int deleteOldRecords(int keepDays) {
        return super.deleteOldRecords(JdbcResourceSnapshotBase.class, keepDays);

    }

}
