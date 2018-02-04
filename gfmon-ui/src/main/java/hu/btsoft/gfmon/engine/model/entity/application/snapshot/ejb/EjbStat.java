/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbStat.java
 *  Created: 2018.01.26. 12:24:27
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Table(name = "APP_EJBSTAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"application", "ejbBeanPoolStats", "ejbBeanMethodStats", "ejbTimersStats", "ejbBeanCacheStat"})
@EqualsAndHashCode(callSuper = true, exclude = {"application", "ejbBeanPoolStats", "ejbBeanMethodStats", "ejbTimersStats", "ejbBeanCacheStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbStat extends AppSnapshotBase {

    /**
     * A mérés melyik alkalmazáshoz tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private Application application;

    /**
     * Az EJB neve
     */
    @NotNull(message = "Az ejbName nem lehet null")
    @Size(min = 3, max = 255, message = "Az ejbName mező hossza {min} és {max} között lehet")
    @Column(name = "EJB_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 30)
    private String ejbName;

    /**
     * • CreateCount
     * <p>
     * Number of times EJB create method is called or 3.x bean is looked up
     */
    @Column(name = "CREATE_COUNT")
    @ColumnPosition(position = 31)
    private Long createCount;

    /**
     * • MethodReadyCount
     * <p>
     * Number of stateless session beans in MethodReady state
     */
    @Column(name = "METHOD_READY_COUNT")
    @ColumnPosition(position = 32)
    private Long methodReadyCount;      //HW-LW + LoweBound-UpperBound érték

    @Column(name = "METHOD_READY_COUNT_LW")
    @ColumnPosition(position = 33)
    private Long methodReadyCountLw;      //LowWaterMark

    @Column(name = "METHOD_READY_COUNT_HW")
    @ColumnPosition(position = 34)
    private Long methodReadyCountHw;      //HighWaterMark

    @Column(name = "METHOD_READY_COUNT_LB")
    @ColumnPosition(position = 35)
    private Long methodReadyCountLb;      //LowerBound

    @Column(name = "METHOD_READY_COUNT_UB")
    @ColumnPosition(position = 36)
    private Long methodReadyCountUb;      //UpperBound

    /**
     * • RemoveCount
     * <p>
     * Number of times EJB remove method is called
     */
    @Column(name = "REMOVE_COUNT")
    @ColumnPosition(position = 37)
    private Long removeCount;
//
//
//
    /**
     * EJB Methode stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 70)
    private List<EjbBeanMethodStat> ejbBeanMethodStats = new LinkedList<>();

    /**
     * EJB Bean pool Stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 71)
    private List<EjbBeanPoolStat> ejbBeanPoolStats = new LinkedList<>();

    /**
     * EJB Timers Stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 72)
    private List<EjbTimerStat> ejbTimersStats = new LinkedList<>();

    /**
     * EJB BeanCache Stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 73)
    private List<EjbBeanCacheStat> ejbBeanCacheStat = new LinkedList<>();

}
