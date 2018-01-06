/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    SnapshotBase.java
 *  Created: 2017.12.26. 15:08:12
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity.snapshot;

import hu.btsoft.gfmon.corelib.model.entity.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.entity.EntityBase;
import hu.btsoft.gfmon.corelib.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.corelib.model.entity.Server;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Mérési eredmények entitások ős osztálya
 *
 * @author BT
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public abstract class SnapshotBase extends EntityBase {

    /**
     * A mérés melyik szerverhez tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVER_ID")
    @ColumnPosition(position = 10)
    private Server server;
}
