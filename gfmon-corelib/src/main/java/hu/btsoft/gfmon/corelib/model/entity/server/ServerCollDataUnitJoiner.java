/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    ServerCollDataUnitJoiner.java
 *  Created: 2018.01.08. 14:53:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
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
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Server -> CollectorDataUnit @OneTomany kapcsolótábla extra oszloppal
 *
 * @author BT
 */
@Entity
@Cacheable(false)
@Table(name = "SERVER_COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@EqualsAndHashCode(exclude = {"server", "collectorDataUnit"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
@IdClass(ServerCollDataUnitJoinerPK.class)
public class ServerCollDataUnitJoiner implements Serializable {

    @Id
    @Column(name = "SERVER_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 1)
    private Long serverId;

    @Id
    @Column(name = "COLLECTORDATAUNIT_ID", insertable = false, updatable = false)
    @ColumnPosition(position = 2)
    private Long collectorDataUnitId;

    /**
     * Az adott szerveren aktív az adatnév gyűjtése?
     */
    @NotNull(message = "Az active nem lehet null")
    @Column(name = "ACTIVE", nullable = false)
    @ColumnPosition(position = 3)
    private Boolean active;

    /**
     * Szerver
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "SERVER_ID", referencedColumnName = "ID")
    @OrderBy("hostName, ipAddress")
    private Server server;

    /**
     * CDU
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "COLLECTORDATAUNIT_ID", referencedColumnName = "ID")
    @OrderBy("restPath, dataName")
    private CollectorDataUnit collectorDataUnit;

    /**
     * A létrehozás dátuma és ideje
     */
    @NotNull(message = "A createdDate nem lehet null")
    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @ColumnPosition(position = 100)
    private Date createdDate;

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
    @ColumnPosition(position = 200)
    private Long optLockVersion;

    /**
     * Konstruktor
     *
     * @param server            szerver
     * @param collectorDataUnit a szerver CDU-ja
     * @param createdBy         létrehozó user
     * @param active            aktív az adott szerveren a CDU?
     */
    public ServerCollDataUnitJoiner(Server server, CollectorDataUnit collectorDataUnit, String createdBy, Boolean active) {
        this.server = server;
        this.collectorDataUnit = collectorDataUnit;
        this.createdBy = createdBy;
        this.active = active;
    }

    /**
     * Technikai mezők karbantartása - új entitás mentése
     */
    @PrePersist
    protected void prePersist() {
        //A createdDate kitöltése, ha üres
        if (createdDate == null) {
            createdDate = new Date();
        }

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
        //ModDat kitöltése, ha üres
        if (modifiedDate == null) {
            modifiedDate = new Date();
        }
        //ModUser kitöltése, ha üres
        if (modifiedBy == null) {
            modifiedBy = "!Unknown User!";
        }
    }
}
