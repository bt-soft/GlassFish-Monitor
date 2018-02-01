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
package hu.btsoft.gfmon.engine.model.entity.server.snapshot.httpservice;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "SVR_HTTP_SERVICE_REQUEST", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpServiceRequest extends SnapshotBase {

    /**
     * • count200
     * <p>
     * Number of responses with a status code equal to 200
     * <p>
     */
    @Column(name = "COUNT_200")
    @ColumnPosition(position = 30)
    private Long count200;

    /**
     * • count2xx
     * <p>
     * Number of responses with a status code in the 2xx range
     */
    @Column(name = "COUNT_2XX")
    @ColumnPosition(position = 31)
    private Long count2xx;

    /**
     * • count302
     * <p>
     * Number of responses with a status code equal to 302
     */
    @Column(name = "COUNT_302")
    @ColumnPosition(position = 32)
    private Long count302;

    /**
     * • count304
     * <p>
     * Number of responses with a status code equal to 304
     */
    @Column(name = "COUNT_304")
    @ColumnPosition(position = 33)
    private Long count304;

    /**
     * • count3xx
     * <p>
     * Number of responses with a status code in the 3xx range
     */
    @Column(name = "COUNT_300")
    @ColumnPosition(position = 34)
    private Long count3xx;

    /**
     * • count400
     * <p>
     * Number of responses with a status code equal to 400
     */
    @Column(name = "COUNT_400")
    @ColumnPosition(position = 35)
    private Long count400;

    /**
     * • count401
     * <p>
     * Number of responses with a status code equal to 401
     */
    @Column(name = "COUNT_401")
    @ColumnPosition(position = 36)
    private Long count401;

    /**
     * • count403
     * <p>
     * Number of responses with a status code equal to 403
     */
    @Column(name = "COUNT_403")
    @ColumnPosition(position = 37)
    private Long count403;

    /**
     * • count404
     * <p>
     * Number of responses with a status code equal to 404
     */
    @Column(name = "COUNT_404")
    @ColumnPosition(position = 38)
    private Long count404;

    /**
     * • count4xx
     * <p>
     * Number of responses with a status code in the 4xx range
     */
    @Column(name = "COUNT_4xx")
    @ColumnPosition(position = 39)
    private Long count4xx;

    /**
     * • count503
     * <p>
     * Number of responses with a status code equal to 503
     */
    @Column(name = "COUNT_503")
    @ColumnPosition(position = 40)
    private Long count503;

    /**
     * • count5xx
     * <p>
     * Number of responses with a status code in the 5xx range
     */
    @Column(name = "COUNT_5xx")
    @ColumnPosition(position = 41)
    private Long count5xx;

    /**
     * • countbytesreceived
     * <p>
     * The number of bytes received
     */
    @Column(name = "COUNT_BYTES_RECEIVED")
    @ColumnPosition(position = 42)
    private Long countBytesReceived;

    /**
     * • countbytestransmitted
     * <p>
     * The number of bytes transmitted
     */
    @Column(name = "COUNT_BYTES_TRANSMITTED")
    @ColumnPosition(position = 43)
    private Long countBytesTransmitted;

    /**
     * • countopenconnections
     * <p>
     * The number of open connections
     */
    @Column(name = "COUNT_OPEN_CONNECTIONS")
    @ColumnPosition(position = 44)
    private Long countOpenConnections;

    /**
     * • countother
     * <p>
     * Number of responses with a status code outside the 2xx, 3xx, 4xx, and 5xx range
     */
    @Column(name = "COUNT_OTHER")
    @ColumnPosition(position = 45)
    private Long countOther;

    /**
     * • countrequests
     * <p>
     * The number of requests received
     */
    @Column(name = "COUNT_REQUESTS")
    @ColumnPosition(position = 46)
    private Long countRequests;

    /**
     * • errorcount
     * <p>
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     */
    @Column(name = "ERROR_COUNT")
    @ColumnPosition(position = 47)
    private Long errorCount;

    /**
     * • maxopenconnections
     * <p>
     * The maximum number of open connections
     */
    @Column(name = "MAX_OPEN_CONNECTIONS")
    @ColumnPosition(position = 48)
    private Long maxOpenConnections;

    /**
     * • maxtime
     * <p>
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    @Column(name = "MAX_TIME")
    @ColumnPosition(position = 49)
    private Long maxTime;

    /**
     * • method
     * <p>
     * The method of the last request serviced
     */
    @Column(name = "METHOD")
    @ColumnPosition(position = 50)
    private String method;

    /**
     * • processingtime
     * <p>
     * Average request processing time
     */
    @Column(name = "PROCESSING_TIME")
    @ColumnPosition(position = 51)
    private Long processingTime;

    /**
     * • uri
     * <p>
     * The URI of the last request serviced
     */
    @Column(name = "URI")
    @ColumnPosition(position = 52)
    private String uri;

}
