/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ConnPoolConnPoolCollDataUnitJoinerPK.java
 *  Created: 2018.02.02. 16:24:03
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.connpool;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kompozik kulcs JdbcConnectionPool <-> JdbcConnectionPoolCollectorDataUnit kapcsolótáblához
 *
 * @author BT
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConnPoolConnPoolCollDataUnitJoinerPK implements Serializable {

    /**
     * JdbcConnectionPool ID
     */
    private Long connPoolId;

    /**
     * JdbcConnectionPoolCollectorDataUnit CDU ID
     */
    private Long connPoolCollectorDataUnitId;

}
