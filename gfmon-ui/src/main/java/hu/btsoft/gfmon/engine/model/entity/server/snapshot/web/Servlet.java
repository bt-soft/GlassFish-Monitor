/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Servlet.java
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
 * REST PATH: monitoring/domain/server/web/servlet
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_WEB_SERVLET", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Servlet extends SnapshotBase {

    /**
     * • activeservletsloadedcount
     * <p>
     * Number of Servlets loaded
     */
    @Column(name = "ACTIVE_SERVLETS_LOADED")
    @ColumnPosition(position = 30)
    private Long activeServletsLoaded;

    /**
     * LowWatermark
     */
    @Column(name = "ACTIVE_SERVLETS_LOADED_LW")
    @ColumnPosition(position = 31)
    private Long activeServletsLoadedLw;

    /**
     * HighWatermark
     */
    @Column(name = "ACTIVE_SERVLETS_LOADED_HW")
    @ColumnPosition(position = 32)
    private Long activeServletsLoadedHw;

    /**
     * • servletprocessingtimes
     * <p>
     * Cumulative Servlet processing times
     */
    @Column(name = "SERVLET_PROCESSING_TIMES")
    @ColumnPosition(position = 33)
    private Long servletProcessingTimes;

    /**
     * • totalservletsloadedcount
     * <p>
     * Total number of Servlets ever loaded
     */
    @Column(name = "TOTAL_SERVLETS_LOADED")
    @ColumnPosition(position = 44)
    private Long totalServletsLoaded;

}
