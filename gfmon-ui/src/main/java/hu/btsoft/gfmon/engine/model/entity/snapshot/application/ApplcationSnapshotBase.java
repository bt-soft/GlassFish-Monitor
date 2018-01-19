/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplcationSnapshotBase.java
 *  Created: 2018.01.19. 16:44:21
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.snapshot.application;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ApplcationSnapshotBase extends SnapshotBase {

    /**
     * Az alkalmazás rövid neve pl.: gf-mon
     */
    @NotNull(message = "Az appShortName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appShortName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_SHORT_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 11)
    private String appShortName;

    /**
     * Az alkalmazás valódi neve pl.: gf-mon-0.0.1-dev
     */
    @NotNull(message = "Az appRealName nem lehet null")
    @Size(min = 5, max = 255, message = "Az appRealName mező hossza {min} és {max} között lehet")
    @Column(name = "APP_REAL_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 12)
    private String appRealName;

}
