/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcResource.java
 *  Created: 2018.01.27. 19:20:10
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.connpool;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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
/**
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "JDBC_RESOURCE",
        catalog = "",
        schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        uniqueConstraints = @UniqueConstraint(columnNames = {"CONNPOOL_ID", "JNDI_NAME"})
)
@Data
@ToString(callSuper = true, exclude = {"connPool"})
@EqualsAndHashCode(callSuper = true, exclude = {"connPool"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@Slf4j
public class JdbcResource extends EntityBase {

    /**
     * A JDBC resource melyik connectio Pool-t használja?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONNPOOL_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private ConnPool connPool;

    /**
     * A JDBC ConnectionPool neve
     * pl.: connectionPool_gfmon
     */
    @NotNull(message = "A jndiName nem lehet null")
    @Size(min = 5, max = 255, message = "A jndiName mező hossza {min} és {max} között lehet")
    @Column(name = "JNDI_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 31)
    private String jndiName;

    /**
     * Leírás
     */
    @Column(name = "DESCRIPTION")
    @ColumnPosition(position = 32)
    private String description;

    /**
     * Engedélyezett?
     */
    @Column(name = "ENABLED")
    @ColumnPosition(position = 33)
    private boolean enabled;

    //ezt a jdbcConnectionPool hordozza, csak a kigyűjtéskor használjuk
    @Transient
    private String poolName;
}
