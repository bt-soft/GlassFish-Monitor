/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SnapshotService.java
 *  Created: 2017.12.25. 17:15:04
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.service;

import hu.btsoft.gfmon.corelib.model.entity.snapshot.SnapshotBase;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Snapshot entitás kezelése
 *
 * @author BT
 */
@Stateless
@Slf4j
public class SnapshotService extends ServiceBase<SnapshotBase> {

    @PersistenceContext
    private EntityManager em;

    /**
     * Kontruktor
     */
    public SnapshotService() {
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

}
