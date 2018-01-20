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
package hu.btsoft.gfmon.engine.model.entity.server;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import java.util.List;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * A gyűjtött adatok neve, mértékeygsége és leírása a GF REST Path-ból kinyerve
 *
 * pl.: http://localhost:4848/monitoring/domain/server/jvm/memory
 *
 * • committedheapsize-count
 * ◦ unit : bytes
 * ◦ lastsampletime : 1515250670904
 * ◦ dataName : CommittedHeapSize
 * ◦ count : 417333248
 * ◦ description : Amount of memory
 * in bytes that is committed for the Java virtual machine to use ◦ starttime : 1515247764215
 *
 *
 * @author BT
 */
@Entity
@Cacheable(true)
@Table(name = "SRV_COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME,
        indexes = {
            @Index(name = "IDX_SVR_CDU_RESTPATH", columnList = "RESTPATH", unique = false),
            @Index(name = "IDX_SVR_CDU_ENTITYNAME", columnList = "ENTITYNAME", unique = false)
        })
@NamedQueries({
    @NamedQuery(name = "SvrCollectorDataUnit.findAll", query = "SELECT scdu from SvrCollectorDataUnit scdu ORDER BY scdu.restPath, scdu.dataName"),//
    @NamedQuery(name = "SvrCollectorDataUnit.findAllPaths", query = "SELECT scdu.restPath from SvrCollectorDataUnit scdu GROUP BY scdu.restPath ORDER BY scdu.restPath, scdu.dataName"),//
    @NamedQuery(name = "SvrCollectorDataUnit.findByPath", query = "SELECT scdu from SvrCollectorDataUnit scdu WHERE scdu.restPath = :restPath ORDER BY scdu.dataName"),//
    @NamedQuery(name = "SvrCollectorDataUnit.findByServerId", query = "SELECT scdu from SvrCollectorDataUnit scdu INNER JOIN scdu.joiners j WHERE  j.serverId = :serverId ORDER BY scdu.restPath, scdu.dataName"),//
    @NamedQuery(name = "SvrCollectorDataUnit.findByActiveAndServerId", query = "SELECT scdu from SvrCollectorDataUnit scdu INNER JOIN scdu.joiners j WHERE j.active = true AND j.serverId = :serverId ORDER BY scdu.restPath, scdu.dataName"),//
})
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = "joiners")
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class SvrCollectorDataUnit extends EntityBase {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    @ColumnPosition(position = 20)
    @NotNull(message = "A restPath nem lehet null")
    @Column(length = 50, nullable = false)
    private String restPath;

    /**
     * Milyen entitás használja?
     */
    @ColumnPosition(position = 21)
    @NotNull(message = "Az entityName nem lehet null")
    @Column(length = 50, nullable = false)
    private String entityName;

    /**
     * Az adat megnevezése
     */
    @ColumnPosition(position = 22)
    @NotNull(message = "A name nem lehet null")
    @Column(length = 50, nullable = false)
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    @ColumnPosition(position = 23)
    @NotNull(message = "Az unit nem lehet null")
    @Column(length = 15, nullable = false)
    private String unit;

    /**
     * Az adat leírása
     */
    @ColumnPosition(position = 24)
    @NotNull(message = "A description nem lehet null")
    @Column(length = 512, nullable = false)
    private String description;

    /**
     * A visszairány a szerverhez
     */
    @OneToMany(mappedBy = "svrCollectorDataUnit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServerSvrCollDataUnitJoiner> joiners;

    /**
     * Konstruktor
     *
     * @param restPath    REST path, ahol szerepel a mért adatnév
     * @param entityName  JPA entitás neve, ami használja ezt az adatevet (ez lehet, hogy nem is kellene...)
     * @param dataName    adatnév
     * @param unit        mértékegység
     * @param description leírás
     */
    public SvrCollectorDataUnit(String restPath, String entityName, String dataName, String unit, String description) {
        this.restPath = restPath;
        this.entityName = entityName;
        this.dataName = dataName;
        this.unit = unit;
        this.description = description;
    }
}
