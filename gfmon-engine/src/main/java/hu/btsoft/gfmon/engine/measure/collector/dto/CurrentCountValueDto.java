/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    CurrentCountValueDto.java
 *  Created: 2017.12.24. 17:10:22
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A GF REST interfészéről kigyűjtött adatok dto-ja
 * HighWatermark/LowWatermark/aktuális érték adatok
 *
 * @author BT
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CurrentCountValueDto extends ValueBaseDto {

    /**
     * A mérté érték aktuális értéke
     */
    private long current;

    /**
     * A mért érték legkisebb értéke
     */
    private long lowWatermark;

    /**
     * A mért érték legnagyobb értéke
     */
    private long highWatermark;

}
