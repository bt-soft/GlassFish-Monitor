/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbBeanCacheStat.java
 *  Created: 2018.01.28. 18:12:24
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Table(name = "APP_EJBBEANCACHE_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"ejbStat"})
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbBeanCacheStat extends AppSnapshotBase {

    /**
     * Az EJB pool statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 11)
    private EjbStat ejbStat;

    /**
     * • CacheHits
     * <p>
     * Provides the number of times a user request hits an EJB in associated EJB cache instance
     */
    @ColumnPosition(position = 20)
    private Long cacheHits;

    /**
     * • CacheMisses
     * <p>
     * Provides the number of times a user request fails to find an EJB in associated EJB cache instance
     */
    @ColumnPosition(position = 21)
    private Long cacheMisses;

    /**
     * • NumBeansInCache
     * <p>
     * Provides total number of EJBs in the associated EJB Cache.
     */
    @ColumnPosition(position = 22)
    private Long numBeansInCache;

    /**
     * • NumExpiredSessionsRemoved
     * <p>
     * Provides a count value reflecting the number of expired sessions that were removed from the bean cache.
     */
    @ColumnPosition(position = 23)
    private Long numExpiredSessionsRemoved;

    /**
     * • NumPassivationErrors
     * <p>
     * Provides a count value reflecting the number of errors that occured while passivating a StatefulSessionBean from the bean cache.
     */
    @ColumnPosition(position = 24)
    private Long numPassivationErrors;

    /**
     * • NumPassivations
     * <p>
     * Provides a count value reflecting the number of passivations for a StatefulSessionBean from the bean cache.
     */
    @ColumnPosition(position = 25)
    private Long numPassivations;

    /**
     * • NumPassivationSuccess
     * <p>
     * Provides a count value reflecting the number of passivations for a StatefulSessionBean from the bean cache that succeeded
     */
    @ColumnPosition(position = 26)
    private Long numPassivationSuccess;

}
