/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    QuantityValueDto.java
 *  Created: 2017.12.24. 17:06:33
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A GF REST interfészéről kigyűjtött adatok dto-ja
 * Számossági/darabszám adatok
 *
 * @author BT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class QuantityValueDto extends ValueBaseDto {

    /**
     * A mért érték darabszáma
     */
    private long count;
}
