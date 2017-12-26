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
import lombok.Data;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás ős osztály
 *
 * @author BT
 */
@MappedSuperclass
@Data
@Customizer(EntityColumnPositionCustomizer.class)
public class EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A rekord ID-je
     * (automatikusan index képződik rá)
     */
    @Id
    @NotNull(message = "Az ID nem lehet null")
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    //    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gfMonSeq")
    @SequenceGenerator(name = "gfMonSeq", sequenceName = "GFMON_SEQ")
    @ColumnPosition(position = 0)
    private Long id;

    /**
     * A létrehozás dátuma és ideje
     */
    @ColumnPosition(position = 100)
    @NotNull(message = "A createdDate nem lehet null")
    @Column(name = "CREATED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /**
     * Új entitásnál a létrehozás dátumát kitöltjük, ha esetleg nem adták volna meg
     */
    @PrePersist
    protected void prePersist() {
        //A createdDate kitöltése, ha üres
        if (createdDate == null) {
            createdDate = new Date();
        }
    }

}
