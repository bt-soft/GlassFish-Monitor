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
import javax.persistence.Transient;
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
@Table(name = "JDBC_CONECTION_POOL_APP_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
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
    @JoinColumn(name = "JDNC_CONECTION_POOL_STAT_ID")
    @ColumnPosition(position = 20)
    private ConnectionPoolStatistic connectionPoolStatistic;

    /**
     * A ConnectionPool Allmaazásstsisztika melyik alklamazáshoz?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID")
    @ColumnPosition(position = 21)
    private Application application;

    /**
     * Az alkalmazás neve neve, ami használja a ConnectionPool-t
     */
    @Transient //Ezt az 'application'-ból ki lehet találni, felesleges tárolni, csak runtime használjuk
    private String appName;

    /**
     * • NumConnAcquired
     * <p>
     * Number of logical connections acquired from the pool.
     */
    @Column(name = "NUM_CONN_ACQUIRED")
    @ColumnPosition(position = 30)
    private Long numConnAcquired;

    /**
     * • NumConnReleased
     * <p>
     * Number of logical connections released to the pool.
     */
    @Column(name = "NUM_CONN_RELEASED")
    @ColumnPosition(position = 31)
    private Long numConnReleased;

    /**
     * • NumConnUsed
     * <p>
     * Provides connection usage statistics.
     * The total number of connections that are currently being used, as well as information about the maximum number
     * of connections that were used (the high water mark).
     */
    @Column(name = "NUM_CONN_USED")
    @ColumnPosition(position = 32)
    private Long numConnUsed;

    @Column(name = "NUM_CONN_USED_LW")
    @ColumnPosition(position = 33)
    private Long numConnUsedLw;

    @Column(name = "NUM_CONN_USED_HW")
    @ColumnPosition(position = 34)
    private Long numConnUsedHw;

}
