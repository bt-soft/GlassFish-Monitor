/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpServiceRequest.java
 *  Created: 2017.12.26. 11:54:52
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.snapshot;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/http-service/server
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_HTTPSERVICEREQUEST", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class HttpServiceRequest extends SnapshotBase {

    /**
     * • count200
     *
     * Number of responses with a status code equal to 200
     *
     */
    private Long count200;

    /**
     * • count2xx
     *
     * Number of responses with a status code in the 2xx range
     */
    private Long count2xx;

    /**
     * • count302
     *
     * Number of responses with a status code equal to 302
     */
    private Long count302;

    /**
     * • count304
     *
     * Number of responses with a status code equal to 304
     */
    private Long count304;

    /**
     * • count3xx
     *
     * Number of responses with a status code in the 3xx range
     */
    private Long count3xx;

    /**
     * • count400
     *
     * Number of responses with a status code equal to 400
     */
    private Long count400;

    /**
     * • count401
     *
     * Number of responses with a status code equal to 401
     */
    private Long count401;

    /**
     * • count403
     *
     * Number of responses with a status code equal to 403
     */
    private Long count403;

    /**
     * • count404
     *
     * Number of responses with a status code equal to 404
     */
    private Long count404;

    /**
     * • count4xx
     *
     * Number of responses with a status code in the 4xx range
     */
    private Long count4xx;

    /**
     * • count503
     *
     * Number of responses with a status code equal to 503
     */
    private Long count503;

    /**
     * • count5xx
     *
     * Number of responses with a status code in the 5xx range
     */
    private Long count5xx;

    /**
     * • countbytesreceived
     *
     * The number of bytes received
     */
    private Long countBytesReceived;

    /**
     * • countbytestransmitted
     *
     * The number of bytes transmitted
     */
    private Long countBytesTransmitted;

    /**
     * • countopenconnections
     *
     * The number of open connections
     */
    private Long countOpenConnections;

    /**
     * • countother
     *
     * Number of responses with a status code outside the 2xx, 3xx, 4xx, and 5xx range
     */
    private Long countOther;

    /**
     * • countrequests
     *
     * The number of requests received
     */
    private Long countRequests;

    /**
     * • errorcount
     *
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     */
    private Long errorCount;

    /**
     * • maxopenconnections
     *
     * The maximum number of open connections
     */
    private Long maxOpenConnections;

    /**
     * • maxtime
     *
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    private Long maxTime;

    /**
     * • method
     *
     * The method of the last request serviced
     */
    private String method;

    /**
     * • processingtime
     *
     * Average request processing time
     */
    private Long processingTime;

    /**
     * • uri
     *
     * The URI of the last request serviced
     */
    private String uri;

}
