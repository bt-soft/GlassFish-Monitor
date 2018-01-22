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
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.server.ApplicationServer;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
        uniqueConstraints = @UniqueConstraint(columnNames = {"APP_SHORT_NAME", "APP_REAL_NAME"})
)
@NamedQueries({
    @NamedQuery(name = "Application.findByServerId", query = "SELECT a FROM Application a WHERE a.server.id = :serverId ORDER BY a.appRealName"), //
    @NamedQuery(name = "Application.findByServerIdAndAppShortName", query = "SELECT a FROM Application a WHERE a.server.id = :serverId AND a.appShortName = :appShortName"), //
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, of = {"appShortName", "appRealName", "active"})
@NoArgsConstructor
@Slf4j
public class Application extends ModifiableEntityBase {

    /**
     * Az alkalmazás rövid neve
     * (verziószám és classifier nélkül)
     */
    @NotNull(message = "Az appShortName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appShortName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_SHORT_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 11)
    private String appShortName;

    /**
     * Az alkalmazás valódi neve pl.: gf-mon-0.0.1-dev
     */
    @NotNull(message = "Az appRealName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appRealName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_REAL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 12)
    private String appRealName;

    /**
     * A monitorozás aktív erre az alkalmazásra?
     */
    @Column(nullable = false)
    @ColumnPosition(position = 13)
    private Boolean active;

    /**
     * Az alkalmazás melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SVR_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 30)
    private Server server;

    //---- Glassfish descriptoból jövő adatok /management/domain/applications/application/{appRealName}
    /**
     * Az alkalmatzás engedélyezett?
     */
    @Column(nullable = false)
    @ColumnPosition(position = 15)
    private boolean enabled;

    /**
     * Context Root
     */
    @Column(length = 128)
    @ColumnPosition(position = 16)
    private String contextRoot;

    /**
     * Az alkalmazás leírása
     */
    @Column(length = 256)
    @ColumnPosition(position = 17)
    private String description;

    //-- Mérési eredmények
    @OneToMany(mappedBy = "application", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationServer> applicationServers;

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
//                Pattern pattern = Pattern.compile(server.getRegExpFilter());
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
