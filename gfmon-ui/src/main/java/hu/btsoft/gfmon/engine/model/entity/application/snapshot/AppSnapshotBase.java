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
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import hu.btsoft.gfmon.engine.model.entity.application.Application;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Szerver mérési eredmények entitások ős osztálya
 *
 * @author BT
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true, exclude = "application")
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public abstract class AppSnapshotBase extends EntityBase {

    @NotNull(message = "A pathSuffix nem lehet null")
    @Size(min = 3, max = 255, message = "Az pathSuffix mező hossza {min} és {max} között lehet")
    @Column(length = 255, nullable = false)
    @ColumnPosition(position = 9)
    private String pathSuffix;

    /**
     * A mérés melyik alkalmazáshoz tartozik?
     * (automatikusan index képződik rá)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_ID")
    @ColumnPosition(position = 10)
    private Application application;
}
