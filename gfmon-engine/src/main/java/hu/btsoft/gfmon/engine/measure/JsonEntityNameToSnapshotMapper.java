/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JsonEntityNameToSnapshotMapper.java
 *  Created: 2017.12.24. 16:19:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.measure;

import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentCountValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import hu.btsoft.gfmon.engine.measure.collector.httpservice.RequestCollector;
import hu.btsoft.gfmon.engine.model.entity.Snapshot;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * A REST API-n keresztül érkező JSon entitások neveit megfelelteti az adatbázisba is lementett Snapshot entitás adatneveinek
 *
 * @author BT
 */
@Slf4j
public class JsonEntityNameToSnapshotMapper {

    /**
     *
     * @param dto
     *
     * @return
     */
    private Long getValue(ValueBaseDto dto) {
        if (dto instanceof QuantityValueDto) {

            return ((QuantityValueDto) dto).getCount();

        } else if (dto instanceof CurrentCountValueDto) {

            return ((CurrentCountValueDto) dto).getCurrent();

        } else if (dto instanceof CurrentObjectValueDto) {

            Object value = ((CurrentObjectValueDto) dto).getCurrent();
            if (value instanceof Number) {
                return (Long) value;
            }
        }

        return null;
    }

    /**
     * High WaterMark leszedése
     *
     * @param dto
     *
     * @return
     */
    private Long getHWValue(ValueBaseDto dto) {
        if (dto instanceof CurrentCountValueDto) {
            return ((CurrentCountValueDto) dto).getHighWatermark();
        }

        return null;
    }

    /**
     * Low watermark érték leszedése
     *
     * @param dto
     *
     * @return
     */
    private Long getLWValue(ValueBaseDto dto) {
        if (dto instanceof CurrentCountValueDto) {
            return ((CurrentCountValueDto) dto).getLowWatermark();
        }

        return null;
    }

    /**
     * REST monitor adatok megfeleltetése a Snapshot valamely attribútumának
     *
     * @param valuesMap
     * @param snapshot
     */
    public void map(HashMap<String, ValueBaseDto> valuesMap, Snapshot snapshot) {

        for (String entityname : valuesMap.keySet()) {
            ValueBaseDto dto = valuesMap.get(entityname);

            Long value = this.getValue(dto);

            switch (entityname) {
                //--- server/http-service/server/request
                case "countopenconnections":

                    if (RequestCollector.URI.equals(dto.getUri())) {
                        snapshot.setHttpreq_countopenconnections(value);
//                    } else if (ConnectionQueueCollector.URI.equals(dto.getUri())) {
//                        snapshot.setConq_countopenconnections(value);
                    }

                    break;

                case "errorcount":
                    snapshot.setHttpreq_errorcount(value);
                    break;

//                case "initheapsize-count":
//                    snapshot.setMem_initheapsizeCount(value);
//                    break;
//
//                case "maxheapsize-count":
//                    snapshot.setMem_maxheapsizeCount(value);
//                    break;
//
//                case "usedheapsize-count":
//                    snapshot.setMem_usedheapsizeCount(value);
//                    break;
//
//                case "countqueued":
//                    snapshot.setConq_countqueued(value);
//                    break;
//
//                case "peakqueued":
//                    snapshot.setConq_peakqueued(value);
//                    break;
//
//                case "currentthreadcount":
//                    snapshot.setThreadp_currthreadcount(value);
//                    break;
//
//                case "currentthreadsbusy":
//                    snapshot.setThreadp_currthreadsbusy(value);
//                    break;
//
//                case "processingtime":
//                    snapshot.setWebreq_processingtime(value);
//                    break;
//
//                case "activesessionscurrent":
//                    snapshot.setWebsess_actsesscurr(value);
//                    snapshot.setWebsess_actsesscurrHW(this.getHWValue(dto));
//                    break;
                default:
                //log.warn(String.format("Nincs a(z) %s entitás nevének mappere!", entityname));
            }
        }
    }

}
