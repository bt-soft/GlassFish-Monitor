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
package hu.btsoft.gfmon.engine.model.entity.snapshot.httpservice;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.ColumnPosition;
import hu.btsoft.gfmon.engine.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

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
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpServiceRequest extends SnapshotBase {

    /**
     * • count200
     *
     * Number of responses with a status code equal to 200
     *
     */
    @ColumnPosition(position = 20)
    private Long count200;

    /**
     * • count2xx
     *
     * Number of responses with a status code in the 2xx range
     */
    @ColumnPosition(position = 21)
    private Long count2xx;

    /**
     * • count302
     *
     * Number of responses with a status code equal to 302
     */
    @ColumnPosition(position = 22)
    private Long count302;

    /**
     * • count304
     *
     * Number of responses with a status code equal to 304
     */
    @ColumnPosition(position = 23)
    private Long count304;

    /**
     * • count3xx
     *
     * Number of responses with a status code in the 3xx range
     */
    @ColumnPosition(position = 24)
    private Long count3xx;

    /**
     * • count400
     *
     * Number of responses with a status code equal to 400
     */
    @ColumnPosition(position = 25)
    private Long count400;

    /**
     * • count401
     *
     * Number of responses with a status code equal to 401
     */
    @ColumnPosition(position = 26)
    private Long count401;

    /**
     * • count403
     *
     * Number of responses with a status code equal to 403
     */
    @ColumnPosition(position = 27)
    private Long count403;

    /**
     * • count404
     *
     * Number of responses with a status code equal to 404
     */
    @ColumnPosition(position = 28)
    private Long count404;

    /**
     * • count4xx
     *
     * Number of responses with a status code in the 4xx range
     */
    @ColumnPosition(position = 29)
    private Long count4xx;

    /**
     * • count503
     *
     * Number of responses with a status code equal to 503
     */
    @ColumnPosition(position = 30)
    private Long count503;

    /**
     * • count5xx
     *
     * Number of responses with a status code in the 5xx range
     */
    @ColumnPosition(position = 31)
    private Long count5xx;

    /**
     * • countbytesreceived
     *
     * The number of bytes received
     */
    @ColumnPosition(position = 32)
    private Long countBytesReceived;

    /**
     * • countbytestransmitted
     *
     * The number of bytes transmitted
     */
    @ColumnPosition(position = 33)
    private Long countBytesTransmitted;

    /**
     * • countopenconnections
     *
     * The number of open connections
     */
    @ColumnPosition(position = 34)
    private Long countOpenConnections;

    /**
     * • countother
     *
     * Number of responses with a status code outside the 2xx, 3xx, 4xx, and 5xx range
     */
    @ColumnPosition(position = 35)
    private Long countOther;

    /**
     * • countrequests
     *
     * The number of requests received
     */
    @ColumnPosition(position = 36)
    private Long countRequests;

    /**
     * • errorcount
     *
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     */
    @ColumnPosition(position = 37)
    private Long errorCount;

    /**
     * • maxopenconnections
     *
     * The maximum number of open connections
     */
    @ColumnPosition(position = 38)
    private Long maxOpenConnections;

    /**
     * • maxtime
     *
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    @ColumnPosition(position = 39)
    private Long maxTime;

    /**
     * • method
     *
     * The method of the last request serviced
     */
    @ColumnPosition(position = 40)
    private String method;

    /**
     * • processingtime
     *
     * Average request processing time
     */
    @ColumnPosition(position = 41)
    private Long processingTime;

    /**
     * • uri
     *
     * The URI of the last request serviced
     */
    @ColumnPosition(position = 42)
    private String uri;

}
