/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPool.java
 *  Created: 2018.01.27. 18:48:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.jdbc;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.ModifiableEntityBase;
import hu.btsoft.gfmon.engine.model.entity.jdbc.snapshot.ConnectionPoolStatistic;
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
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "JDBC_CONNECTION_POOL",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"POOL_NAME"})
)
@NamedQueries({
    @NamedQuery(name = "JdbcConnectionPool.findByServerId", query = "SELECT c FROM JdbcConnectionPool c WHERE c.server.id = :serverId ORDER BY c.poolName"), //
})
@Data
@ToString(callSuper = true, exclude = {"server", "connectionPoolStatistic"})
@EqualsAndHashCode(callSuper = true, exclude = {"server", "connectionPoolStatistic"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@Slf4j
public class JdbcConnectionPool extends ModifiableEntityBase {

    /**
     * A JdbcConnectionPool melyik szerveren van?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SVR_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 10)
    private Server server;

    /**
     * A JDBC ConnectionPool neve
     * pl.: connectionPool_gfmon
     */
    @NotNull(message = "A poolName nem lehet null")
    @Size(min = 5, max = 255, message = "A poolName mező hossza {min} és {max} között lehet")
    @Column(name = "POOL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 11)
    private String poolName;

    @ColumnPosition(position = 12)
    private String description;

    @ColumnPosition(position = 13)
    private String datasourceClassname;

    @ColumnPosition(position = 14)
    private String maxPoolSize;

    @ColumnPosition(position = 15)
    private String steadyPoolSize;

    @ColumnPosition(position = 16)
    private String poolResizeQuantity;

    //-- Milyen statisztikái vannak?
    @OneToMany(mappedBy = "jdbcConnectionPool", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "JDBC_CONPOOL_STAT_ID", referencedColumnName = "ID", nullable = false)
    private List<ConnectionPoolStatistic> connectionPoolStatistic;

}
