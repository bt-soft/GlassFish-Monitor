/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Jsp.java
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
 * REST PATH: monitoring/domain/server/web/jsp
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_WEB_JSP", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Jsp extends SnapshotBase {

    /**
     * • jspcount
     * <p>
     * Number of active JSP pages
     */
    @Column(name = "JSP_COUNT")
    @ColumnPosition(position = 30)
    private Long jspCount;

    /**
     * • jspcount - low watermark
     */
    @Column(name = "JSP_COUNT_LW")
    @ColumnPosition(position = 31)
    private Long jspCountLw;

    /**
     * • jspcount - low watermark
     */
    @Column(name = "JSP_COUNT_HW")
    @ColumnPosition(position = 32)
    private Long jspCountHw;

    /**
     * • jsperrorcount
     * <p>
     * Total number of errors triggered by JSP page invocations
     */
    @Column(name = "JSP_ERROR_COUNT")
    @ColumnPosition(position = 33)
    private Long jspErrorCount;

    /**
     * • jspreloadedcount
     * <p>
     * Total number of JSP pages that were reloaded
     */
    @Column(name = "JSP_RELOADED_COUNT")
    @ColumnPosition(position = 34)
    private Long jspReloadedCount;

    /**
     * • totaljspcount
     * <p>
     * Total number of JSP pages ever loaded
     */
    @Column(name = "TOTAL_JSP_COUNT")
    @ColumnPosition(position = 35)
    private Long totalJspCount;

}
