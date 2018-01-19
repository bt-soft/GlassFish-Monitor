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
package hu.btsoft.gfmon.engine.model.entity.snapshot.server.web;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
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
 * REST PATH: monitoring/domain/server/web/servlet
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_WEB_SERVLET", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Servlet extends SnapshotBase {

    /**
     * • activeservletsloadedcount
     *
     * Number of Servlets loaded
     */
    @ColumnPosition(position = 20)
    private Long activeServletsLoaded;

    /**
     * LowWatermark
     */
    @ColumnPosition(position = 21)
    private Long activeServletsLoadedLw;

    /**
     * HighWatermark
     */
    @ColumnPosition(position = 22)
    private Long activeServletsLoadedHw;

    /**
     * • servletprocessingtimes
     *
     * Cumulative Servlet processing times
     */
    @ColumnPosition(position = 23)
    private Long servletProcessingTimes;

    /**
     * • totalservletsloadedcount
     *
     * Total number of Servlets ever loaded
     */
    @ColumnPosition(position = 24)
    private Long totalServletsLoaded;

}
