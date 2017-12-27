/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener1KeepAlive.java
 *  Created: 2017.12.27. 10:41:20
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.snapshot.network;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.ColumnPosition;
import hu.btsoft.gfmon.engine.model.entity.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
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
 * REST PATH: monitoring/domain/server/network/http-listener-1/keep-alive
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_NET_HTTPL1KEEPALVE", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpListener1KeepAlive extends SnapshotBase {

    /**
     * • countconnections
     *
     * Number of connections in keep-alive mode
     */
    @ColumnPosition(position = 20)
    private Long countConnections;

    /**
     * • countflushes
     *
     * Number of keep-alive connections that were closed
     */
    @ColumnPosition(position = 21)
    private Long countFlushes;

    /**
     * • counthits
     *
     * Number of requests received by connections in keep-alive mode
     */
    @ColumnPosition(position = 22)
    private Long countHits;

    /**
     * • countrefusals
     *
     * Number of keep-alive connections that were rejected
     */
    @ColumnPosition(position = 23)
    private Long countRefusals;

    /**
     * • counttimeouts
     *
     * Number of keep-alive connections that timed out
     */
    @ColumnPosition(position = 24)
    private Long countTimeouts;

    /**
     * • maxrequests
     *
     * Maximum number of requests allowed on a single keep-alive connection
     */
    @ColumnPosition(position = 25)
    private Long maxRequests;

    /**
     * • secondstimeouts
     *
     * Keep-alive timeout value in seconds
     */
    @ColumnPosition(position = 26)
    private Long secondsTimeouts;

}
