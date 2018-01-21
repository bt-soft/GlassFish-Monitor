/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationCollectorDataUnit.java
 *  Created: 2018.01.19. 17:22:20
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Cacheable(true)
@Table(name = "APP_COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        indexes = {
            @Index(name = "IDX_APP_CDU_RESTPATHMASK", columnList = "RESTPATHMASK", unique = false),
            @Index(name = "IDX_APP_CDU_ENTITYNAME", columnList = "ENTITYNAME", unique = false)
        })
@Data
@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true, exclude = "joiners")
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ApplicationCollectorDataUnit extends EntityBase {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    @ColumnPosition(position = 20)
    @NotNull(message = "A restPathMask nem lehet null")
    @Column(length = 50, nullable = false)
    private String restPathMask;

    /**
     * Milyen entitás használja?
     */
    @ColumnPosition(position = 21)
    @NotNull(message = "Az entityName nem lehet null")
    @Column(length = 50, nullable = false)
    private String entityName;

    /**
     * Az adat megnevezése
     */
    @ColumnPosition(position = 22)
    @NotNull(message = "A name nem lehet null")
    @Column(length = 50, nullable = false)
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    @ColumnPosition(position = 23)
    @NotNull(message = "Az unit nem lehet null")
    @Column(length = 15, nullable = false)
    private String unit;

    /**
     * Az adat leírása
     */
    @ColumnPosition(position = 24)
    @NotNull(message = "A description nem lehet null")
    @Column(length = 512, nullable = false)
    private String description;

}
