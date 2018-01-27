/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JSonEntityToSnapshotEntityMapperBase.java
 *  Created: 2017.12.25. 11:33:35
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.reflection.ReflectionUtils;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.EntityBase;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * JsonEntitások+Eredmények -> Snapshot mapper CDI bean
 * Ebben az osztályban szűrjük ki, hogy a töménytelen mérési eredmények közül valójában melyek érdekelnek minket
 * Ez attól függ, hogy az etitásban megtalálható-e az adot mért mező adatneve
 *
 * @author BT
 */
@Slf4j
public abstract class JSonEntityToSnapshotEntityMapperBase {

    /**
     * Mezők JPA és Serializable nélkül
     *
     * @param clazz osztály példány
     *
     * @return
     */
    protected Set<Field> getAllFields(Class<?> clazz) {

        Set<Field> fields = new HashSet<>();
        ReflectionUtils.getAllDeclaredFields(clazz, fields);

        //A JPA és a Serializable dolgait kitöröljük a halmazból
        for (Iterator<Field> i = fields.iterator(); i.hasNext();) {
            Field field = i.next();
            if (field.getName().startsWith("_persistence_") || field.getName().equals("serialVersionUID")) {
                i.remove();
            }
        }

        return fields;
    }

    /**
     * Memő kikeresése a név alapján
     *
     * @param fields    mezők
     * @param fieldName keresett mező
     *
     * @return
     */
    protected Field getFieldByName(Set<Field> fields, String fieldName) {

        Field result = null;
        for (Field field : fields) {

            if (fieldName.equals(field.getName())) {
                return field;
            }

        }

        return result;
    }

    /**
     * Típushelyes érték konverter
     * A mező típusának megfelelő típusra konvertáljuk az értéket
     * (pl.: Long -> Date, '%%%EOL%%%'-> '\n')
     *
     * @param fieldType JPA mező típusa
     * @param dtoValue  dto értéke
     *
     * @return JPA típushelyes érték
     */
    protected Object typeSafeValueConverter(Class<?> fieldType, Object dtoValue) {

        if (dtoValue == null) {
            return null;
        }

        //Típushelyes érték leszedése
        Object value = null;

        if (Date.class == fieldType) {
            value = new Date((Long) dtoValue);

        } else if (Long.class == fieldType) {
            value = (Long) dtoValue;

        } else if (String.class == fieldType) {
            String sValue = (String) dtoValue;
            value = !StringUtils.isEmpty(sValue) ? sValue.replaceAll("%%%EOL%%%", "\n") : null;

        }

        return value;
    }

    /**
     * Reflection API segítségével állítgatjuk a JPA entitások mezőit
     *
     * @param jpaEntityRef JPA entitás referenciája
     * @param dto          a mérés értéke
     */
    protected void fieldMapper(EntityBase jpaEntityRef, CollectedValueDto dto) {

        try {

            Set<Field> fields = getAllFields(jpaEntityRef.getClass());

            //Végigmegyünk az összes
            for (Field field : fields) {

                if (field.getName().equalsIgnoreCase(dto.getName())) {

                    //elérhetővé tesszük a privát mezőt
                    field.setAccessible(true);

                    //Érték típushelyes leszedése
                    Class<?> fieldType = field.getType();

                    switch (dto.getUnit()) {

                        case SECONDS:
                        case MILLISECOND:
                        case MILLISECONDS:
                        case NANOSECOND:
                        case COUNT:
                        case BYTES:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCount()));
                            break;

                        case LIST:
                        case STRING:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCurrent()));
                            break;

                        case COUNT_CURR_LW_HW_LB_UB: {
                            //lowerbound
                            Field _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.LOWERBOUND_VAR_POSTFIX);
                            if (_field != null) {
                                _field.setAccessible(true);
                                _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getLowerBound()));
                            }

                            //upperbound
                            _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.UPPERBOUND_VAR_POSTFIX);
                            if (_field != null) {
                                _field.setAccessible(true);
                                _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getUpperBound()));
                            }
                        }
                        //NINCS BREAK!!! -> csorogjon rá a COUNT_CURR_LW_HW-ra !

                        case COUNT_CURR_LW_HW:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCurrent()));
                             {
                                //LowWatermark
                                Field _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.LOW_WATERMARK_VAR_POSTFIX);
                                if (_field != null) {
                                    _field.setAccessible(true);
                                    _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getLowWatermark()));
                                }

                                //HighWatermark
                                _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.HIGH_WATERMARK_VAR_POSTFIX);
                                if (_field != null) {
                                    _field.setAccessible(true);
                                    _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getHighWatermark()));
                                }
                            }
                            break;

                        case COUNT_MT_MT_TT:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCount()));
                             {
                                //minTime
                                Field _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.MINTIME_VAR_POSTFIX);
                                if (_field != null) {
                                    _field.setAccessible(true);
                                    _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getMinTime()));
                                }

                                //maxTime
                                _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.MAXTIME_VAR_POSTFIX);
                                if (_field != null) {
                                    _field.setAccessible(true);
                                    _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getMaxTime()));
                                }
                                //totalTime
                                _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.TOTALTIME_VAR_POSTFIX);
                                if (_field != null) {
                                    _field.setAccessible(true);
                                    _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getTotalTime()));
                                }
                            }
                            break;

                        default:
                            log.warn("Nincs lekezelve a mapping, fieldName: '{}', unitType: {}!", field.getName(), dto.getUnit());
                    }
                }
            }

//            //A lastsampletime mező beállítása
//            if (dto.getLastSampleTime() != null) {
//                jpaEntityRef.setLastSampleTime(dto.getLastSampleTime());
//            }
        } catch (IllegalArgumentException | IllegalAccessException e) {
            log.error("Hiba a(z) '{}' osztály '{}' mezőjének mappingja során!\n", jpaEntityRef.getClass().getSimpleName(), dto.getName(), e);
        }

    }

}
