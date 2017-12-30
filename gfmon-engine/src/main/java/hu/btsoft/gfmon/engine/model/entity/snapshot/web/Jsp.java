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
package hu.btsoft.gfmon.engine.model.entity.snapshot.web;

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
 * REST PATH: monitoring/domain/server/web/jsp
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_WEB_JSP", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Jsp extends SnapshotBase {

    /**
     * • jspcount
     *
     * Number of active JSP pages
     */
    @ColumnPosition(position = 20)
    private Long jspCount;

    /**
     * • jspcount - low watermark
     */
    @ColumnPosition(position = 21)
    private Long jspCountLw;

    /**
     * • jspcount - low watermark
     */
    @ColumnPosition(position = 22)
    private Long jspCountHw;

    /**
     * • jsperrorcount
     *
     * Total number of errors triggered by JSP page invocations
     */
    @ColumnPosition(position = 23)
    private Long jspErrorCount;

    /**
     * • jspreloadedcount
     *
     * Total number of JSP pages that were reloaded
     */
    @ColumnPosition(position = 24)
    private Long jspReloadedCount;

    /**
     * • totaljspcount
     *
     * Total number of JSP pages ever loaded
     */
    @ColumnPosition(position = 25)
    private Long totalJspCount;

}
