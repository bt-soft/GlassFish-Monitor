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
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = "Server.findAll", query = "SELECT s FROM Server s ORDER BY s.hostName, s.portNumber"), //
    @NamedQuery(name = "Server.findAllActive", query = "SELECT s FROM Server s WHERE s.active = true ORDER BY s.hostName, s.portNumber"), //
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "server")
@NoArgsConstructor
@Slf4j
public class Application extends ModifiableEntityBase {

    /**
     * Az alkalmazás rövid neve pl.: gf-mon
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
     * Az alkalmazás melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SVR_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 13)
    private Server server;
}
