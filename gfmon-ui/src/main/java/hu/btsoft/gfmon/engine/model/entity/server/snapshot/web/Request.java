/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Request.java
 *  Created: 2017.12.27. 14:45:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot.web;

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
 * REST PATH: monitoring/domain/server/web/request
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_WEB_REQUEST", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Request extends SnapshotBase {

    /**
     * • errorcount
     * <p>
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     */
    @Column(name = "ERROR_COUNT")
    @ColumnPosition(position = 30)
    private Long errorCount;

    /**
     * • maxtime
     * <p>
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    @Column(name = "MAX_TIME")
    @ColumnPosition(position = 31)
    private Long maxTime;

    /**
     * • processingtime
     * <p>
     * Average request processing time
     */
    @Column(name = "PROCESSING_TIME")
    @ColumnPosition(position = 32)
    private Long processingTime;

    /**
     * • requestcount
     * <p>
     * Cumulative number of requests processed so far
     */
    @Column(name = "REQUEST_COUNT")
    @ColumnPosition(position = 33)
    private Long requestCount;

}
