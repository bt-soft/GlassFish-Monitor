/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    CollectorDataUnit.java
 *  Created: 2018.01.06. 15:59:15
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * A gyűjtött adatok neve, mértékeygsége és leírása a GF REST Path-ból kinyerve
 *
 * pl.:
 * http://localhost:4848/monitoring/domain/server/jvm/memory
 *
 * •committedheapsize-count
 * ◦ unit : bytes
 * ◦ lastsampletime : 1515250670904
 * ◦ dataName : CommittedHeapSize
 * ◦ count : 417333248
 * ◦ description : Amount of memory in bytes that is committed for the Java virtual machine to use
 * ◦ starttime : 1515247764215
 *
 *
 * @author BT
 */
@Entity
@Cacheable(true)
@Table(name = "COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@NamedQueries({
    @NamedQuery(name = "CollectorDataUnit.findAll", query = "SELECT e from CollectorDataUnit e ORDER BY e.restPath, e.dataName"),//
    @NamedQuery(name = "CollectorDataUnit.findByPath", query = "SELECT e from CollectorDataUnit e WHERE e.restPath = :restPath ORDER BY e.dataName"),//
    //
    @NamedQuery(name = "CollectorDataUnit.findAllPaths", query = "SELECT e.restPath from CollectorDataUnit e GROUP BY e.restPath ORDER BY e.restPath, e.dataName"),//
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class CollectorDataUnit extends EntityBase {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    @ColumnPosition(position = 20)
    @NotNull(message = "A restPath nem lehet null")
    @Column(length = 50)
    private String restPath;

    /**
     * Milyen entitás használja?
     */
    @ColumnPosition(position = 21)
    @NotNull(message = "Az entityName nem lehet null")
    @Column(length = 50)
    private String entityName;

    /**
     * Az adat megnevezése
     */
    @ColumnPosition(position = 22)
    @NotNull(message = "A name nem lehet null")
    @Column(length = 50)
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    @ColumnPosition(position = 23)
    @NotNull(message = "Az unit nem lehet null")
    @Column(length = 15)
    private String unit;

    /**
     * Az adat leírása
     */
    @ColumnPosition(position = 24)
    @NotNull(message = "A description nem lehet null")
    @Column(length = 512)
    private String description;
}
