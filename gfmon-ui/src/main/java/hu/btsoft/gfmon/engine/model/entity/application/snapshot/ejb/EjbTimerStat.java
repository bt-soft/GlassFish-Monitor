/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbTimerStat.java
 *  Created: 2018.01.28. 18:12:24
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
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
@Table(name = "APP_EJBTIMER_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"ejbStat"})
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbTimerStat extends AppSnapshotBase {

    /**
     * Az EJB pool statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private EjbStat ejbStat;

    /**
     * • NumTimersCreated
     * <p>
     * Number of timers created in the system
     */
    @Column(name = "NUM_TIMERS_CREATED")
    @ColumnPosition(position = 30)
    private Long numTimersCreated;

    /**
     * • NumTimersDelivered
     * <p>
     * Number of timers delivered by the system
     */
    @Column(name = "NUM_TIMERS_DELIVERED")
    @ColumnPosition(position = 31)
    private Long numTimersDelivered;

    /**
     * • NumTimersRemoved
     * <p>
     * Number of timers removed from the system
     */
    @Column(name = "NUM_TIMERS_REMOVED")
    @ColumnPosition(position = 32)
    private Long numTimersRemoved;

}
