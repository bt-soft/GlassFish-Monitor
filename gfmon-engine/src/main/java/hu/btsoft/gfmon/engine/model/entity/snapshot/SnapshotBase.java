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
package hu.btsoft.gfmon.engine.model.entity.snapshot;

import hu.btsoft.gfmon.engine.model.entity.ColumnPosition;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import hu.btsoft.gfmon.engine.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.Server;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
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

    /**
     * A mérés eredményét a GF mikor gyűjtötte?
     * (Ez a JSon 'lastsampletime' mezőjének értéke)
     */
    @Temporal(TemporalType.TIMESTAMP)
    //@NotNull(message = "A lastSampleTime nem lehet null")
    @Column(name = "LAST_SAMPLE_TIME"/*, nullable = false*/)
    @ColumnPosition(position = 11)
    private Date lastSampleTime;

}
