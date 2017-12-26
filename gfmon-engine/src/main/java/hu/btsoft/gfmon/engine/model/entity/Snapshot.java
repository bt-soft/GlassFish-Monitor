/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Snapshot.java
 *  Created: 2017.12.24. 15:54:24
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Egy GF szervernek a monitorozás alatt mért aktuális értékei
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "SNAPSHOT", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public class Snapshot extends EntityBase {

    /**
     * A mérés melyik szerverhez tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVER_ID")
    private Server server;

//
//--- REST PATH: monitoring/domain/server/http-service/server
//
    /**
     * • countopenconnections
     *
     * The number of open connections
     */
    @Column(name = "HREQ_OPCONNS")
    private Long httpreqOpenConnectionsCnt;

    /**
     * • errorcount
     *
     * Cumulative value of the error count,
     * with error count representing the number of cases where the response code
     * was greater than or equal to 400
     */
    @Column(name = "HREQ_ERRCNTS")
    private Long httpreqErrorCnt;

//    /**
//     * • maxopenconnections
//     *
//     * The maximum number of open connections
//     */
//    @Column(name = "HREQ_MAXOPENCONNS")
//    private Long httpreqMaxOpenConnectionsCnt;
//
    /**
     * • maxtime
     *
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    @Column(name = "HREQ_TIME_MAX")
    private Long httpreqMaxTime;

    /**
     * • processingtime
     *
     * Average request processing time
     */
    @Column(name = "HREQ_TIME_PROC")
    private Long httpreqProcTime;

//
//--- REST PATH:  monitoring/domain/server/jvm/memory
//
    /**
     * • initheapsize-count
     *
     * Amount of memory in bytes that the Java virtual machine initially
     * requests from the operating system for memory management
     */
    @Column(name = "MEM_HEAP_INIT")
    private Long initHeapSizeCnt;

    /**
     * • maxheapsize-count
     *
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @Column(name = "MEM_HEAP_MAX")
    private Long maxHeapSizeCnt;

    /**
     * • usedheapsize-count
     *
     * Amount of used memory in bytes
     */
    @Column(name = "MEM_HEAP_USED")
    private Long usedHeapSizeCnt;

//--- REST PATH:  monitoring/domain/server/jvm/thread-system
    /**
     * • daemonthreadcount
     *
     * Amount of used memory in bytes
     */
    @Column(name = "THRD_DAEMTHRCNT")
    private Long deamonThreadCnt;

    /**
     * • peakthreadcount
     *
     * Returns the peak live thread count since the Java virtual machine started or peak was reset
     */
    @Column(name = "THRD_DAEMTHRCNT_PEAK")
    private Long deamonThreadPeak;

    /**
     * • threadcount
     *
     * Returns the current number of live threads including both daemon and non-daemon threads
     */
    @Column(name = "THRD_THRCNT")
    private Long threadCount;

    /**
     * • totalstartedthreadcount
     *
     * Returns the total number of threads created and also started since the Java virtual machine started
     */
    @Column(name = "THRD_THRCNT_TOTAL_STRD")
    private Long totalStartedThreadCount;

//
//--- REST PATH:  monitoring/domain/server/network/connection-queue
//
    /**
     * • countopenconnections
     *
     * The number of open/active connections
     */
    @Column(name = "NCONQ_OPENCONNS")
    private Long networkOpenConnectionsCnt;

    /**
     * • countqueued
     *
     * Number of connections currently in the queue
     */
    @Column(name = "NCONQ_QUEDCONNS")
    private Long networkQueuedConnectionsCnt;

    /**
     * • peakqueued
     *
     * Largest number of connections that were in the queue simultaneously
     */
    @Column(name = "NCONQ_QUEDCONNS_PEAK")
    private Long networkQueuedConnectionsPeak;

//
//--- REST PATH:  monitoring/domain/server/network/http-listener-1/connection-queue
//
    /**
     * • countopenconnections
     *
     * The number of open/active connections
     */
    @Column(name = "NHL1CONQ_OPENCONNS")
    private Long httpListener1OpenConnectionsCnt;

    /**
     * • countqueued
     *
     * Number of connections currently in the queue
     */
    @Column(name = "NHL1CONQ_QUEDCONNS")
    private Long httpListener1QueuedConnectionsCnt;

    /**
     * • peakqueued
     *
     * Largest number of connections that were in the queue simultaneously
     */
    @Column(name = "NHL1CONQ_QUEDCONNS_PEAK")
    private Long httpListener1QueuedConnectionsPeak;

//
//--- REST PATH:  monitoring/domain/server/network/http-listener-1/keep-alive
//
    /**
     * • countconnections
     *
     * Number of connections in keep-alive mode
     */
    @Column(name = "NHL1KALIVE_COUNTCONNS")
    private Long httpListener1KeepAliveConnections;
}
