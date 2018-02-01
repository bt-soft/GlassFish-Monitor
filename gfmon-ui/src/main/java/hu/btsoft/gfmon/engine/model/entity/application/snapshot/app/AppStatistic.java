/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppStatistic.java
 *  Created: 2018.01.19. 19:03:17
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.app;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Alkalmazás szerver adatai
 * <p>
 * pl.:
 * http://localhost:4848/monitoring/domain/server/applications/{appname}/server
 *
 * @author BT
 */
@Entity
@Table(name = "APP_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"application", "appServletStatistics"})
@EqualsAndHashCode(callSuper = true, exclude = {"application", "appServletStatistics"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class AppStatistic extends AppSnapshotBase {

    /**
     * A mérés melyik alkalmazáshoz tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID")
    @ColumnPosition(position = 20)
    private Application application;

    /**
     * • maxtime
     * <p>
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     * <p>
     */
    @Column(name = "MAX_TIME")
    @ColumnPosition(position = 30)
    private Long maxTime;

    /**
     * • processingtime
     * <p>
     * Average request processing time
     * <p>
     */
    @Column(name = "PROCESSING_TIME")
    @ColumnPosition(position = 31)
    private Long processingTime;

    /**
     * • requestcount
     * <p>
     * Cumulative number of requests processed so far
     * <p>
     */
    @Column(name = "REQUEST_COUNT")
    @ColumnPosition(position = 32)
    private Long requestCount;

    /**
     * • errorcount
     * <p>
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     * <p>
     */
    @Column(name = "ERROR_COUNT")
    @ColumnPosition(position = 33)
    private Long errorCount;

    /**
     * • ActivatedSessionsTotal
     * <p>
     * Total number of sessions ever activated
     * <p>
     */
    @Column(name = "ACTIVATED_SESSIONS_TOTAL")
    @ColumnPosition(position = 34)
    private Long activatedSessionsTotal;

    /**
     * • sessionstotal
     * <p>
     * Total number of sessions ever created
     * <p>
     */
    @Column(name = "SESSIONS_TOTAL")
    @ColumnPosition(position = 35)
    private Long sessionsTotal;

    /**
     * • activesessionscurrent
     * <p>
     * Number of active sessions
     * <p>
     */
    @Column(name = "ACTIVE_SESSIONS")
    @ColumnPosition(position = 36)
    private Long activeSessions;

    @Column(name = "ACTIVE_SESSIONS_LW")
    @ColumnPosition(position = 37)
    private Long activeSessionsLw;

    @Column(name = "ACTIVE_SESSIONS_HW")
    @ColumnPosition(position = 38)
    private Long activeSessionsHw;

    /**
     * • passivatedsessionstotal
     * <p>
     * Total number of sessions ever passivated
     * <p>
     */
    @Column(name = "PASSIVATED_SESSIONS_TOTAL")
    @ColumnPosition(position = 39)
    private Long passivatedSessionsTotal;

    /**
     * • expiredsessionstotal
     * <p>
     * Total number of sessions ever expired
     * <p>
     */
    @Column(name = "EXPIRED_SESSIONS_TOTAL")
    @ColumnPosition(position = 40)
    private Long expiredSessionsTotal;

    /**
     * • rejectedsessionstotal
     * <p>
     * Total number of sessions ever rejected
     * <p>
     */
    @Column(name = "REJECTED_SESSIONS_TOTAL")
    @ColumnPosition(position = 41)
    private Long rejectedSessionsTotal;

    /**
     * • jspcount
     * <p>
     * Number of active JSP pages
     * <p>
     */
    @Column(name = "JSP_COUNT")
    @ColumnPosition(position = 42)
    private Long jspCount;

    @Column(name = "JSP_COUNT_LW")
    @ColumnPosition(position = 43)
    private Long jspCountLw;

    @Column(name = "JSP_COUNT_HW")
    @ColumnPosition(position = 44)
    private Long jspCountHw;

    /**
     * • jsperrorcount
     * <p>
     * Total number of errors triggered by JSP page invocations
     * <p>
     */
    @Column(name = "JSP_ERROR_COUNT")
    @ColumnPosition(position = 45)
    private Long jspErrorCount;

    /**
     * • jspreloadedcount
     * <p>
     * Total number of JSP pages that were reloaded
     * <p>
     */
    @Column(name = "JSP_RELOADED_COUNT")
    @ColumnPosition(position = 46)
    private Long jspReloadedCount;

    /**
     * • totaljspcount
     * <p>
     * Total number of JSP pages ever loaded
     * <p>
     */
    @Column(name = "TOTAL_JSP_COUNT")
    @ColumnPosition(position = 47)
    private Long totalJspCount;

    /**
     * • activeservletsloadedcount
     * <p>
     * Number of Servlets loaded
     * <p>
     */
    @Column(name = "ACTIVE_SERVLETS_LOADED")
    @ColumnPosition(position = 48)
    private Long activeServletsLoaded;

    @Column(name = "ACTIVE_SERVLETS_LOADED_LW")
    @ColumnPosition(position = 49)
    private Long activeServletsLoadedLw;

    @Column(name = "ACTIVE_SERVLETS_LOADED_HW")
    @ColumnPosition(position = 50)
    private Long activeServletsLoadedHw;

    /**
     * • servletprocessingtimes
     * <p>
     * Cumulative Servlet processing times
     * <p>
     */
    @Column(name = "SERVLET_PROCESSING_TIMES")
    @ColumnPosition(position = 51)
    private Long servletProcessingTimes;

    /**
     * • totalservletsloadedcount
     * <p>
     * Total number of Servlets ever loaded
     * <p>
     */
    @Column(name = "TOTAL_SERVLETS_LOADED")
    @ColumnPosition(position = 52)
    private Long totalServletsLoaded;

//
//
    //-- Servlet mérési eredmények
    @OneToMany(mappedBy = "appStatistic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_SERVLET_STAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 70)
    private List<AppServletStatistic> appServletStatistics = new LinkedList<>();

}
