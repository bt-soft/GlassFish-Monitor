/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    ServerSvrCollDataUnitJoinerPK.java
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
 * Kompozit kulcs Server <-> SvrCollectorDataUnit kapcsolótáblához
 *
 * @author BT
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerSvrCollDataUnitJoinerPK implements Serializable {

    /**
     * Server ID
     */
    private Long serverId;

    /**
     * Server CDU ID
     */
    private Long svrCollectorDataUnitId;
}
