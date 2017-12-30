/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener1ConnectionQueue.java
 *  Created: 2017.12.27. 10:33:51
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.snapshot.network;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.snapshot.ConnectionQueueFieldsBase;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/network/http-listener-1/connection-queue
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_NET_HTTPL1CONNQ", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpListener1ConnectionQueue extends ConnectionQueueFieldsBase {

//    @Embedded
//    private FieldsConnectionQueue connectionQueueFields;
}
