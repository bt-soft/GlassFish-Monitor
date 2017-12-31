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
package hu.btsoft.gfmon.corelib.model.entity.snapshot.web;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.entity.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.corelib.model.entity.snapshot.SnapshotBase;
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
 * REST PATH: monitoring/domain/server/web/request
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_WEB_REQUEST", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Request extends SnapshotBase {

    /**
     * • errorcount
     *
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     */
    @ColumnPosition(position = 20)
    private Long errorCount;

    /**
     * • maxtime
     *
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     */
    @ColumnPosition(position = 21)
    private Long maxTime;

    /**
     * • processingtime
     *
     * Average request processing time
     */
    @ColumnPosition(position = 22)
    private Long processingTime;

    /**
     * • requestcount
     *
     * Cumulative number of requests processed so far
     */
    @ColumnPosition(position = 23)
    private Long requestCount;

}
