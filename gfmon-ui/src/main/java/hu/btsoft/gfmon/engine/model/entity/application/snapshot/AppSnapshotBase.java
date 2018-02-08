/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    AppSnapshotBase.java
 *  Created: 2017.12.26. 15:08:12
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Alklamazás statisztika entitások ős osztálya
 *
 * @author BT
 */
@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class AppSnapshotBase extends EntityBase {

    /**
     * A mérés melyik alkalmazáshoz tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 20)
    private Application application;
}
