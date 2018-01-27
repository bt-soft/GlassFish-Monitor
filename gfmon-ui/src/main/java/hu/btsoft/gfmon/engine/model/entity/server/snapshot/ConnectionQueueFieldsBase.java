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
     *
     * The number of open/active connections
     */
    @ColumnPosition(position = 20)
    private Long countOpenConnections;

    /**
     * • countoverflows
     *
     * Number of times the queue has been too full to accommodate a connection
     */
    @ColumnPosition(position = 21)
    private Long countOverflows;

    /**
     * • countqueued
     *
     * Number of connections currently in the queue
     */
    @ColumnPosition(position = 22)
    private Long countQueued;

    /**
     * • countqueued15minutesaverage
     *
     * Average number of connections queued in the last 15 minutes
     */
    @ColumnPosition(position = 23)
    private Long countQueued15MinutesAverage;

    /**
     * • countqueued1minutesaverage
     *
     * Average number of connections queued in the last 1 minute
     */
    @ColumnPosition(position = 24)
    private Long countQueued1MinuteAverage;

    /**
     * • countqueued5minutesaverage
     *
     * Average number of connections queued in the last 5 minute
     */
    @ColumnPosition(position = 25)
    private Long countQueued5MinutesAverage;

    /**
     * • counttotalconnections
     *
     * Total number of connections that have been accepted
     */
    @ColumnPosition(position = 26)
    private Long countTotalQueued;

    /**
     * • maxqueued
     *
     * Maximum size of the connection queue
     */
    @ColumnPosition(position = 27)
    private Long maxQueued;

    /**
     * • peakqueued
     *
     * Largest number of connections that were in the queue simultaneously
     */
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
