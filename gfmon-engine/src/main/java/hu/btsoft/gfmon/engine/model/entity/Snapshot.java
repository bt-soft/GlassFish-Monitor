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
import javax.persistence.Index;
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
@Table(name = "SNAPSHOT", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME,
        indexes = {
            //@Index(name = "I_SNOT_TIME", columnList = "CREATE_DAT", unique = true), //index a mérési időre
            @Index(name = "I_SNOT_SRVID", columnList = "SERVER_ID"),}) //index a szerver ID-re
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Slf4j
public class Snapshot extends EntityBase {

    /**
     * A mérés melyik szerverhez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVER_ID")
    private Server server;

//--- REST PATH: server/http-service/server/request
    /**
     * The number of open connections
     */
    @Column(name = "httpreq_opconns")
    private Long httpreqCountOpenConnections;

    /**
     * Cumulative value of the error count,
     * with error count representing the number of cases where the response code
     * was greater than or equal to 400
     */
    @Column(name = "httpreq_errcnts")
    private Long httpreqErrorCount;

//--- REST PATH:  server/network/connection-queue
    /**
     * The number of open/active connections
     */
    @Column(name = "network_openconns")
    private Long networkCountOpenConnections;

}
