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

import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SvrSnapshotBase;
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
public class JSonEntityToServerSnapshotEntityMapper extends JSonEntityToSnapshotEntityMapperBase {

    /**
     * Egy Entitás felépítése a mérési eredményekből
     * A valuesList egyenként egy mezőt tatalmaz, ezt kell mappalni egy entitásban
     *
     * @param valuesList       mérési eredmények
     * @param snapshotEntities Snapshot JPA entitások halmaza, ebbe gyűjtjük a lementendő JPA entitásokat
     */
    public void map(List<CollectedValueDto> valuesList, Set<SvrSnapshotBase> snapshotEntities) {

        SvrSnapshotBase snapshotEntity = null;
        Class<? extends SvrSnapshotBase> jpaEntityClass = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (CollectedValueDto dto : valuesList) {

            //A JPA entitás típusát attól függően hozzuk létre, hogy mely uri-ról származik a mérés
            if (jpaEntityClass == null) {
                //Leszedjük a mért értéket
                jpaEntityClass = SvrRestPathToSvrJpaEntityClassMap.getJpaEntityClass(dto.getPath());
            }

            if (jpaEntityClass != null) {
                try {
                    if (snapshotEntity == null) {
                        snapshotEntity = (SvrSnapshotBase) jpaEntityClass.newInstance();
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
