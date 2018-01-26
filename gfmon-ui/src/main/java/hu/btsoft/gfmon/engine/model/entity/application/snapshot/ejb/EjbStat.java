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
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
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
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = {"ejbBeanPoolStats", "ejbBeanMethodeStats"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbStat extends AppSnapshotBase {

    /**
     * Az EJB neve
     */
    @NotNull(message = "Az ejbName nem lehet null")
    @Size(min = 3, max = 255, message = "Az ejbName mező hossza {min} és {max} között lehet")
    @Column(name = "EJB_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 19)
    private String ejbName;

    /**
     * • CreateCount
     * <p>
     * Number of times EJB create method is called or 3.x bean is looked up
     */
    @ColumnPosition(position = 20)
    private Long createCount;

    /**
     * • MethodReadyCount
     * <p>
     * Number of stateless session beans in MethodReady state
     */
    @ColumnPosition(position = 21)
    private Long methodReadyCount;      //HW-LW érték!

    /**
     * • RemoveCount
     * <p>
     * Number of times EJB remove method is called
     */
    @ColumnPosition(position = 21)
    private Long removeCount;

    /**
     * EJB Methode stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_EJBMETHODE_STAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 23)
    private List<EjbBeanMethodeStat> ejbBeanMethodeStats;

    /**
     * EJB Bean pool Stat
     */
    @OneToMany(mappedBy = "ejbStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_EJBPOOL_STAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 22)
    private List<EjbBeanPoolStat> ejbBeanPoolStats;

}
