/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ServerMonitorValueDto.java
 *  Created: 2017.12.24. 17:00:03
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server;

import hu.btsoft.gfmon.engine.monitor.collector.types.ValueUnitType;
import java.util.Date;
import lombok.Data;

/**
 * A GF REST interfészéről kigyűjtött adatok dto osztálya
 *
 * @author BT
 */
@Data
public class ServerMonitorValueDto {

    /**
     * Milyen path-on mértük?
     */
    private String path;

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
     * A mért érték darabszáma
     */
    private Long count;

    /**
     * A mért érték értéke
     */
    private Object current;

    /**
     * A mért érték legkisebb értéke
     */
    private Long lowWatermark;

    /**
     * A mért érték legnagyobb értéke
     */
    private Long highWatermark;
}
