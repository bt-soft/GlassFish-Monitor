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
package hu.btsoft.gfmon.engine.model.entity.connpool;

import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JDLB erőforrások statisztika entitások ős osztálya
 *
 * @author BT
 */
@MappedSuperclass
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public abstract class JdbcResourceSnapshotBase extends EntityBase {

    // Üres osztály, csak amiatt, hogy a napi takarításokat el tudjuk végezni
    // A takartító (JdbcConnectionPoolService.deleteOldRecords()) csak az JdbcResourceSnapshotBase leszármazottait kezeli
    // Ha igen, akkor pucolhatja a régi entitás bejegyzéseket az adatbázisból
}
