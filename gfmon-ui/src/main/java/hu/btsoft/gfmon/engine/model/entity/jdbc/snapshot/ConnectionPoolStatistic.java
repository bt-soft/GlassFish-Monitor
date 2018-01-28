/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnectionPool.java
 *  Created: 2018.01.27. 18:48:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcConnectionPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcResourceSnapshotBase;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "JDBC_CONPOOL_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"jdbcConnectionPool", "connectionPoolAppStatistic"})
@EqualsAndHashCode(callSuper = true, exclude = {"jdbcConnectionPool", "connectionPoolAppStatistic"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ConnectionPoolStatistic extends JdbcResourceSnapshotBase {

    /**
     * A ConnectionPool statisztika melyik JdbcConnectionPool-hoz tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JDBC_CONNECTION_POOL_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 10)
    private JdbcConnectionPool jdbcConnectionPool;

    /**
     * • AverageConnWaitTime
     * <p>
     * Average wait-time-duration per successful connection request
     */
    @ColumnPosition(position = 21)
    private Long averageConnWaitTime;

    /**
     * • ConnRequestWaitTime
     * <p>
     * The longest and shortest wait times of connection requests. The current value indicates the wait time of the last request that was serviced by the pool.
     */
    @ColumnPosition(position = 22)
    private Long connRequestWaitTime;

    @ColumnPosition(position = 23)
    private Long connRequestWaitTimeLw;

    @ColumnPosition(position = 24)
    private Long connRequestWaitTimeHw;

    /**
     * • NumConnAcquired
     * <p>
     * Number of logical connections acquired from the pool.
     */
    @ColumnPosition(position = 25)
    private Long numConnAcquired;

    /**
     * • NumConnCreated
     * <p>
     * The number of physical connections that were created since the last reset.
     */
    @ColumnPosition(position = 26)
    private Long numConnCreated;

    /**
     * • NumConnDestroyed
     * <p>
     * Number of physical connections that were destroyed since the last reset.
     */
    @ColumnPosition(position = 27)
    private Long numConnDestroyed;

    /**
     * • NumConnFailedValidation
     * <p>
     * The total number of connections in the connection pool that failed validation from the start time until the last sample time.
     */
    @ColumnPosition(position = 28)
    private Long numConnFailedValidation;

    /**
     * • NumConnFree
     * <p>
     * The total number of free connections in the pool as of the last sampling.
     */
    @ColumnPosition(position = 28)
    private Long numConnFree;

    @ColumnPosition(position = 29)
    private Long numConnFreeLw;

    @ColumnPosition(position = 30)
    private Long numConnFreeHw;

    /**
     * • NumConnNotSuccessfullyMatched
     * <p>
     * Number of connections rejected during matching
     */
    @ColumnPosition(position = 31)
    private Long numConnNotSuccessfullyMatched;

    /**
     * • NumConnReleased
     * <p>
     * Number of logical connections released to the pool.
     */
    @ColumnPosition(position = 32)
    private Long numConnReleased;

    /**
     * • NumConnSuccessfullyMatched
     * <p>
     * Number of connections succesfully matched
     */
    @ColumnPosition(position = 33)
    private Long numConnSuccessfullyMatched;

    /**
     * • NumConnTimedOut
     * <p>
     * The total number of connections in the pool that timed out between the start time and the last sample time.
     */
    @ColumnPosition(position = 34)
    private Long numConnTimedOut;

    /**
     * • NumConnUsed
     * <p>
     * Provides connection usage statistics.
     * The total number of connections that are currently being used, as well as information about the maximum number
     * of connections that were used (the high water mark).
     */
    @ColumnPosition(position = 35)
    private Long numConnUsed;

    @ColumnPosition(position = 35)
    private Long numConnUsedLw;

    @ColumnPosition(position = 36)
    private Long numConnUsedHw;

    /**
     * • NumPotentialConnLeak
     * <p>
     * Number of potential connection leaks
     */
    @ColumnPosition(position = 37)
    private Long numPotentialConnLeak;

    /**
     * • WaitQueueLength
     * <p>
     * Number of potential connection leaks
     */
    @ColumnPosition(position = 37)
    private Long waitQueueLength;

    //-- Az alkalmazások ConnectionPool statisztika mérési eredményei
    @OneToMany(mappedBy = "connectionPoolStatistic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "JDBC_CONPOOL_APP_STAT_ID", referencedColumnName = "ID", nullable = false)
    private List<ConnectionPoolAppStatistic> connectionPoolAppStatistic;
}
