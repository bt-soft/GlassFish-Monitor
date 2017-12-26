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
import hu.btsoft.gfmon.engine.monitor.collector.network.ConnectionQueueCollector;
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
        }
    }

    /**
     * DTO -> Snapshot mapper - "network/connection-queue"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void networkMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "countopenconnections":
                snapshot.setNetworkOpenConnectionsCnt((Long) value);
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

                case ConnectionQueueCollector.URI:
                    networkMapper(enityName, dto, snapshot);
                    break;

                case MemoryColletor.URI:
                    memoryMapper(enityName, dto, snapshot);
                    break;
            }
        }
    }
}
