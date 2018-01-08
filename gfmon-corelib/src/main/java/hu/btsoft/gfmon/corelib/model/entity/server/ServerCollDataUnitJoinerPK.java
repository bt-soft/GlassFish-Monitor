/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    ServerCollDataUnitJoinerPK.java
 *  Created: 2018.01.08. 17:03:13
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity.server;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * Kompozik elsődleges kulcs begyazható JPA osztály
 *
 * @author BT
 */
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
@Customizer(EntityColumnPositionCustomizer.class)
public class ServerCollDataUnitJoinerPK implements Serializable {

    @ColumnPosition(position = 20)
    private Server server;

    @ColumnPosition(position = 21)
    private CollectorDataUnit collectorDataUnit;
}
