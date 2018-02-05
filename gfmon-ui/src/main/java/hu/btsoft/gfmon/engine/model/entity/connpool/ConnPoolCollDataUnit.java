/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolCollDataUnit.java
 *  Created: 2018.01.19. 17:22:20
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.connpool;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
@Table(name = "CONNPOOL_CDU", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        indexes = {
            @Index(name = "IDX_JDBC_CONPOOL_CDU_REST_PATH_MASK", columnList = "REST_PATH_MASK", unique = false),
            @Index(name = "IDX_JDBC_CONPOOL_CDU_ENTITY_NAME", columnList = "ENTITY_NAME", unique = false)
        })
@NamedQueries({
    @NamedQuery(name = "ConnPoolCollDataUnit.findAll", query = "SELECT cpCdu from ConnPoolCollDataUnit cpCdu ORDER BY cpCdu.restPathMask, cpCdu.dataName"),//
    @NamedQuery(name = "ConnPoolCollDataUnit.findAllRestPathMasks", query = "SELECT cpCdu.restPathMask from ConnPoolCollDataUnit cpCdu GROUP BY cpCdu.restPathMask ORDER BY cpCdu.restPathMask, cpCdu.dataName"),//
    @NamedQuery(name = "ConnPoolCollDataUnit.findByRestPathMask", query = "SELECT cpCdu from ConnPoolCollDataUnit cpCdu WHERE cpCdu.restPathMask = :restPathMask ORDER BY cpCdu.dataName"),//
})
@Data
@ToString(callSuper = true, exclude = {"joiners"})
@EqualsAndHashCode(callSuper = true, exclude = {"joiners"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ConnPoolCollDataUnit extends EntityBase {

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
     * A visszairány a JDBC Connection Pool-hoz a kapcsolótáblán keresztül
     */
    @OneToMany(mappedBy = "connPoolCollDataUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnPoolConnPoolCollDataUnitJoiner> joiners = new LinkedList<>();

    /**
     * Konstruktor
     *
     * @param restPathMask REST path maszk
     * @param entityName   entitás neve
     * @param dataName     adatnév
     * @param unit         mértékegység
     * @param description  leírás
     */
    public ConnPoolCollDataUnit(String restPathMask, String entityName, String dataName, String unit, String description) {
        this.restPathMask = restPathMask;
        this.entityName = entityName;
        this.dataName = dataName;
        this.unit = unit;
        this.description = description;
    }

}
