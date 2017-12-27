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
 * REST PATH: monitoring/domain/server/web/servlet
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_WEB_SERVLET", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
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
     * • servletprocessingtimes
     *
     * Cumulative Servlet processing times
     */
    @ColumnPosition(position = 21)
    private Long servletProcessingTimes;

    /**
     * • totalservletsloadedcount
     *
     * Total number of Servlets ever loaded
     */
    @ColumnPosition(position = 21)
    private Long totalServletsLoaded;

}
