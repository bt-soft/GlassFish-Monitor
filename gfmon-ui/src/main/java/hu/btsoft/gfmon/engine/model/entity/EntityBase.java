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

import hu.btsoft.gfmon.corelib.model.audit.EntityAuditListener;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@EntityListeners(EntityAuditListener.class)
public class EntityBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * A rekord ID-je
     * (automatikusan index képződik rá)
     */
    @Id
    @NotNull(message = "Az ID nem lehet null")
    @Column(name = "ID", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "gfMonSeq", sequenceName = "GFMON_SEQ")
    @ColumnPosition(position = 0)
    private Long id;

    /**
     * A létrehozás dátuma és ideje
     */
    @ColumnPosition(position = 100)
    @NotNull(message = "A createdDate nem lehet null")
    @Column(name = EntityAuditListener.AUDIT_COLUMN_CREATED_DATE, nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    /**
     * Létrehozó user
     */
    @Size(min = 1, max = 30, message = "A createdBy {min} és {max} közötti hosszúságú lehet")
    @NotNull(message = "A createdBy nem lehet null")
    @Column(name = EntityAuditListener.AUDIT_COLUMN_CREATED_BY, nullable = false, updatable = false)
    @ColumnPosition(position = 101)
    private String createdBy;

    /**
     * Új entitásnál a létrehozás dátumát mindenképpen kitöltjük
     */
    @PrePersist
    protected void prePersist() {
        String currentAuditUser = EntityAuditListener.getCurrentAuditUser();
        if (currentAuditUser == null) {
            currentAuditUser = EntityAuditListener.UNKNOWN_USER;
        }
        createdBy = currentAuditUser;
        createdDate = new Date();
    }

}
