/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener2ThreadPool.java
 *  Created: 2017.12.27. 10:50:58
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.entity.snapshot.network;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.corelib.model.entity.snapshot.ThreadPoolFieldsBase;
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
 * REST PATH: monitoring/domain/server/network/connection-queue
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_NET_HTTPL2THRDPOOL", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpListener2ThreadPool extends ThreadPoolFieldsBase {

}
