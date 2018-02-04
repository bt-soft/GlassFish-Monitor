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
import hu.btsoft.gfmon.engine.model.entity.jdbc.ConnPool;
import hu.btsoft.gfmon.engine.model.entity.jdbc.JdbcResourceSnapshotBase;
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
@Table(name = "CONNPOOL_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"connPool", "connPoolAppStats"})
@EqualsAndHashCode(callSuper = true, exclude = {"connPool", "connPoolAppStats"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ConnPoolStat extends JdbcResourceSnapshotBase {

    /**
     * A ConnectionPool statisztika melyik ConnPool-hoz tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONNPOOL_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private ConnPool connPool;

    /**
     * • AverageConnWaitTime
     * <p>
     * Average wait-time-duration per successful connection request
     */
    @Column(name = "AVERAGE_CONN_WAIT_TIME")
    @ColumnPosition(position = 30)
    private Long averageConnWaitTime;

    /**
     * • ConnRequestWaitTime
     * <p>
     * The longest and shortest wait times of connection requests. The current value indicates the wait time of the last request that was serviced by the pool.
     */
    @Column(name = "CONN_REQUEST_WAIT_TIME")
    @ColumnPosition(position = 31)
    private Long connRequestWaitTime;

    @Column(name = "CONN_REQUEST_WAIT_TIME_LW")
    @ColumnPosition(position = 32)
    private Long connRequestWaitTimeLw;

    @Column(name = "CONN_REQUEST_WAIT_TIME_HW")
    @ColumnPosition(position = 33)
    private Long connRequestWaitTimeHw;

    /**
     * • NumConnAcquired
     * <p>
     * Number of logical connections acquired from the pool.
     */
    @Column(name = "NUM_CONN_ACQUIRED")
    @ColumnPosition(position = 34)
    private Long numConnAcquired;

    /**
     * • NumConnCreated
     * <p>
     * The number of physical connections that were created since the last reset.
     */
    @Column(name = "NUM_CONN_CREATED")
    @ColumnPosition(position = 35)
    private Long numConnCreated;

    /**
     * • NumConnDestroyed
     * <p>
     * Number of physical connections that were destroyed since the last reset.
     */
    @Column(name = "NUM_CONN_DESTROYED")
    @ColumnPosition(position = 36)
    private Long numConnDestroyed;

    /**
     * • NumConnFailedValidation
     * <p>
     * The total number of connections in the connection pool that failed validation from the start time until the last sample time.
     */
    @Column(name = "NUM_CONN_FAILED_VERIFICATION")
    @ColumnPosition(position = 37)
    private Long numConnFailedValidation;

    /**
     * • NumConnFree
     * <p>
     * The total number of free connections in the pool as of the last sampling.
     */
    @Column(name = "NUM_CONN_FREE")
    @ColumnPosition(position = 38)
    private Long numConnFree;

    @Column(name = "NUM_CONN_FREE_LW")
    @ColumnPosition(position = 39)
    private Long numConnFreeLw;

    @Column(name = "NUM_CONN_FREE_HW")
    @ColumnPosition(position = 40)
    private Long numConnFreeHw;

    /**
     * • NumConnNotSuccessfullyMatched
     * <p>
     * Number of connections rejected during matching
     */
    @Column(name = "NUM_CONN_NOT_SUCCESSFULLY_MATCHED")
    @ColumnPosition(position = 41)
    private Long numConnNotSuccessfullyMatched;

    /**
     * • NumConnReleased
     * <p>
     * Number of logical connections released to the pool.
     */
    @Column(name = "NUM_CONN_RELEASED")
    @ColumnPosition(position = 42)
    private Long numConnReleased;

    /**
     * • NumConnSuccessfullyMatched
     * <p>
     * Number of connections succesfully matched
     */
    @Column(name = "NUM_CONN_SUCCESSFULLY_MATCHED")
    @ColumnPosition(position = 43)
    private Long numConnSuccessfullyMatched;

    /**
     * • NumConnTimedOut
     * <p>
     * The total number of connections in the pool that timed out between the start time and the last sample time.
     */
    @Column(name = "NUM_CONN_TIMED_OUT")
    @ColumnPosition(position = 44)
    private Long numConnTimedOut;

    /**
     * • NumConnUsed
     * <p>
     * Provides connection usage statistics.
     * The total number of connections that are currently being used, as well as information about the maximum number
     * of connections that were used (the high water mark).
     */
    @Column(name = "NUM_CONN_USED")
    @ColumnPosition(position = 45)
    private Long numConnUsed;

    @Column(name = "NUM_CONN_USED_LW")
    @ColumnPosition(position = 46)
    private Long numConnUsedLw;

    @Column(name = "NUM_CONN_USED_HW")
    @ColumnPosition(position = 47)
    private Long numConnUsedHw;

    /**
     * • NumPotentialConnLeak
     * <p>
     * Number of potential connection leaks
     */
    @Column(name = "NUM_POTENTIAL_CONN_LEAK")
    @ColumnPosition(position = 47)
    private Long numPotentialConnLeak;

    /**
     * • WaitQueueLength
     * <p>
     * Number of potential connection leaks
     */
    @Column(name = "WAIT_QUEUE_LENGTH")
    @ColumnPosition(position = 49)
    private Long waitQueueLength;

    //-- Az alkalmazások ConnectionPool statisztika mérési eredményei
    @OneToMany(mappedBy = "connPoolStat", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 70)
    private List<ConnPoolAppStat> connPoolAppStats = new LinkedList<>();
}
