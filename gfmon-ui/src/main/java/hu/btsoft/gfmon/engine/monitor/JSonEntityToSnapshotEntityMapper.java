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

import hu.btsoft.gfmon.engine.model.entity.EntityBase;
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
public class JSonEntityToSnapshotEntityMapper extends JSonEntityToSnapshotEntityMapperBase {

    /**
     * Egy Entitás felépítése a mérési eredményekből
     * A valuesList egyenként egy mezőt tatalmaz, ezt kell mappalni egy entitásban
     *
     * @param valuesList mérési eredmények
     *
     * @return adatokkal feltöltött JPA entitás, vagy null
     */
    public EntityBase map(List<CollectedValueDto> valuesList) {

        if (valuesList == null || valuesList.isEmpty()) {
            return null;
        }

        EntityBase jpaEntity = null;
        Class<? extends EntityBase> jpaEntityClass = null;

        //Végigmegyünk az összes mért JSon entitáson
        for (CollectedValueDto dto : valuesList) {

            //A JPA entitás típusát attól függően hozzuk létre, hogy mely uri-ról származik a mérés
            if (jpaEntityClass == null) {
                //Leszedjük a mért értéket
                jpaEntityClass = RestPathToJpaEntityClassMap.getJpaEntityClass(dto.getPath());
            }

            if (jpaEntityClass != null) {
                try {
                    if (jpaEntity == null) {
                        jpaEntity = (EntityBase) jpaEntityClass.newInstance();
                    }

                    //DTO -> JPA entitás adat feltöltés
                    this.fieldMapper(jpaEntity, dto);

                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("Nem lehet létrehozni az entitás példányt!", e);
                }
            }

        }
        return jpaEntity;
    }
}
