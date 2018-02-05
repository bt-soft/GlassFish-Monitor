/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPool.java
 *  Created: 2018.01.27. 18:48:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.connpool;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.ModifiableEntityBase;
import hu.btsoft.gfmon.engine.model.entity.connpool.snapshot.ConnPoolStat;
import hu.btsoft.gfmon.engine.model.entity.server.Server;
import java.util.LinkedList;
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
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "CONNPOOL",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"SVR_ID", "POOL_NAME"})
)
@NamedQueries({
    @NamedQuery(name = "ConnPool.findByServerId", query = "SELECT c FROM ConnPool c WHERE c.server.id = :serverId ORDER BY c.poolName"), //
})
@Data
@ToString(callSuper = true, exclude = {"server", "jdbcResources", "connPoolStats", "joiners"})
@EqualsAndHashCode(callSuper = true, exclude = {"server", "active", "jdbcResources", "connPoolStats", "joiners"}) //az 'active' nem számít bele az azonosságba!
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@Slf4j
public class ConnPool extends ModifiableEntityBase {

    /**
     * A ConnPool melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SVR_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private Server server;

    /**
     * A monitorozás aktív rá?
     */
    @Column(name = "ACTIVE", nullable = false)
    @ColumnPosition(position = 30)
    private Boolean active;

    /**
     * A JDBC ConnectionPool neve
     * pl.: connectionPool_gfmon
     */
    @NotNull(message = "A poolName nem lehet null")
    @Size(min = 5, max = 255, message = "A poolName mező hossza {min} és {max} között lehet")
    @Column(name = "POOL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 31)
    private String poolName;

    @Column(name = "DESCRIPTION")
    @ColumnPosition(position = 32)
    private String description;

    @Column(name = "DATASOURCE_CLASS_NAME")
    @ColumnPosition(position = 33)
    private String datasourceClassname;

    @Column(name = "IDDLE_TIMEOUT_IN_SECONDS")
    @ColumnPosition(position = 34)
    private String idleTimeoutInSeconds;

    @Column(name = "INIT_SQL")
    @ColumnPosition(position = 35)
    private String initSql;

    @Column(name = "MAC_CONNECTION_USAGE_COUNT")
    @ColumnPosition(position = 36)
    private String maxConnectionUsageCount;

    @Column(name = "MAX_POOL_SIZE")
    @ColumnPosition(position = 37)
    private String maxPoolSize;

    @Column(name = "MAX_WAIT_TIME_IN_MILLIS")
    @ColumnPosition(position = 38)
    private String maxWaitTimeInMillis;

    @Column(name = "POOL_RESIZE_QUANTITY")
    @ColumnPosition(position = 39)
    private String poolResizeQuantity;

    @Column(name = "POOLING")
    @ColumnPosition(position = 40)
    private boolean pooling;

    @Column(name = "RESTYPE")
    @ColumnPosition(position = 41)
    private String resType;

    @Column(name = "STATEMENT_CACHE_SIZE")
    @ColumnPosition(position = 42)
    private String statementCacheSize;

    @Column(name = "STEADY_POOL_SIZE")
    @ColumnPosition(position = 43)
    private String steadyPoolSize;

    @Column(name = "STATEMENT_TIMEOUT_IN_SECONDS")
    @ColumnPosition(position = 44)
    private String statementTimeoutInSeconds;

//
    /**
     * A JDBC Connection Pool mérendő adatai
     * - eager: mindig kell -> mindig felolvassuk
     * - cascade: update, merge menjen rájuk is, ha a szervert töröljük, akkor törlődjönenek az alkalmazások is
     * - orphanRemoval: izomból törlés lesz
     */
    @OneToMany(mappedBy = "connPool", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnPoolConnPoolCollDataUnitJoiner> joiners = new LinkedList<>();

    /**
     * Milyen jdbcResources használja?
     */
    @OneToMany(mappedBy = "connPool", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 70)
    private List<JdbcResource> jdbcResources = new LinkedList<>();

    /**
     * Milyen statisztikái vannak?
     */
    @OneToMany(mappedBy = "connPool", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ColumnPosition(position = 71)
    private List<ConnPoolStat> connPoolStats = new LinkedList<>();
}
