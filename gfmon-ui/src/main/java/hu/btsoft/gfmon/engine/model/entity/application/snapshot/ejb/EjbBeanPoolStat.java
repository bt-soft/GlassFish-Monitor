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
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Cacheable;
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
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbBeanPoolStat extends AppSnapshotBase {

    /**
     * Az EJB pool statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 11)
    private EjbStat ejbStat;

    /**
     * • JmsMaxMessagesLoad
     * <p>
     * Provides the maximum number of messages to load into a JMS session, at a time.
     */
    @ColumnPosition(position = 20)
    private Long jmsMaxMessagesLoad;

    /**
     * • NumBeansInPool
     * <p>
     * Number of EJBs in associated pool
     */
    @ColumnPosition(position = 21)
    private Long numBeansInPool;  //HW-LW érték!

    /**
     * • NumThreadsWaiting
     * <p>
     * Number of threads waiting for free beans
     */
    @ColumnPosition(position = 22)
    private Long numThreadsWaiting;

    /**
     * • TotalBeansCreated
     * <p>
     * Number of beans created in the associated pool
     */
    @ColumnPosition(position = 23)
    private Long totalBeansCreated;

    /**
     * • TotalBeansDestroyed
     * <p>
     * Number of beans destroyed from the associated pool
     */
    @ColumnPosition(position = 24)
    private Long totalBeansDestroyed;

}
