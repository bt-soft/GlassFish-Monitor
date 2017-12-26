/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    CurrentObjectValueDto.java
 *  Created: 2017.12.24. 17:09:10
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A GF REST interfészéről kigyűjtött adatok dto-ja
 * Aktuális objektum értéke
 *
 * @author BT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CurrentObjectValueDto extends ValueBaseDto {

    /**
     * A mért érték értéke
     */
    private Object current;

}
