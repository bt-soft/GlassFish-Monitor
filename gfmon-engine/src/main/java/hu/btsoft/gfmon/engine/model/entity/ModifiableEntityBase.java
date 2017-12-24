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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Módosítható Entitás ős
 * JAXB a JAX-WS szerializáció miatt a UI felület felé
 *
 * @author BT
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = false)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "modUser", "modDat", "optLockVersion"})
public class ModifiableEntityBase extends EntityBase {

    /**
     * Módosító user
     */
    @Size(min = 1, max = 30, message = "A modUser 1-30 hosszú lehet")
    @NotNull(message = "A modUser nem lehet null")
    @Column(name = "MOD_USER", nullable = false)
    @XmlAttribute(required = true)
    private String modUser;

    /**
     * A módosítás dátuma és ideje
     */
    @Basic(optional = false)
    @NotNull(message = "A modDat nem lehet null")
    @Column(name = "MOD_DAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlAttribute(required = true)
    private Date modDat;

    /**
     * JPA optimista lock
     */
    @Version
    @Column(name = "OPT_LOCK", columnDefinition = "integer DEFAULT 0", nullable = false)
    @XmlAttribute(required = true)
    private Long optLockVersion;

    /**
     * Technikai mezők karbantartása
     */
    @PreUpdate
    @PrePersist
    protected void updateFileds() {
        //ModUser kitöltése, ha üres
        if (modUser == null) {
            modUser = "!Unknown User!";
        }
        //ModDat kitöltése, ha üres
        if (modDat == null) {
            modDat = new Date();
        }
    }
}
