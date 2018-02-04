/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationAppCollDataUnitJoiner.java
 *  Created: 2018.02.02. 16:25:42
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.audit.EntityAuditListener;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Application <-> AppCollectorDataUnit Kapcsolótábla extra mezőkkel
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "APPLICATION_APP_CDU", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@EqualsAndHashCode(exclude = {"application", "appCollectorDataUnit"})
@ToString(exclude = {"application", "appCollectorDataUnit"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@IdClass(ApplicationAppCollDataUnitJoinerPK.class)
public class ApplicationAppCollDataUnitJoiner implements Serializable {

    @Id
    @Column(name = "APPLICATION_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 1)
    private Long applicationId;

    @Id
    @Column(name = "APP_CDU_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 2)
    private Long appCollectorDataUnitId;

    /**
     * Az adott alkalmazásban aktív az adatnév gyűjtése?
     */
    @NotNull(message = "Az active nem lehet null")
    @Column(nullable = false)
    @ColumnPosition(position = 3)
    private boolean active;

    /**
     * Kieginfo, ha a monitor tilt le egy oldalt, mert pl.: törölték a http-listener-2-t
     */
    @ColumnPosition(position = 4)
    private String additionalMessage;

    /**
     * Application
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID", nullable = false)
    private Application application;

    /**
     * CDU
     * Itt nem szabad kiadni a "cascade = CascadeType.ALL"-t mert végigtörli az összes mértékegységet is :)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_CDU_ID", referencedColumnName = "ID", nullable = false)
    private AppCollectorDataUnit appCollectorDataUnit;

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
     * A módosítás ideje
     */
    @Column(name = EntityAuditListener.AUDIT_COLUMN_MODIFIED_DATE, nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @ColumnPosition(position = 102)
    private Date modifiedDate;

    /**
     * Módosító user
     */
    @Size(min = 1, max = 30, message = "A modifiedBy {min} és {max} közötti hosszúságú lehet")
    @Column(name = EntityAuditListener.AUDIT_COLUMN_MODIFIED_BY, nullable = true)
    @ColumnPosition(position = 103)
    private String modifiedBy;

    /**
     * JPA optimista lock
     */
    @Version
    @Column(name = "Version", columnDefinition = "Integer DEFAULT 0", nullable = false)
    @ColumnPosition(position = 200)
    private Long optLockVersion;

    /**
     * Eredeti DB érték
     * Az adatbázisból való felolvasáss után beállítjuk.
     * Ezzel tudjuk detektálni, hogy változott-e az értéke az adatbázishoz képest?
     */
    @Transient
    private boolean activeDbValue;

    /**
     * Konstruktor
     *
     * @param application          szerver
     * @param appCollectorDataUnit a szerver CDU-ja
     * @param createdBy            létrehozó user
     * @param active               aktív az adott szerveren a CDU?
     */
    public ApplicationAppCollDataUnitJoiner(Application application, AppCollectorDataUnit appCollectorDataUnit, String createdBy, Boolean active) {
        this.application = application;
        this.appCollectorDataUnit = appCollectorDataUnit;
        this.createdBy = createdBy;
        this.active = active;
    }

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

    /**
     * Technikai mezők karbantartása - entitás update
     */
    @PreUpdate
    protected void preUpdate() {
        String currentAuditUser = EntityAuditListener.getCurrentAuditUser();
        if (currentAuditUser == null) {
            currentAuditUser = EntityAuditListener.UNKNOWN_USER;
        }
        modifiedBy = currentAuditUser;
        modifiedDate = new Date();
    }

    /**
     * Adatbázisművelet után beállítjuk a DB értéket, hogy detektálni tudjuk a változást
     */
    @PostPersist
    @PostLoad
    protected void post() {
        activeDbValue = active;
    }

}
