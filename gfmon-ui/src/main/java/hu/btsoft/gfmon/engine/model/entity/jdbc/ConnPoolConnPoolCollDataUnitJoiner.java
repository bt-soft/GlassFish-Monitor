/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolConnPoolCollDataUnitJoiner.java
 *  Created: 2018.02.03. 9:04:50
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.jdbc;

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
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "CONNPOOL_CONNPOOL_CDU", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@EqualsAndHashCode(exclude = {"connPool", "connPoolCollDataUnit"})
@ToString(exclude = {"connPool", "connPoolCollDataUnit"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@IdClass(ConnPoolConnPoolCollDataUnitJoinerPK.class)
public class ConnPoolConnPoolCollDataUnitJoiner implements Serializable {

    @Id
    @Column(name = "CONNPOOL_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 1)
    private Long connPoolId;

    @Id
    @Column(name = "CONNPOOL_CDU_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 2)
    private Long connPoolCollectorDataUnitId;

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
     * ConnPool
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "CONNPOOL_ID", referencedColumnName = "ID")
    private ConnPool connPool;

    /**
     * CDU
     * Itt nem szabad kiadni a "cascade = CascadeType.ALL"-t mert végigtörli az összes mértékegységet is :)
     */
    @ManyToOne
    @JoinColumn(name = "CONNPOOL_CDU_ID", referencedColumnName = "ID")
    private ConnPoolCollDataUnit connPoolCollDataUnit;

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
     * @param connPool             JDBC Connection Pool
     * @param connPoolCollDataUnit a JDBC Connection Pool CDU-ja
     * @param createdBy            létrehozó user
     * @param active               aktív az adott szerveren a CDU?
     */
    public ConnPoolConnPoolCollDataUnitJoiner(ConnPool connPool, ConnPoolCollDataUnit connPoolCollDataUnit, String createdBy, Boolean active) {
        this.connPool = connPool;
        this.connPoolCollDataUnit = connPoolCollDataUnit;
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
