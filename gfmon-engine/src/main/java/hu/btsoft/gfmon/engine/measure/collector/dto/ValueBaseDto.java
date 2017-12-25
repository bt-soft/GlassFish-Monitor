/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ValueBaseDto.java
 *  Created: 2017.12.24. 17:00:03
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure.collector.dto;

import hu.btsoft.gfmon.engine.measure.collector.types.ValueUnitType;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 * A GF REST interfészéről kigyűjtött adatok dto ős osztálya
 *
 * @author BT
 */
@Data
@ToString(of = {"unit", "lastSampleTime", "startTime", "name", "description"})
public abstract class ValueBaseDto {

    private String uri;
    private ValueUnitType unit;
    private Date lastSampleTime;
    private Date startTime;
    private String name;
    private String description;
}
