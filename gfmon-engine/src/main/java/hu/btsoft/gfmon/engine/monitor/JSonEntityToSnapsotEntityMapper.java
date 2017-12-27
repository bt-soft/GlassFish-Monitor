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
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import hu.btsoft.gfmon.engine.model.entity.snapshot.httpservice.HttpServiceRequest;
import hu.btsoft.gfmon.engine.model.entity.snapshot.jvm.JvmMemory;
import hu.btsoft.gfmon.engine.model.entity.snapshot.jvm.ThreadSystem;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1ConnectionQueue;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1KeepAlive;
import hu.btsoft.gfmon.engine.model.entity.snapshot.network.HttpListener1ThreadPool;
import hu.btsoft.gfmon.engine.monitor.collector.MonitorValueDto;
import hu.btsoft.gfmon.engine.monitor.collector.httpservice.HttpServiceRequestCollector;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.MemoryColletor;
import hu.btsoft.gfmon.engine.monitor.collector.jvm.ThreadSystemCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1ConnectionQueueCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1KeepAliveCollector;
import hu.btsoft.gfmon.engine.monitor.collector.network.HttpListener1ThreadPoolCollector;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

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

                    //Típushelyes érték leszedése
                    Object value = null;

                    Class<?> fieldType = field.getType();
                    if (Date.class == fieldType) {
                        value = new Date(dto.getCount());

                    } else if (Long.class == fieldType) {
                        value = dto.getCount();

                    } else if (String.class == fieldType) {
                        value = dto.getCurrent();
                    }

                    field.set(jpaEntityRef, value);

                    //Jöhet a következő mező!
                    continue;
                }

                if (dto.getLowWatermark() != null) { //lowwatermark leszedése - ha van
                    Field _field = this.getFieldByName(fields, field.getName() + "LW");
                    if (_field != null) {
                        _field.setAccessible(true);
                        _field.set(jpaEntityRef, dto.getLowWatermark());
                    }
                } else if (dto.getHighWatermark() != null) { //highwatermark leszedése - ha van
                    Field _field = this.getFieldByName(fields, field.getName() + "HW");
                    if (_field != null) {
                        _field.setAccessible(true);
                        _field.set(jpaEntityRef, dto.getHighWatermark());
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
     * @param valuesMap        mérési eredmények MAP
     * @param snapshotEntities Snapshot JPA entitások halmaza, ebbe gyűjtjük a lementendő JPA entitásokat
     */
    void map(HashMap<String /*Json entityName*/, MonitorValueDto> valuesMap, Set<SnapshotBase> snapshotEntities) {

        SnapshotBase snapshotEntity = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (String enityName : valuesMap.keySet()) {

            //Leszedjük a mért értéket
            MonitorValueDto dto = valuesMap.get(enityName);
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
