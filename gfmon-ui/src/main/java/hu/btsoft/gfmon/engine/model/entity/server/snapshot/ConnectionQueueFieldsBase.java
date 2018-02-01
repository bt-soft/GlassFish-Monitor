/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ConnectionQueueFieldsBase.java
 *  Created: 2017.12.27. 10:37:26
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Connection queue mezők entitás ős
 * (Még nem találtam meg, hogy hogyan lehet az @Embeddable entitás mezőit reflectionnal feltérképezni, emiatt a sima abstract osztály implemnetáció
 *
 * @author BT
 */
//@Embeddable
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public abstract class ConnectionQueueFieldsBase extends SnapshotBase {

    /**
     * • countopenconnections
     * <p>
     * The number of open/active connections
     */
    @Column(name = "COUNT_OPEN_CONNECTIONS")
    @ColumnPosition(position = 30)
    private Long countOpenConnections;

    /**
     * • countoverflows
     * <p>
     * Number of times the queue has been too full to accommodate a connection
     */
    @Column(name = "COUNT_OWERFLOWS")
    @ColumnPosition(position = 31)
    private Long countOverflows;

    /**
     * • countqueued
     * <p>
     * Number of connections currently in the queue
     */
    @Column(name = "COUNT_QUEUED")
    @ColumnPosition(position = 32)
    private Long countQueued;

    /**
     * • countqueued15minutesaverage
     * <p>
     * Average number of connections queued in the last 15 minutes
     */
    @Column(name = "COUNT_QUEUED_15MINUTES_AVERAGE")
    @ColumnPosition(position = 33)
    private Long countQueued15MinutesAverage;

    /**
     * • countqueued1minutesaverage
     * <p>
     * Average number of connections queued in the last 1 minute
     */
    @Column(name = "COUNT_QUEUED_1MINUTE_AVERAGE")
    @ColumnPosition(position = 34)
    private Long countQueued1MinuteAverage;

    /**
     * • countqueued5minutesaverage
     * <p>
     * Average number of connections queued in the last 5 minute
     */
    @Column(name = "COUNT_QUEUED_5MINUTES_AVERAGE")
    @ColumnPosition(position = 35)
    private Long countQueued5MinutesAverage;

    /**
     * • counttotalconnections
     * <p>
     * Total number of connections that have been accepted
     */
    @Column(name = "COUNT_TOTAL_QUEUED")
    @ColumnPosition(position = 36)
    private Long countTotalQueued;

    /**
     * • maxqueued
     * <p>
     * Maximum size of the connection queue
     */
    @Column(name = "MAX_QUEUED")
    @ColumnPosition(position = 37)
    private Long maxQueued;

    /**
     * • peakqueued
     * <p>
     * Largest number of connections that were in the queue simultaneously
     */
    @Column(name = "PEAK_QUEUED")
    @ColumnPosition(position = 27)
    private Long peakQueued;

//    /**
//     * • tickstotalqueued
//     *
//     * (Unsupported) Total number of ticks that connections have spent in the queue
//     */
//    @ColumnPosition(position = 28)
//    private Long ticksTotalQueued;
//
}
