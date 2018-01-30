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
    @ColumnPosition(position = 10)
    private Application application;

    /**
     * • ActivatedSessionsTotal
     * <p>
     * Total number of sessions ever activated
     * <p>
     */
    @ColumnPosition(position = 20)
    private Long activatedSessionsTotal;

    /**
     * • activeservletsloadedcount
     * <p>
     * Number of Servlets loaded
     * <p>
     */
    @ColumnPosition(position = 21)
    private Long activeServletsLoaded;

    /**
     * • activesessionscurrent
     * <p>
     * Number of active sessions
     * <p>
     */
    @ColumnPosition(position = 22)
    private Long activeSessions;

    /**
     * • errorcount
     * <p>
     * Cumulative value of the error count, with error count representing the number of cases where the response code was greater than or equal to 400
     * <p>
     */
    @ColumnPosition(position = 23)
    private Long errorCount;

    /**
     * • expiredsessionstotal
     * <p>
     * Total number of sessions ever expired
     * <p>
     */
    @ColumnPosition(position = 24)
    private Long expiredSessionsTotal;

    /**
     * • jspcount
     * <p>
     * Number of active JSP pages
     * <p>
     */
    @ColumnPosition(position = 25)
    private Long jspCount;

    /**
     * • jsperrorcount
     * <p>
     * Total number of errors triggered by JSP page invocations
     * <p>
     */
    @ColumnPosition(position = 26)
    private Long jspErrorCount;

    /**
     * • jspreloadedcount
     * <p>
     * Total number of JSP pages that were reloaded
     * <p>
     */
    @ColumnPosition(position = 27)
    private Long jspReloadedCount;

    /**
     * • maxtime
     * <p>
     * Longest response time for a request; not a cumulative value, but the largest response time from among the response times
     * <p>
     */
    @ColumnPosition(position = 28)
    private Long maxTime;

    /**
     * • passivatedsessionstotal
     * <p>
     * Total number of sessions ever passivated
     * <p>
     */
    @ColumnPosition(position = 29)
    private Long passivatedSessionsTotal;

    /**
     * • processingtime
     * <p>
     * Average request processing time
     * <p>
     */
    @ColumnPosition(position = 30)
    private Long processingTime;

    /**
     * • rejectedsessionstotal
     * <p>
     * Total number of sessions ever rejected
     * <p>
     */
    @ColumnPosition(position = 31)
    private Long rejectedSessionsTotal;

    /**
     * • requestcount
     * <p>
     * Cumulative number of requests processed so far
     * <p>
     */
    @ColumnPosition(position = 32)
    private Long requestCount;

    /**
     * • servletprocessingtimes
     * <p>
     * Cumulative Servlet processing times
     * <p>
     */
    @ColumnPosition(position = 33)
    private Long servletProcessingTimes;

    /**
     * • sessionstotal
     * <p>
     * Total number of sessions ever created
     * <p>
     */
    @ColumnPosition(position = 34)
    private Long sessionsTotal;

    /**
     * • totaljspcount
     * <p>
     * Total number of JSP pages ever loaded
     * <p>
     */
    @ColumnPosition(position = 35)
    private Long totalJspCount;

    /**
     * • totalservletsloadedcount
     * <p>
     * Total number of Servlets ever loaded
     * <p>
     */
    @ColumnPosition(position = 36)
    private Long totalServletsLoaded;

    //-- Servlet mérési eredmények
    @OneToMany(mappedBy = "appStatistic", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_SERVLET_STAT_ID", referencedColumnName = "ID", nullable = false)
    private List<AppServletStatistic> appServletStatistics = new LinkedList<>();

}
