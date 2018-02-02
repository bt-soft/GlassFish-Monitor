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
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Slf4j
public abstract class AppStatCollectorBase extends CollectorBase {

    private static final ThreadLocal<String> CURRENT_PATH = new ThreadLocal();

    /**
     * Path elkérése
     *
     * @return path
     */
    @Override
    public String getPath() {
        return CURRENT_PATH.get();
    }

    @PostConstruct
    protected void init() {
        CURRENT_PATH.set(this.getPathForEntityMapping());
    }

    /**
     * Aktuális path beállítása
     *
     * @param params paraméterek MAP-je
     */
    public void setCurrentPath(Map<String, String> params) {
        String path = this.getPathForEntityMapping();
        for (Map.Entry<String, String> paramEntry : params.entrySet()) {
            path = path.replace(paramEntry.getKey(), paramEntry.getValue());
        }

        CURRENT_PATH.set(path);
    }

    /**
     *
     * @param entities
     * @param collectedDatatNames
     *
     * @return
     */
    @Override
    public List<CollectedValueDto> fetchValues(JsonObject entities, Set<String> collectedDatatNames) {
        return super.fetchValues(entities, collectedDatatNames);
    }

}
