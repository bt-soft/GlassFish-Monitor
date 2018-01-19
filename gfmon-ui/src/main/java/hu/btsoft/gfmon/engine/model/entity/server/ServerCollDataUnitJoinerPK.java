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
package hu.btsoft.gfmon.engine.model.entity.server;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kompozik elsődleges kulcs a SERVER_COLLDATA_UNIT táblához
 *
 * @author BT
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerCollDataUnitJoinerPK implements Serializable {

    /**
     * Server ID
     */
    private Long serverId;

    /**
     * CDU ID
     */
    private Long collectorDataUnitId;
}
