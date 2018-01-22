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
     * Egy Entitás felépítése a mérési eredményekből
     * A valuesList egyenként egy mezőt tatalmaz, ezt kell mappalni egy entitásban
     *
     * @param valuesList mérési eredmények
     *
     * @return új entitás
     */
    public AppSnapshotBase map(List<CollectedValueDto> valuesList) {

        if (valuesList == null || valuesList.isEmpty()) {
            return null;
        }

        AppSnapshotBase snapshotEntity = null;
        Class<? extends AppSnapshotBase> jpaEntityClass = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (CollectedValueDto dto : valuesList) {

            //A JPA entitás típusát attól függően hozzuk létre, hogy mely uri-ról származik a mérés
            if (jpaEntityClass == null) {
                jpaEntityClass = AppRestPathToAppJpaEntityClassMap.getJpaEntityClass(dto.getPath());
            }

            if (jpaEntityClass != null) {
                try {
                    if (snapshotEntity == null) {
                        snapshotEntity = (AppSnapshotBase) jpaEntityClass.newInstance();
                    }

                    //DTO -> JPA entitás map
                    this.fieldMapper(snapshotEntity, dto);

                    snapshotEntity.setPathSuffix(dto.getPath());

                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Nem lehet létrehozni az entitás példányt!", e);
                }
            }

        }

        return snapshotEntity;
    }
}
