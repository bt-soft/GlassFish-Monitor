/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationServerChild.java
 *  Created: 2018.01.19. 19:18:15
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 *
 * pl.:
 * http://localhost:4848/monitoring/domain/server/applications/{appname}/server/(jsp|defaul|server|FacesServlet|ThemeServlet)
 *
 * @author BT
 */
@Entity
@Table(name = "APP_SERVER_CHILD", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ApplicationServerChild extends EntityBase {

    /**
     * A gyerek mérés neve
     *
     * DownloadServlet
     * FacesServlet
     * ThemeServlet
     * default
     * jsp
     */
    @NotNull(message = "Az appServerChildName nem lehet null")
    @Size(min = 5, max = 50, message = "Az appServerChildName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_SERVER_CHILD_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 21)
    private String appServerChildName;

    /**
     * • errorcount
     *
     * Number of error responses (that is, responses with a status code greater than or equal to 400)
     */
    @ColumnPosition(position = 21)
    private Long errorCount;

    /**
     * • maxtime
     *
     * Maximum response time
     */
    @ColumnPosition(position = 22)
    private Long maxTime;

    /**
     * • processingtime
     *
     * ProcessingTime
     */
    @ColumnPosition(position = 23)
    private Long processingTime;

    /**
     * • requestcount
     *
     * Number of requests processed
     */
    @ColumnPosition(position = 24)
    private Long requestCount;

    /**
     * • servicetime
     *
     * Aggregate response time
     */
    @ColumnPosition(position = 25)
    private Long ServiceTime;

    /**
     * Ez a gyerek mérés melyik Application Server méréshez tartozik?
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "APP_SERVER_ID", referencedColumnName = "ID", nullable = false)
    private ApplicationServer applicationServer;
}
