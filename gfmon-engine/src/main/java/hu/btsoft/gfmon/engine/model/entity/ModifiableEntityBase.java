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
package hu.btsoft.gfmon.engine.model.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Módosítható Entitás ős
 *
 * @author BT
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
public class ModifiableEntityBase extends EntityBase {

    /**
     * Létrehozó user
     */
    @Size(min = 1, max = 30, message = "A createdBy 1-30 hosszú lehet")
    @NotNull(message = "A createdBy nem lehet null")
    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy;

    /**
     * A létrehozás ideje
     */
    //A létrehozás ideje az EntityBase osztályban
//
    /**
     * Módosító user
     */
    @Size(min = 1, max = 30, message = "A modifiedBy 1-30 hosszú lehet")
    //@NotNull(message = "A modifiedBy nem lehet null")
    @Column(name = "MODIFIED_BY", nullable = true)
    private String modifiedBy;

    /**
     * A módosítás ideje
     */
    @Column(name = "MODIFIED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;

    /**
     * JPA optimista lock
     */
    @Version
    @Column(name = "Version", columnDefinition = "Integer DEFAULT 0", nullable = false)
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
