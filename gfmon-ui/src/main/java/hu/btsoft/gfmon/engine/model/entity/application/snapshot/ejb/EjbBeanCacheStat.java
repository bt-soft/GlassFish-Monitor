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
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.Column;
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
public class EjbBeanCacheStat extends EntityBase {

    /**
     * Az EJB pool statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private EjbStat ejbStat;

    /**
     * • CacheHits
     * <p>
     * Provides the number of times a user request hits an EJB in associated EJB cache instance
     */
    @Column(name = "CACHE_HITS")
    @ColumnPosition(position = 30)
    private Long cacheHits;

    @Column(name = "CACHE_HITS_LW")
    @ColumnPosition(position = 30)
    private Long cacheHitsLw;

    @Column(name = "CACHE_HITS_HW")
    @ColumnPosition(position = 30)
    private Long cacheHitsHw;

    @Column(name = "CACHE_HITS_LB")
    @ColumnPosition(position = 30)
    private Long cacheHitsLb;

    @Column(name = "CACHE_HITS_UB")
    @ColumnPosition(position = 30)
    private Long cacheHitsUb;

    /**
     * • CacheMisses
     * <p>
     * Provides the number of times a user request fails to find an EJB in associated EJB cache instance
     */
    @Column(name = "CACHE_MISSES")
    @ColumnPosition(position = 31)
    private Long cacheMisses;

    @Column(name = "CACHE_MISSES_LW")
    @ColumnPosition(position = 31)
    private Long cacheMissesLw;

    @Column(name = "CACHE_MISSES_HW")
    @ColumnPosition(position = 31)
    private Long cacheMissesHw;

    @Column(name = "CACHE_MISSES_LB")
    @ColumnPosition(position = 31)
    private Long cacheMissesLb;

    @Column(name = "CACHE_MISSES_UB")
    @ColumnPosition(position = 31)
    private Long cacheMissesUb;

    /**
     * • NumBeansInCache
     * <p>
     * Provides total number of EJBs in the associated EJB Cache.
     */
    @Column(name = "NUM_BEANS_IN_CACHE")
    @ColumnPosition(position = 32)
    private Long numBeansInCache;

    @Column(name = "NUM_BEANS_IN_CACHE_LW")
    @ColumnPosition(position = 32)
    private Long numBeansInCacheLw;

    @Column(name = "NUM_BEANS_IN_CACHE_HW")
    @ColumnPosition(position = 32)
    private Long numBeansInCacheHw;

    @Column(name = "NUM_BEANS_IN_CACHE_LB")
    @ColumnPosition(position = 32)
    private Long numBeansInCacheLb;

    @Column(name = "NUM_BEANS_IN_CACHE_UB")
    @ColumnPosition(position = 32)
    private Long numBeansInCacheUb;

    /**
     * • NumExpiredSessionsRemoved
     * <p>
     * Provides a count value reflecting the number of expired sessions that were removed from the bean cache.
     */
    @Column(name = "NUM_EXPIRED_SESSIONS_REMOVED")
    @ColumnPosition(position = 33)
    private Long numExpiredSessionsRemoved;

    /**
     * • NumPassivationErrors
     * <p>
     * Provides a count value reflecting the number of errors that occured while passivating a StatefulSessionBean from the bean cache.
     */
    @Column(name = "NUM_PASSIVATIONS_ERRORS")
    @ColumnPosition(position = 34)
    private Long numPassivationErrors;

    /**
     * • NumPassivations
     * <p>
     * Provides a count value reflecting the number of passivations for a StatefulSessionBean from the bean cache.
     */
    @Column(name = "NUM_PASSIVATIONS")
    @ColumnPosition(position = 35)
    private Long numPassivations;

    /**
     * • NumPassivationSuccess
     * <p>
     * Provides a count value reflecting the number of passivations for a StatefulSessionBean from the bean cache that succeeded
     */
    @Column(name = "NUM_PASSIVATION_SUCCESS")
    @ColumnPosition(position = 36)
    private Long numPassivationSuccess;

}
