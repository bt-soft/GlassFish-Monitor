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
import hu.btsoft.gfmon.corelib.model.entity.EntityBase;
import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.PostLoad;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Server -> CollectorDataUnit @OneTomany kapcsolótábla extra oszloppal
 *
 * @author BT
 */
@Entity
@Table(name = "SERVER_COLLDATA_UNIT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@AssociationOverrides({
    @AssociationOverride(name = "pk.server", joinColumns = @JoinColumn(name = "SERVER_ID", referencedColumnName = "ID")),
    @AssociationOverride(name = "pk.collectorDataUnit", joinColumns = @JoinColumn(name = "COLLECTORDATAUNIT_ID", referencedColumnName = "ID"))
})
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ServerCollDataUnitJoiner extends EntityBase {

    /**
     * Kompozik elsődleges kulcs
     */
    @Embedded
    private ServerCollDataUnitJoinerPK pk;

    /**
     * Az adott szerveren aktív az adatnév gyűjtése?
     */
    @NotNull(message = "Az active nem lehet null")
    @ColumnPosition(position = 30)
    private Boolean active;

    /**
     * Konstruktor
     *
     * @param server            szerver entitás példány
     * @param collectorDataUnit CDU entitás példány
     */
    public ServerCollDataUnitJoiner(Server server, CollectorDataUnit collectorDataUnit) {
        this.pk = new ServerCollDataUnitJoinerPK(server, collectorDataUnit);
        this.active = collectorDataUnit.getActive();
    }

    /**
     * Szerver elkérése
     *
     * @return szerver entitás
     */
    public Server getServer() {
        return pk.getServer();
    }

    /**
     * Szerver beállítása
     *
     * @param server szerver entitás
     */
    public void setServer(Server server) {
        pk.setServer(server);
    }

    /**
     * CDU entitás elkérése
     *
     * @return CDU entitás
     */
    public CollectorDataUnit getCollectorDataUnit() {
        return pk.getCollectorDataUnit();
    }

    /**
     * CDU entitás beállítása
     *
     * @param collectorDataUnit CDU entitás
     */
    public void setCollectorDataUnit(CollectorDataUnit collectorDataUnit) {
        pk.setCollectorDataUnit(collectorDataUnit);
    }

    /**
     * A mentés előtt a CDU entitás active Transient mezőjéből állítjuk be az aktív mezőt
     */
    @PrePersist
    @PreUpdate
    protected void pre() {
        this.active = pk.getCollectorDataUnit().getActive();
    }

    /**
     * A CDU entitás active Transient mezőjét itt állítjuk be a felolvasás után
     */
    @PostLoad
    @PostUpdate
    protected void post() {
        pk.getCollectorDataUnit().setActive(this.active);
    }
}
