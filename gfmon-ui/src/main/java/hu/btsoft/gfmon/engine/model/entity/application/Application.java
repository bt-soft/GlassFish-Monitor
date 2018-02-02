/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    Application.java
 *  Created: 2018.01.19. 18:23:49
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.engine.model.entity.ModifiableEntityBase;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.app.AppStatistic;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb.EjbStat;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnectionPoolAppStatistic;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "APPLICATION",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"SVR_ID", "SHORT_NAME", "REAL_NAME", "MODULE_REAL_NAME", "MODULE_SHORT_NAME"})
)
@NamedQueries({
    @NamedQuery(name = "Application.findByServerId", query = "SELECT a FROM Application a WHERE a.server.id = :serverId ORDER BY a.appRealName, a.moduleRealName"), //
    @NamedQuery(name = "Application.findByServerIdAndAppShortName", query = "SELECT a FROM Application a WHERE a.server.id = :serverId AND a.appShortName = :appShortName"), //
})
@Data
@ToString(callSuper = true, exclude = {"server", "ejbStats", "appStatistics"})

@EqualsAndHashCode(callSuper = true, of = {"appShortName", "appRealName", "moduleShortName", "moduleRealName", "enabled", "contextRoot", "description", "active"})
@NoArgsConstructor
@Slf4j
public class Application extends ModifiableEntityBase {

    /**
     * Az alkalmazás melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SVR_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private Server server;

    /**
     * Az alkalmazás rövid neve
     * pl.: TestEar
     * (verziószám és classifier nélkül)
     */
    @NotNull(message = "Az appShortName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appShortName mező hossza {min} és {max} között lehet")
    @Column(name = "SHORT_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 30)
    private String appShortName;

    /**
     * Az alkalmazás valódi neve
     * pl.: TestEar-0.0.1-dev
     * /management/domain/applications/application/
     */
    @NotNull(message = "Az appRealName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appRealName mező hossza {min} és {max} között lehet")
    @Column(name = "REAL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 31)
    private String appRealName;

    /**
     * Az alkalmazás almoduljának valódi neve
     * pl.: TestEar-ejb-0.0.3.jar, TestEar-web-0.0.3.war
     * /management/domain/applications/application/TestEar/module
     */
    @NotNull(message = "A moduleRealName nem lehet null")
    @Size(min = 1, max = 255, message = "A moduleRealName mező hossza {min} és {max} között lehet")
    @Column(name = "MODULE_REAL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 32)
    private String moduleRealName;

    /**
     * Az alkalmazás almoduljának rövid neve
     * (verziószám és classifier nélkül)
     * pl.: TestEar-ejb, TestEar-web
     * /management/domain/applications/application/TestEar/module
     */
    @NotNull(message = "A moduleShortName nem lehet null")
    @Size(min = 1, max = 255, message = "A moduleShortName mező hossza {min} és {max} között lehet")
    @Column(name = "MODULE_SHORT_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 33)
    private String moduleShortName;

    /**
     * Az alkalmazás almoduljának motorjainak listája,
     * pl.: ejb,jpa,security,web,weld
     * /management/domain/applications/application/TestEar/module/TestEar-web-0.0.3.war/engine
     * Egy EJB modulnak nincs WEB motorja!
     */
    @ElementCollection
    @MapKeyColumn(name = "APPLICATION_ID")
    @Column(name = "MODULE_ENGINE_NAME", length = 512)
    @CollectionTable(name = "APPLICATION_MODULE_ENGINE", joinColumns = @JoinColumn(name = "APPLICATION_ID"))
    @ColumnPosition(position = 34)
    private Set<String> moduleEngines;

    /**
     * A monitorozás aktív erre az alkalmazásra?
     */
    @Column(name = "ACTIVE", nullable = false)
    @ColumnPosition(position = 35)
    private Boolean active;

    //---- Glassfish descriptoból jövő adatok /management/domain/applications/application/{appRealName}
    /**
     * Az alkalmazás engedélyezett?
     */
    @Column(name = "ENABLED", nullable = false)
    @ColumnPosition(position = 40)
    private boolean enabled;

    /**
     * Context Root
     */
    @Column(name = "CONTEXT_ROOT", length = 128)
    @ColumnPosition(position = 41)
    private String contextRoot;

    /**
     * Az alkalmazás leírása
     */
    @Column(name = "DESCRIPTION", length = 512)
    @ColumnPosition(position = 42)
    private String description;
//
//
//
    //-- Alkalmazás statisztika Mérési eredmények
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_STAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 70)
    private List<AppStatistic> appStatistics = new LinkedList<>();

    //-- EJB statisztika eredmények
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 71)
    private List<EjbStat> ejbStats = new LinkedList<>();

    //-- ConnectionPool usage statisztika mérése eredmények
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "JDBC_CONECTION_POOL_APP_STAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 72)
    private List<ConnectionPoolAppStatistic> connectionPoolAppStatistics = new LinkedList<>();

    /**
     * Konstruktor
     *
     * @param appRealName alkalmazás tényleges neve
     * @param active      monitorzásra aktív?
     * @param server      melyik szerveren van?
     */
    public Application(String appRealName, boolean active, Server server) {
        this.appRealName = appRealName;
        this.active = active;
        this.server = server;
    }

    /**
     * Konstruktor
     *
     * @param appShortName alkalmazás rövid neve (verziószám és classifier nélkül)
     * @param appRealName  alkalmazás tényleges neve
     * @param active       monitorzásra aktív?
     * @param server       melyik szerveren van?
     */
    public Application(String appShortName, String appRealName, boolean active, Server server) {
        this.appShortName = appShortName;
        this.appRealName = appRealName;
        this.active = active;
        this.server = server;
    }

    //----------------------
    /**
     * Rövid név képzése
     *
     * @param _appRealName az alkalmazás hosszú neve
     *
     * @return az alklamzás verzió és classifier nélküli neve
     */
    public static String createAppShortName(String _appRealName) {

        //Képezzük a rövid nevet
//                Pattern pattern = Pattern.compile(server.getAppRegExpFilter());
//                String appShortName = "";
//                Matc_her matcher = pattern.matcher(appRealName);
//                if (matcher.matches() && matcher.groupCount() >= 1) {
//                    appShortName = matcher.group(1);
//                }
//
//TODO: majd ha tudom képezni vhogy :(
//
        return _appRealName;
    }
}
