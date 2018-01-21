/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationServer.java
 *  Created: 2018.01.19. 19:03:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Alkalmazás szerver adatai
 *
 * pl.:
 * http://localhost:4848/monitoring/domain/server/applications/{appname}/server
 *
 * @author BT
 */
@Entity
@Table(name = "APP_SERVER", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ApplicationServer extends AppSnapshotBase {

    /**
     * • ActivatedSessionsTotal
     *
     * Total number of sessions ever activated
     *
     */
    @ColumnPosition(position = 20)
    private Long activatedSessionsTotal;

    /**
     * • activeservletsloadedcount
     *
     * Number of Servlets loaded
     *
     */
    @ColumnPosition(position = 21)
    private Long activeServletsLoaded;

    /**
     * • activesessionscurrent
     *
     * Number of active sessions
     *
     */
    @ColumnPosition(position = 22)
    private Long activeSessions;

    /**
     * • errorcount
     *
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     *
     */
    @ColumnPosition(position = 23)
    private Long errorCount;

    /**
     * • expiredsessionstotal
     *
     * Total number of sessions ever expired
     *
     */
    @ColumnPosition(position = 24)
    private Long expiredSessionsTotal;

    /**
     * • jspcount
     *
     * Number of active JSP pages
     *
     */
    @ColumnPosition(position = 25)
    private Long jspCount;

    /**
     * • jsperrorcount
     *
     * Total number of errors triggered by JSP page invocations
     *
     */
    @ColumnPosition(position = 26)
    private Long jspErrorCount;

    /**
     * • jspreloadedcount
     *
     * Total number of JSP pages that were reloaded
     *
     */
    @ColumnPosition(position = 27)
    private Long jspReloadedCount;

    /**
     * • maxtime
     *
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     *
     */
    @ColumnPosition(position = 28)
    private Long maxTime;

    /**
     * • passivatedsessionstotal
     *
     * Total number of sessions ever passivated
     *
     */
    @ColumnPosition(position = 29)
    private Long passivatedSessionsTotal;

    /**
     * • processingtime
     *
     * Average request processing time
     *
     */
    @ColumnPosition(position = 30)
    private Long processingTime;

    /**
     * • rejectedsessionstotal
     *
     * Total number of sessions ever rejected
     *
     */
    @ColumnPosition(position = 31)
    private Long rejectedSessionsTotal;

    /**
     * • requestcount
     *
     * Cumulative number of requests processed so far
     *
     */
    @ColumnPosition(position = 32)
    private Long requestCount;

    /**
     * • servletprocessingtimes
     *
     * Cumulative Servlet processing times
     *
     */
    @ColumnPosition(position = 33)
    private Long servletProcessingTimes;

    /**
     * • sessionstotal
     *
     * Total number of sessions ever created
     *
     */
    @ColumnPosition(position = 34)
    private Long sessionsTotal;

    /**
     * • totaljspcount
     *
     * Total number of JSP pages ever loaded
     *
     */
    @ColumnPosition(position = 35)
    private Long totalJspCount;

    /**
     * • totalservletsloadedcount
     *
     * Total number of Servlets ever loaded
     *
     */
    @ColumnPosition(position = 36)
    private Long totalServletsLoaded;

    /**
     * Gyerek mérési eredmények
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "applicationServer", fetch = FetchType.LAZY)
    private List<ApplicationServerChild> childs;
}
