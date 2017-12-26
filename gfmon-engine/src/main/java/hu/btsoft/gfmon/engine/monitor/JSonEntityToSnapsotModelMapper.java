/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JSonEntityToSnapsotModelMapper.java
 *  Created: 2017.12.25. 11:33:35
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.model.entity.Snapshot;
import hu.btsoft.gfmon.engine.monitor.collector.dto.CurrentCountValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.dto.ValueBaseDto;
import hu.btsoft.gfmon.engine.monitor.collector.httpservice.RequestCollector;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.MemoryColletor;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.ThreadSystemCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1KeepAliveCollector;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonEntitások+Eredmények -> Snapshot mapper
 * Ebben az osztályban szűrjük ki, hogy a töménytelen mérési eredmények közül valójában melyek érdekelnek minket
 *
 * @author BT
 */
@Slf4j
public class JSonEntityToSnapsotModelMapper {

//    private ModelMapper modelMapper;
//
//    //PropertyMap<Snapshot, HashMap<String /*Json entityName*/, ValueBaseDto>> propertyMap;
//
//    /**
//     * ModelMapper felhúzása
//     */
//    @PostConstruct
//    protected void init() {
//
//        //--- ModelMapper conditions
//        //A "http-service" modulból jött az adat?
//        Condition isModuleHttpService = ctx -> "http-service".equals(((ValueBaseDto) ctx.getSource()).getMonitoringServiceModuleName());
//
//        //QuantityValueDto a forrás?
//        Condition isQuantityValueDto = ctx -> ctx.getSource() instanceof QuantityValueDto;
//        //CurrentCountValueDto a forrás?
//        Condition isCurrentCountValueDto = ctx -> ctx.getSource() instanceof CurrentCountValueDto;
//        //CurrentObjectValueDto a forrás?
//        Condition isCurrentObjectValueDto = ctx -> ctx.getSource() instanceof CurrentObjectValueDto;
//
//        //--- ModelMapper converters
//        Converter<ValueBaseDto, Long> value2Long = new AbstractConverter<ValueBaseDto, Long>() {
//            @Override
//            protected Long convert(ValueBaseDto dto) {
//                if (dto instanceof QuantityValueDto) {
//                    return ((QuantityValueDto) dto).getCount();
//                } else if (dto instanceof CurrentCountValueDto) {
//                    return ((CurrentCountValueDto) dto).getCurrent();
//                } else if (dto instanceof CurrentObjectValueDto) {
//                    Object value = ((CurrentObjectValueDto) dto).getCurrent();
//                    if (value instanceof Number) {
//                        return (Long) value;
//                    }
//                }
//                return null;
//            }
//        };
//
//        //--- ModelMapper TypeMappers
//        TypeMap<ValueBaseDto, Snapshot> baseDtoTypeMap = modelMapper.createTypeMap(ValueBaseDto.class, Snapshot.class);
//        //Ha a "http-service"-ből jön az adat -> RequestCollector szolgáltatja
//        baseDtoTypeMap.addMappings(mapper -> mapper.when(isModuleHttpService).map(src -> this.getDtoNumericValue(src), (dest, v) -> dest.setHttpreqCountOpenConnections((Long) v)));
//
//    }
//
//
    /**
     * A DTO értékének típushelyes leszedése
     *
     * @param dto dto példány
     *
     * @return null, String vagy a long érték
     */
    private Object getDtoTypedValue(ValueBaseDto dto) {

        if (dto instanceof QuantityValueDto) {
            return ((QuantityValueDto) dto).getCount();

        } else if (dto instanceof CurrentCountValueDto) {
            return ((CurrentCountValueDto) dto).getCurrent();

        } else if (dto instanceof CurrentObjectValueDto) {

            Object value = ((CurrentObjectValueDto) dto).getCurrent();

            if (value instanceof Number) {
                return (Long) value;
            }

            //Ha nem Number, akkor String lesz
            return (String) value;
        }

        return null;
    }

