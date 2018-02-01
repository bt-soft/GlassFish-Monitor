/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    KeepAliveFieldsBase.java
 *  Created: 2017.12.27. 14:20:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot;

import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * keep-alive mezők
 * (Még nem találtam meg, hogy hogyan lehet az @Embeddable entitás mezőit reflectionnal feltérképezni, emiatt a sima abstract osztály implemnetáció
 *
 * @author BT
 */
//@Embeddable
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public abstract class KeepAliveFieldsBase extends SnapshotBase {

    /**
     * • countconnections
     * <p>
     * Number of connections in keep-alive mode
     */
    @Column(name = "COUNT_CONNECTIONS")
    @ColumnPosition(position = 30)
    private Long countConnections;

    /**
     * • countflushes
     * <p>
     * Number of keep-alive connections that were closed
     */
    @Column(name = "COUNT_FLUSHES")
    @ColumnPosition(position = 31)
    private Long countFlushes;

    /**
     * • counthits
     * <p>
     * Number of requests received by connections in keep-alive mode
     */
    @Column(name = "COUNT_HITS")
    @ColumnPosition(position = 32)
    private Long countHits;

    /**
     * • countrefusals
     * <p>
     * Number of keep-alive connections that were rejected
     */
    @Column(name = "COUNT_REFUSALS")
    @ColumnPosition(position = 33)
    private Long countRefusals;

    /**
     * • counttimeouts
     * <p>
     * Number of keep-alive connections that timed out
     */
    @Column(name = "COUNT_TIMEOUTS")
    @ColumnPosition(position = 34)
    private Long countTimeouts;

    /**
     * • maxrequests
     * <p>
     * Maximum number of requests allowed on a single keep-alive connection
     */
    @Column(name = "MAX_REQUESTS")
    @ColumnPosition(position = 35)
    private Long maxRequests;

    /**
     * • secondstimeouts
     * <p>
     * Keep-alive timeout value in seconds
     */
    @Column(name = "SECONDS_TIMEOUTS")
    @ColumnPosition(position = 36)
    private Long secondsTimeouts;

}
