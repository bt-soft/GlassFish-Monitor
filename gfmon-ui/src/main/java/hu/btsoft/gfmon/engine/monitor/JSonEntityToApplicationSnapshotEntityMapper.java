/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JSonEntityToApplicationSnapshotEntityMapper.java
 *  Created: 2017.12.25. 11:33:35
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor;

import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import hu.btsoft.gfmon.engine.monitor.collector.CollectedValueDto;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * JsonEntitások+Eredmények -> Snapshot mapper CDI bean
 * Ebben az osztályban szűrjük ki, hogy a töménytelen mérési eredmények közül valójában melyek érdekelnek minket
 *
 * @author BT
 */
@Slf4j
public class JSonEntityToApplicationSnapshotEntityMapper extends JSonEntityToSnapshotEntityMapperBase {

    /**
     * Map
     *
     * @param valuesList       mérési eredmények
     * @param snapshotEntities Snapshot JPA entitások halmaza, ebbe gyűjtjük a lementendő JPA entitásokat
     */
    public void map(List<CollectedValueDto> valuesList, Set<AppSnapshotBase> snapshotEntities) {

        AppSnapshotBase snapshotEntity = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (CollectedValueDto dto : valuesList) {

            //Leszedjük a mért értéket
            String path = dto.getPath();

            //A JPA entitás típusát attól függően hozzuk létre, hogy mely uri-ról származik a mérés
            Class<? extends AppSnapshotBase> jpaEntityClass = AppRestPathToAppJpaEntityClassMap.getJpaEntityClass(path);

            if (jpaEntityClass != null) {
                try {
                    if (snapshotEntity == null) {
                        snapshotEntity = (AppSnapshotBase) jpaEntityClass.newInstance();
                    }

                    //DTO -> JPA entitás map
                    this.fieldMapper(snapshotEntity, dto);

                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Nem lehet létrehozni az entitás példányt!", e);
                }
            }
        }

        //Ha van eredmény, akkor az hozzáadjuk a mérési halmazhoz
        if (snapshotEntity != null) {
            snapshotEntities.add(snapshotEntity);
        }
    }
}
