/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JSonEntityToSnapsotEntityMapper.java
 *  Created: 2017.12.25. 11:33:35
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.corelib.reflection.ReflectionUtils;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.snapshot.httpservice.HttpServiceRequest;
import hu.btsoft.gfmon.engine.model.entity.snapshot.jvm.JvmMemory;
import hu.btsoft.gfmon.engine.model.entity.snapshot.jvm.ThreadSystem;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1KeepAlive;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1ThreadPool;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener2ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener2KeepAlive;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener2ThreadPool;
import hu.btsoft.gfmon.engine.model.entity.snapshot.taservice.TransActionService;
import hu.btsoft.gfmon.engine.model.entity.snapshot.web.Jsp;
import hu.btsoft.gfmon.engine.model.entity.snapshot.web.Request;
import hu.btsoft.gfmon.engine.model.entity.snapshot.web.Servlet;
import hu.btsoft.gfmon.engine.model.entity.snapshot.web.Session;
import hu.btsoft.gfmon.engine.monitor.collector.MonitorValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.httpservice.HttpServiceRequestCollector;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.MemoryColletor;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.ThreadSystemCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1KeepAliveCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1ThreadPoolCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener2ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener2KeepAliveCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener2ThreadPoolCollector;
import hu.btsoft.gfmon.engine.monitor.collector.taservice.TransActionServiceColletor;
import hu.btsoft.gfmon.engine.monitor.collector.web.JspColletor;
import hu.btsoft.gfmon.engine.monitor.collector.web.RequestColletor;
import hu.btsoft.gfmon.engine.monitor.collector.web.ServletColletor;
import hu.btsoft.gfmon.engine.monitor.collector.web.SessionCollector;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * JsonEntitások+Eredmények -> Snapshot mapper
 * Ebben az osztályban szűrjük ki, hogy a töménytelen mérési eredmények közül valójában melyek érdekelnek minket
 *
 * @author BT
 */
@Slf4j
public class JSonEntityToSnapsotEntityMapper {

    /**
     * Mezők JPA és Serializable nélkül
     *
     * @param clazz osztály példány
     *
     * @return
     */
    private Set<Field> getAllFields(Class<?> clazz) {

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
     * @param clazz
     * @param fieldName
     *
     * @return
     */
    private Field getFieldByName(Set<Field> fields, String fieldName) {

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
    private Object typeSafeValueConverter(Class<?> fieldType, Object dtoValue) {

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
    private void fieldMapper(SnapshotBase jpaEntityRef, MonitorValueDto dto) {

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
                        case NANOSECOND:
                        case COUNT:
                        case BYTES:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCount()));
                            break;

                        case LIST:
                        case STRING:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCurrent()));
                            break;

                        case COUNT_CURLWHW:
                            field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getCurrent()));

                            //LowWatermark
                            Field _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.LOW_WATERMARK_VAR_POSTFX);
                            if (_field != null) {
                                _field.setAccessible(true);
                                _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getLowWatermark()));
                            }

                            //HighWatermark
                            _field = this.getFieldByName(fields, field.getName() + IGFMonEngineConstants.HIGH_WATERMARK_VAR_POSTFX);
                            if (_field != null) {
                                _field.setAccessible(true);
                                _field.set(jpaEntityRef, this.typeSafeValueConverter(fieldType, dto.getHighWatermark()));
                            }

                            break;

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

    /**
     * Map
     *
     * @param valuesList       mérési eredmények
     * @param snapshotEntities Snapshot JPA entitások halmaza, ebbe gyűjtjük a lementendő JPA entitásokat
     */
    void map(List<MonitorValueDto> valuesList, Set<SnapshotBase> snapshotEntities) {

        SnapshotBase snapshotEntity = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (MonitorValueDto dto : valuesList) {

            //Leszedjük a mért értéket
            String uri = dto.getUri();

            //A JPA entitás típusát attól függően hozzuk létre, hogy mely uri-ról származik a mérés
            switch (uri) {

                case HttpServiceRequestCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpServiceRequest();
                    }
                    break;

                case MemoryColletor.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new JvmMemory();
                    }
                    break;

                case ThreadSystemCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new ThreadSystem();
                    }
                    break;

                case ConnectionQueueCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new ConnectionQueue();
                    }
                    break;

                case HttpListener1ConnectionQueueCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener1ConnectionQueue();
                    }
                    break;

                case HttpListener1KeepAliveCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener1KeepAlive();
                    }
                    break;

                case HttpListener1ThreadPoolCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener1ThreadPool();
                    }
                    break;

                case HttpListener2ConnectionQueueCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener2ConnectionQueue();
                    }
                    break;

                case HttpListener2KeepAliveCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener2KeepAlive();
                    }
                    break;

                case HttpListener2ThreadPoolCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new HttpListener2ThreadPool();
                    }
                    break;

                case TransActionServiceColletor.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new TransActionService();
                    }
                    break;

                case JspColletor.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new Jsp();
                    }
                    break;

                case RequestColletor.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new Request();
                    }
                    break;

                case ServletColletor.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new Servlet();
                    }
                    break;

                case SessionCollector.URI:
                    if (snapshotEntity == null) {
                        snapshotEntity = new Session();
                    }
                    break;

            }

            if (snapshotEntity != null) {
                this.fieldMapper(snapshotEntity, dto);
            }
        }

        //Ha van eredmény, akkor az hozzáadjuk a halmazhoz
        if (snapshotEntity != null) {
            snapshotEntities.add(snapshotEntity);
        }

    }
}
