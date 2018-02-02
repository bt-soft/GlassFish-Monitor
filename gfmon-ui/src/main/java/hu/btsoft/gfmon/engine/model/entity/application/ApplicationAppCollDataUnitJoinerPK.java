/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    ApplicationAppCollDataUnitJoinerPK.java
 *  Created: 2018.02.02. 16:24:03
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author BT
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ApplicationAppCollDataUnitJoinerPK implements Serializable {

    /**
     * Application ID
     */
    private Long applicationId;

    /**
     * Application CDU ID
     */
    private Long appCollectorDataUnitId;

}
