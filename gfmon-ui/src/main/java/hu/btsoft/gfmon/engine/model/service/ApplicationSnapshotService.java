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

import hu.btsoft.gfmon.engine.model.entity.application.ApplicationSnapshotBase;
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
public class ApplicationSnapshotService extends ServiceBase<ApplicationSnapshotBase> {

    @PersistenceContext(unitName = "gfmon_PU")
    private EntityManager em;

    /**
     * Kontruktor
     */
    public ApplicationSnapshotService() {
        super(ApplicationSnapshotBase.class);
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
        return super.deleteOldRecords(ApplicationSnapshotBase.class, keepDays);

    }

}
