/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ModifiableEntityBase.java
 *  Created: 2017.12.23. 12:11:55
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Módosítható Entitás ős
 *
 * @author BT
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Customizer(EntityColumnPositionCustomizer.class)
public class ModifiableEntityBase extends EntityBase {

    /**
     * Szekvencia, Id-szerű egyedi sorszám (de ahhoz semmi köze)
     * Runtime változó, csak a konfig UI felületen használjuk, pl az újonnan felvett szerver rekordok azonosítása érdekében
     */
    @Transient
    private Long runtimeSeqId;

    /**
     * Létrehozó user
     */
    @Size(min = 1, max = 30, message = "A createdBy 1-30 hosszú lehet")
    @NotNull(message = "A createdBy nem lehet null")
    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    @ColumnPosition(position = 101)
    private String createdBy;

    /**
     * A módosítás ideje
     */
    @Column(name = "MODIFIED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ColumnPosition(position = 102)
    private Date modifiedDate;

    /**
     * Módosító user
     */
    @Size(min = 1, max = 30, message = "A modifiedBy 1-30 hosszú lehet")
    @Column(name = "MODIFIED_BY", nullable = true)
    @ColumnPosition(position = 103)
    private String modifiedBy;

    /**
     * JPA optimista lock
     */
    @Version
    @Column(name = "Version", columnDefinition = "Integer DEFAULT 0", nullable = false)
    @ColumnPosition(position = 104)
    private Long optLockVersion;

    /**
     * Technikai mezők karbantartása - új entitás mentése
     */
    @PrePersist
    @Override
    protected void prePersist() {

        super.prePersist();

        //createdBy kitöltése, ha üres
        if (createdBy == null) {
            createdBy = "!Unknown User!";
        }

    }

    /**
     * Technikai mezők karbantartása - entitás update
     */
    @PreUpdate
    protected void preUpdate() {
        //ModUser kitöltése, ha üres
        if (modifiedBy == null) {
            modifiedBy = "!Unknown User!";
        }

        //ModDat kitöltése, ha üres
        if (modifiedDate == null) {
            modifiedDate = new Date();
        }
    }
}
