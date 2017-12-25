/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    EntityBase.java
 *  Created: 2017.12.23. 12:01:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import lombok.Data;

/**
 * JPA entitás ős osztály
 * JAXB a JAX-WS szerializáció miatt a UI felület felé
 *
 * @author BT
 */
@MappedSuperclass
@Data
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"id", "createDat"})
public class EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A rekord ID-je
     * (automatikusan index képződik rá)
     */
    @Id
    @Basic(optional = false)
    @NotNull(message = "Az ID nem lehet null")
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    //    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gfMonSeq")
    @SequenceGenerator(name = "gfMonSeq", sequenceName = "GFMON_SEQ")
    @XmlAttribute(required = true)
    private Long id;

    /**
     * A létrehozás dátuma és ideje
     */
    @Basic(optional = false)
    @NotNull(message = "A createDat nem lehet null")
    @Column(name = "CREATE_DAT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @XmlAttribute(required = true)
    private Date createDat;

    /**
     * Új entitásnál a létrehozás dátumát kitöltjük, ha esetleg nem adták volna meg
     */
    @PrePersist
    protected void prePersist() {
        //A createDat kitöltése, ha üres
        if (createDat == null) {
            createDat = new Date();
        }
    }

}
