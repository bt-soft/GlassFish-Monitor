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
package hu.btsoft.gfmon.engine.measure;

import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentCountValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.CurrentObjectValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.QuantityValueDto;
import hu.btsoft.gfmon.engine.measure.collector.dto.ValueBaseDto;
import hu.btsoft.gfmon.engine.model.entity.Snapshot;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonEntitások+Eredmények -> Snapshot mapper
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
     * DTO -> Snapshot mapper - "http-service"
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
                snapshot.setHttpreqCountOpenConnections((Long) value);
                break;

            case "errorcount":
                snapshot.setHttpreqErrorCount((Long) value);
                break;
        }
    }

    /**
     * DTO -> Snapshot mapper - "network"
     *
     * @param enityName entitás neve
     * @param dto       mérési eredmények
     * @param snapshot  snapshot
     */
    private void networkMapper(String enityName, ValueBaseDto dto, Snapshot snapshot) {

        Object value = this.getDtoTypedValue(dto);

        switch (enityName) {
            case "countopenconnections":
                snapshot.setNetworkCountOpenConnections((Long) value);
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

        for (String enityName : valuesMap.keySet()) {

            ValueBaseDto dto = valuesMap.get(enityName);
            String monitoringServiceModuleName = dto.getMonitoringServiceModuleName();

            switch (monitoringServiceModuleName) {

                case "http-service":
                    httpServiceMapper(enityName, dto, snapshot);
                    break;

                case "network":
                    networkMapper(enityName, dto, snapshot);
                    break;
            }
        }
    }
}
