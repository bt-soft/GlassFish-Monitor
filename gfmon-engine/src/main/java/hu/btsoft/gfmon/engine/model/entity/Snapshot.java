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

//--- REST PATH: server/http-service/server/request
    /**
     * The number of open connections
     */
    @Column(name = "HREQ_OPCONNS")
    private Long httpreqOpenConnectionsCnt;

    /**
     * Cumulative value of the error count,
     * with error count representing the number of cases where the response code
     * was greater than or equal to 400
     */
    @Column(name = "HREQ_ERRCNTS")
    private Long httpreqErrorCnt;

//--- REST PATH:  server/network/connection-queue
    /**
     * The number of open/active connections
     */
    @Column(name = "NET_OPENCONNS")
    private Long networkOpenConnectionsCnt;

//--- REST PATH:  server/jvm/memory
    /**
     * Amount of memory in bytes that the Java virtual machine initially
     * requests from the operating system for memory management
     */
    @Column(name = "MEM_INITHEAP")
    private Long initHeapSizeCnt;

    /**
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @Column(name = "MEM_MAXHEAP")
    private Long maxHeapSizeCnt;

    /**
     * Amount of used memory in bytes
     */
    @Column(name = "MEM_USEDHEAP")
    private Long usedHeapSizeCnt;
}
