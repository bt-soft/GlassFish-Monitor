/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppServletStatistic.java
 *  Created: 2018.01.19. 19:18:15
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.app;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Table(name = "APP_SERVLET_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"appStatistic"})
@EqualsAndHashCode(callSuper = true, exclude = {"appStatistic"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class AppServletStatistic extends AppSnapshotBase {

    /**
     * A mérés melyik alkalmazás statisztikához tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_STAT_ID")
    @ColumnPosition(position = 20)
    private AppStatistic appStatistic;

    /**
     * Szervlet neve
     */
    @NotNull(message = "A servletName nem lehet null")
    @Size(min = 3, max = 255, message = "A servletName mező hossza {min} és {max} között lehet")
    @Column(name = "SERVLET_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 30)
    private String servletName;

    /**
     * • errorcount
     * <p>
     * Number of error responses (that is, responses with a status code greater than or equal to 400)
     */
    @Column(name = "ERROR_COUNT")
    @ColumnPosition(position = 30)
    private Long errorCount;

    /**
     * • maxtime
     * <p>
     * Maximum response time
     */
    @Column(name = "MAX_TIME")
    @ColumnPosition(position = 31)
    private Long maxTime;

    /**
     * • processingtime
     * <p>
     * ProcessingTime
     */
    @Column(name = "PROCESSING_TIME")
    @ColumnPosition(position = 32)
    private Long processingTime;

    /**
     * • requestcount
     * <p>
     * Number of requests processed
     */
    @Column(name = "REQUEST_COUNT")
    @ColumnPosition(position = 33)
    private Long requestCount;

    /**
     * • servicetime
     * <p>
     * Aggregate response time
     */
    @Column(name = "SERVICE_TIME")
    @ColumnPosition(position = 34)
    private Long ServiceTime;

}