    /**
     * DTO -> Snapshot mapper - "server/http-service/server/request"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void httpServiceMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            //--- server/http-service/server/request
            case "countopenconnections":
                snapshot.setHttpreqOpenConnectionsCnt((Long) value);
                break;

            case "errorcount":
                snapshot.setHttpreqErrorCnt((Long) value);
                break;

//            case "maxopenconnections":
//                snapshot.setHttpreqMaxOpenConnectionsCnt((Long) value);
//                break;
//
            case "maxtime":
                snapshot.setHttpreqMaxTime((Long) value);
                break;

            case "processingtime":
                snapshot.setHttpreqProcTime((Long) value);
                break;
        }
    }

    /**
     * DTO -> Snapshot mapper - "jvm/memory"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void memoryMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "initheapsize-count":
                snapshot.setInitHeapSizeCnt((Long) value);
                break;

            case "maxheapsize-count":
                snapshot.setMaxHeapSizeCnt((Long) value);
                break;

            case "usedheapsize-count":
                snapshot.setUsedHeapSizeCnt((Long) value);
                break;
        }
    }

    /**
     * DTO -> Snapshot mapper - "jvm/thread-system"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void threadSystemMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "daemonthreadcount":
                snapshot.setDeamonThreadCnt((Long) value);
                break;

            case "peakthreadcount":
                snapshot.setDeamonThreadPeak((Long) value);
                break;

            case "threadcount":
                snapshot.setThreadCount((Long) value);
                break;

            case "totalstartedthreadcount":
                snapshot.setTotalStartedThreadCount((Long) value);
                break;

        }
    }

    /**
     * DTO -> Snapshot mapper - "network/connection-queue"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void connQueMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "countopenconnections":
                snapshot.setNetworkOpenConnectionsCnt((Long) value);
                break;

            case "countqueued":
                snapshot.setNetworkQueuedConnectionsCnt((Long) value);
                break;

            case "peakqueued":
                snapshot.setNetworkQueuedConnectionsCnt((Long) value);
                break;
        }
    }

    /**
     * DTO -> Snapshot mapper - "network/http-listener-1/connection-queue"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void httpListener1ConnQueMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "countopenconnections":
                snapshot.setHttpListener1OpenConnectionsCnt((Long) value);
                break;

            case "countqueued":
                snapshot.setHttpListener1QueuedConnectionsCnt((Long) value);
                break;

            case "peakqueued":
                snapshot.setHttpListener1QueuedConnectionsPeak((Long) value);
                break;
        }
    }

    /**
     * DTO -> Snapshot mapper - "network/http-listener-1/keep-alive"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void httpListener1KeepAlive(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "countconnections":
                snapshot.setHttpListener1KeepAliveConnections((Long) value);
                break;

        }
    }

    /**
     * Map
     *
     * @param valuesMap mérési eredmények MAP
     * @param snapshot  Snapshot entitás (új, még nincs lementve)
     */
    void map(HashMap<String /*Json entityName*/, ValueBaseDto> valuesMap, Snapshot snapshot) {

        //Végigmegyünk az összes mért JSon intitáson
        for (String enityName : valuesMap.keySet()) {

            //Leszedjük a mért értéket
            ValueBaseDto dto = valuesMap.get(enityName);
            String uri = dto.getUri();

            //Attól függően, hogy mely uri-ról származik, a mappereket külön-külön hívjuk meg
            switch (uri) {

                case RequestCollector.URI:
                    httpServiceMapper(enityName, dto, snapshot);
                    break;

                case MemoryColletor.URI:
                    memoryMapper(enityName, dto, snapshot);
                    break;

                case ThreadSystemCollector.URI:
                    threadSystemMapper(enityName, dto, snapshot);
                    break;

                case ConnectionQueueCollector.URI:
                    connQueMapper(enityName, dto, snapshot);
                    break;

                case HttpListener1ConnectionQueueCollector.URI:
                    httpListener1ConnQueMapper(enityName, dto, snapshot);
                    break;

                case HttpListener1KeepAliveCollector.URI:
                    httpListener1KeepAlive(enityName, dto, snapshot);
                    break;

            }
        }
    }
}
