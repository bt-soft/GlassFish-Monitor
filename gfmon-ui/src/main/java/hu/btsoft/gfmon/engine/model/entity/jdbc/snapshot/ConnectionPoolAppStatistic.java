/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnectionPoolAppStatistic.java
 *  Created: 2018.01.27. 19:01:46
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcResourceSnapshotBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "RES_CONPOOL_APP_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"connectionPoolStatistic", "application"})
@EqualsAndHashCode(callSuper = true, exclude = {"connectionPoolStatistic", "application"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ConnectionPoolAppStatistic extends JdbcResourceSnapshotBase {

    /**
     * A mérés melyik ConnectionPool statisztikához tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RES_CONPOOL_STAT_ID")
    @ColumnPosition(position = 10)
    private ConnectionPoolStatistic connectionPoolStatistic;

    /**
     * A ConnectionPool Allmaazásstsisztika melyik alklamazáshoz?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID")
    @ColumnPosition(position = 11)
    private Application application;

    /**
     * Az alkalmazás neve neve, ami használja a ConnectionPool-t
     */
    @NotNull(message = "A appName nem lehet null")
    @Size(min = 3, max = 255, message = "A appName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 20)
    private String appName;

    /**
     * • NumConnAcquired
     * <p>
     * Number of logical connections acquired from the pool.
     */
    @ColumnPosition(position = 21)
    private Long numConnAcquired;

    /**
     * • NumConnReleased
     * <p>
     * Number of logical connections released to the pool.
     */
    @ColumnPosition(position = 22)
    private Long numConnReleased;

    /**
     * • NumConnUsed
     * <p>
     * Provides connection usage statistics.
     * The total number of connections that are currently being used, as well as information about the maximum number
     * of connections that were used (the high water mark).
     */
    @ColumnPosition(position = 23)
    private Long numConnUsed;

    @ColumnPosition(position = 24)
    private Long numConnUsedLw;

    @ColumnPosition(position = 25)
    private Long numConnUsedHw;

}
