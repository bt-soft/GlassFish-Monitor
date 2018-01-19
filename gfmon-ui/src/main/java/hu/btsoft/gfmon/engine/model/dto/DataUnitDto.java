/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    DataUnitDto.java
 *  Created: 2018.01.06. 16:22:31
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A gyűjtött adatok neve, mértékeygsége és leírása a GF REST Path-ból kinyerve
 *
 * @author BT
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataUnitDto {

    /**
     * A mértékegység milyen REST Path-on van?
     */
    private String restPath;

    /**
     * Milyen entitás használja?
     */
    private String entityName;

    /**
     * Az adat megnevezése
     */
    private String dataName;

    /**
     * Mértékegység megnevezése
     */
    private String unit;

    /**
     * Az adat leírása
     */
    private String description;

}
