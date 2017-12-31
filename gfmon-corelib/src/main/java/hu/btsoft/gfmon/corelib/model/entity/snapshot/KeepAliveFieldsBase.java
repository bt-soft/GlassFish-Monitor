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
package hu.btsoft.gfmon.corelib.model.entity.snapshot;

import hu.btsoft.gfmon.corelib.model.entity.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.entity.EntityColumnPositionCustomizer;
import javax.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.eclipse.persistence.annotations.Customizer;

/**
 * keep-alive mezők
 *
 * @author BT
 */
//@Embeddable
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class KeepAliveFieldsBase extends SnapshotBase {

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
