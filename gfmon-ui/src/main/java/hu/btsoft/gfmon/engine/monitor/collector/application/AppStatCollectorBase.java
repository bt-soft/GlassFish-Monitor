/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    AppStatCollectorBase.java
 *  Created: 2018.01.27. 13:09:09
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application;

import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.CollectorBase;
import java.util.List;
import java.util.Set;
import javax.json.JsonObject;

/**
 *
 * @author BT
 */
public abstract class AppStatCollectorBase extends CollectorBase {

    @Override
    public List<CollectedValueDto> fetchValues(JsonObject entities, Set<String> collectedDatatNames) {
        return super.fetchValues(entities, collectedDatatNames);
    }

}
