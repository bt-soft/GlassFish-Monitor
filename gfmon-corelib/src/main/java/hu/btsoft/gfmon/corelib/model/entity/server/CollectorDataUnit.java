/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    CollectorDataUnit.java
 *  Created: 2018.01.06. 15:59:15
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.corelib.model.entity.EntityBase;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * A gyűjtött adatok neve, mértékeygsége és leírása a GF REST Path-ból kinyerve
 *
 * pl.:
 * http://localhost:4848/monitoring/domain/server/jvm/memory
 *
 * •committedheapsize-count
 * ◦ unit : bytes
 * ◦ lastsampletime : 1515250670904
 * ◦ dataName : CommittedHeapSize
 * ◦ count : 417333248
 * ◦ description : Amount of memory in bytes that is committed for the Java virtual machine to use
 * ◦ starttime : 1515247764215
 *
 *
 * @author BT
 */
@Entity
@Cacheable(true)
@Table(name = "COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        indexes = {
            @Index(name = "IDX_RESTPATH", columnList = "RESTPATH", unique = false)
        })
@NamedQueries({
    @NamedQuery(name = "CollectorDataUnit.findAll", query = "SELECT cdu from CollectorDataUnit cdu ORDER BY cdu.restPath, cdu.dataName"),//
    @NamedQuery(name = "CollectorDataUnit.findAllPaths", query = "SELECT cdu.restPath from CollectorDataUnit cdu GROUP BY cdu.restPath ORDER BY cdu.restPath, cdu.dataName"),//
    @NamedQuery(name = "CollectorDataUnit.findByPath", query = "SELECT cdu from CollectorDataUnit cdu WHERE cdu.restPath = :restPath ORDER BY cdu.dataName"),//
    @NamedQuery(name = "CollectorDataUnit.findByServerId", query = "SELECT cdu from CollectorDataUnit cdu INNER JOIN cdu.serverCollDataUnitJoiners j WHERE  j.pk.server.id = :serverId ORDER BY cdu.restPath, cdu.dataName"),//
    @NamedQuery(name = "CollectorDataUnit.findByActiveAndServerId", query = "SELECT cdu from CollectorDataUnit cdu INNER JOIN cdu.serverCollDataUnitJoiners j WHERE j.active = 1 AND j.pk.server.id = :serverId ORDER BY cdu.restPath, cdu.dataName"),//
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class CollectorDataUnit extends EntityBase {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    @ColumnPosition(position = 20)
    @NotNull(message = "A restPath nem lehet null")
    @Column(length = 50)
    private String restPath;

    /**
     * Milyen entitás használja?
     */
    @ColumnPosition(position = 21)
    @NotNull(message = "Az entityName nem lehet null")
    @Column(length = 50)
    private String entityName;

    /**
     * Az adat megnevezése
     */
    @ColumnPosition(position = 22)
    @NotNull(message = "A name nem lehet null")
    @Column(length = 50)
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    @ColumnPosition(position = 23)
    @NotNull(message = "Az unit nem lehet null")
    @Column(length = 15)
    private String unit;

    /**
     * Az adat leírása
     */
    @ColumnPosition(position = 24)
    @NotNull(message = "A description nem lehet null")
    @Column(length = 512)
    private String description;

    /**
     * A visszairány a szerverhez
     */
    @OneToMany(mappedBy = "pk.collectorDataUnit", fetch = FetchType.LAZY)
    private List<ServerCollDataUnitJoiner> serverCollDataUnitJoiners;

    /**
     * Az adott szerver esetén aktív?
     * Nem mentjük az adatbázisba, itt csak runtime értékként használjuk, a ServerCollDataUnitJoiner entitásban van az adott szerverhez rendelve!
     *
     * A flag kezelése:
     * - Itt az entitás @PostLoad/@PostUpdate JPA életciklus metódusában
     * - A ServerCollDataUnitJoiner join entitás @PostLoad/@PostUpdate JPA életciklus metódusában
     */
    @Transient
    private Boolean active;

    /**
     * Konstruktor
     *
     * @param restPath    path, ahol szerepel az adatnév
     * @param entityName  entitás neve, ami használja
     * @param dataName    adatnév
     * @param unit        mértékegység
     * @param description leírás
     * @param active      aktiv?
     */
    public CollectorDataUnit(String restPath, String entityName, String dataName, String unit, String description, Boolean active) {
        this.restPath = restPath;
        this.entityName = entityName;
        this.dataName = dataName;
        this.unit = unit;
        this.description = description;
        this.active = active;
    }

    /**
     * A mentés előtt beállítjuk az active adatot, ha szükséges
     *
     */
    @PrePersist
    @PreUpdate
    protected void pre() {
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
    }

    /**
     * Felolvasás után beállítjuk az active adatot, ha szükséges
     */
    @PostLoad
    @PostUpdate
    protected void post() {
        if (this.active == null) {
            this.active = Boolean.TRUE;
        }
    }

}
