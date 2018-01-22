/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationServerSubComponent.java
 *  Created: 2018.01.19. 19:18:15
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Alkalmazás szerver (jsp|defaul|server|FacesServlet|ThemeServlet) adatai
 * <p>
 * pl.:
 * http://localhost:4848/monitoring/domain/server/applications/{appname}/server/(jsp|defaul|server|FacesServlet|ThemeServlet)
 *
 * @author BT
 */
@Entity
@Table(name = "APP_SERVER_SUBCOMP", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ApplicationServerSubComponent extends AppSnapshotBase {

    /**
     * Az alkalmazás melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_SERVER_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 11)
    private ApplicationServer applicationServer;

    /**
     * • errorcount
     * <p>
     * Number of error responses (that is, responses with a status code greater than or equal to 400)
     */
    @ColumnPosition(position = 21)
    private Long errorCount;

    /**
     * • maxtime
     * <p>
     * Maximum response time
     */
    @ColumnPosition(position = 22)
    private Long maxTime;

    /**
     * • processingtime
     * <p>
     * ProcessingTime
     */
    @ColumnPosition(position = 23)
    private Long processingTime;

    /**
     * • requestcount
     * <p>
     * Number of requests processed
     */
    @ColumnPosition(position = 24)
    private Long requestCount;

    /**
     * • servicetime
     * <p>
     * Aggregate response time
     */
    @ColumnPosition(position = 25)
    private Long ServiceTime;

}
