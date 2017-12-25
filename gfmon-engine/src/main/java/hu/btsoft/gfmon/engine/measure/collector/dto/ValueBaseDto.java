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

    /**
     * Melyik modulból származik?
     */
    private String monitoringServiceModuleName;

    /**
     * Milyen uri-n mértük?
     */
    private String uri;

    /**
     * Mértékegység
     */
    private ValueUnitType unit;

    /**
     * A mintavétel dátuma
     */
    private Date lastSampleTime;

    /**
     * Indítás dátuma
     */
    private Date startTime;

    /**
     * A mért érték neve
     */
    private String name;

    /**
     * A mért érték leírása
     */
    private String description;
}
