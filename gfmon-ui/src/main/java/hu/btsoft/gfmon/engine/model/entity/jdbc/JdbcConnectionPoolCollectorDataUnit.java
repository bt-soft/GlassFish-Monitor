/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    JdbcConnectionPoolCollectorDataUnit.java
 *  Created: 2018.01.19. 17:22:20
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.jdbc;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(true)
@Table(name = "JDBC_CONNPOOL_CDU", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        indexes = {
            @Index(name = "IDX_JDBC_CONPOOL_CDU_REST_PATH_MASK", columnList = "REST_PATH_MASK", unique = false),
            @Index(name = "IDX_JDBC_CONPOOL_CDU_ENTITY_NAME", columnList = "ENTITY_NAME", unique = false)
        })
@NamedQueries({
    @NamedQuery(name = "JdbcConnectionPoolCollectorDataUnit.findAll", query = "SELECT jdbcCdu from JdbcConnectionPoolCollectorDataUnit jdbcCdu ORDER BY jdbcCdu.restPathMask, jdbcCdu.dataName"),//
    @NamedQuery(name = "JdbcConnectionPoolCollectorDataUnit.findAllRestPathMasks", query = "SELECT jdbcCdu.restPathMask from JdbcConnectionPoolCollectorDataUnit jdbcCdu GROUP BY jdbcCdu.restPathMask ORDER BY jdbcCdu.restPathMask, jdbcCdu.dataName"),//
    @NamedQuery(name = "JdbcConnectionPoolCollectorDataUnit.findByRestPathMask", query = "SELECT jdbcCdu from JdbcConnectionPoolCollectorDataUnit jdbcCdu WHERE jdbcCdu.restPathMask = :restPathMask ORDER BY jdbcCdu.dataName"),//
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class JdbcConnectionPoolCollectorDataUnit extends EntityBase {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    @ColumnPosition(position = 30)
    @NotNull(message = "A restPathMask nem lehet null")
    @Column(name = "REST_PATH_MASK", length = 1024, nullable = false)
    private String restPathMask;

    /**
     * Milyen entitás használja?
     */
    @ColumnPosition(position = 31)
    @NotNull(message = "Az entityName nem lehet null")
    @Column(name = "ENTITY_NAME", length = 50, nullable = false)
    private String entityName;

    /**
     * Az adat megnevezése
     */
    @ColumnPosition(position = 32)
    @NotNull(message = "A name nem lehet null")
    @Column(name = "DATA_NAME", length = 50, nullable = false)
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    @ColumnPosition(position = 33)
    @NotNull(message = "Az unit nem lehet null")
    @Column(name = "UNIT", length = 15, nullable = false)
    private String unit;

    /**
     * Az adat leírása
     */
    @ColumnPosition(position = 34)
    @NotNull(message = "A description nem lehet null")
    @Column(name = "DESCRIPTION", length = 512, nullable = false)
    private String description;

    /**
     * Konstruktor
     *
     * @param restPathMask REST path maszk
     * @param entityName   entitás neve
     * @param dataName     adatnév
     * @param unit         mértékegység
     * @param description  leírás
     */
    public JdbcConnectionPoolCollectorDataUnit(String restPathMask, String entityName, String dataName, String unit, String description) {
        this.restPathMask = restPathMask;
        this.entityName = entityName;
        this.dataName = dataName;
        this.unit = unit;
        this.description = description;
    }

}
