/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbBeanPoolStat.java
 *  Created: 2018.01.26. 12:28:50
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
@Table(name = "APP_EJBPOOL_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"ejbStat"})
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbBeanPoolStat extends EntityBase {

    /**
     * Az EJB pool statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private EjbStat ejbStat;

    /**
     * • JmsMaxMessagesLoad
     * <p>
     * Provides the maximum number of messages to load into a JMS session, at a time.
     */
    @Column(name = "JMS_MAX_MESSAGE_LOAD")
    @ColumnPosition(position = 30)
    private Long jmsMaxMessagesLoad;

    /**
     * • NumBeansInPool
     * <p>
     * Number of EJBs in associated pool
     */
    @Column(name = "NUM_BEANS_IN_POOL")
    @ColumnPosition(position = 31)
    private Long numBeansInPool;  //HW-LW érték!

    @Column(name = "NUM_BEANS_IN_POOL_LW")
    @ColumnPosition(position = 32)
    private Long numBeansInPoolLw;

    @Column(name = "NUM_BEANS_IN_POOL_HW")
    @ColumnPosition(position = 33)
    private Long numBeansInPoolHw;

    @Column(name = "NUM_BEANS_IN_POOL_LB")
    @ColumnPosition(position = 34)
    private Long numBeansInPoolLb;

    @Column(name = "NUM_BEANS_IN_POOL_UB")
    @ColumnPosition(position = 35)
    private Long numBeansInPoolUb;

    /**
     * • NumThreadsWaiting
     * <p>
     * Number of threads waiting for free beans
     */
    @Column(name = "NUM_THREADS_WAITING")
    @ColumnPosition(position = 36)
    private Long numThreadsWaiting;

    @Column(name = "NUM_THREADS_WAITING_LW")
    @ColumnPosition(position = 37)
    private Long numThreadsWaitingLw;

    @Column(name = "NUM_THREADS_WAITING_HW")
    @ColumnPosition(position = 38)
    private Long numThreadsWaitingHw;

    @Column(name = "NUM_THREADS_WAITING_LB")
    @ColumnPosition(position = 39)
    private Long numThreadsWaitingLb;

    @Column(name = "NUM_THREADS_WAITING_UB")
    @ColumnPosition(position = 40)
    private Long numThreadsWaitingUb;

    /**
     * • TotalBeansCreated
     * <p>
     * Number of beans created in the associated pool
     */
    @Column(name = "TOTAL_BEANS_CREATED")
    @ColumnPosition(position = 41)
    private Long totalBeansCreated;

    /**
     * • TotalBeansDestroyed
     * <p>
     * Number of beans destroyed from the associated pool
     */
    @Column(name = "TOTAL_BEANS_DESTROYED")
    @ColumnPosition(position = 42)
    private Long totalBeansDestroyed;

}
