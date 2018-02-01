/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    JvmMemory.java
 *  Created: 2017.12.26. 13:30:29
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot.jvm;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/jvm/memory
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_JVM_MEMORY", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class JvmMemory extends SnapshotBase {

    /**
     * • committedheapsize-count
     * <p>
     * Amount of memory in bytes that is committed for the Java virtual machine to use
     */
    @Column(name = "COMMITTED_HEAP_SIZE")
    @ColumnPosition(position = 30)
    private Long committedHeapSize;

    /**
     * • committednonheapsize-count
     * <p>
     * Amount of memory in bytes that is committed for the Java virtual machine to use
     */
    @Column(name = "COMMITTED_NON_HEAP_SIZE")
    @ColumnPosition(position = 31)
    private Long committedNonHeapSize;

    /**
     * • initheapsize-count
     * <p>
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    @Column(name = "INITIAL_HEAP_SIZE")
    @ColumnPosition(position = 32)
    private Long initialHeapSize;

    /**
     * • initnonheapsize-count
     * <p>
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    @Column(name = "INITIAL_NON_HEAP_SIZE")
    @ColumnPosition(position = 33)
    private Long initialNonHeapSize;

    /**
     * • maxheapsize-count
     * <p>
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @Column(name = "MAX_HEAP_SIZE")
    @ColumnPosition(position = 34)
    private Long maxHeapSize;

    /**
     * • maxnonheapsize-count
     * <p>
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @Column(name = "MAX_NON_HEAP_SIZE")
    @ColumnPosition(position = 35)
    private Long maxNonHeapSize;

    /**
     * • objectpendingfinalizationcount-count
     * <p>
     * Approximate number of objects for which finalization is pending
     */
    @Column(name = "OBJECTS_PENDING_FINALIZATION")
    @ColumnPosition(position = 36)
    private Long objectsPendingFinalization;

    /**
     * • usedheapsize-count
     * <p>
     * Amount of used memory in bytes
     */
    @Column(name = "USED_HEAP_SIZE")
    @ColumnPosition(position = 37)
    private Long usedHeapSize;

    /**
     * • usednonheapsize-count
     * <p>
     * Amount of used memory in bytes
     */
    @Column(name = "USED_NON_HEAP_SIZE")
    @ColumnPosition(position = 38)
    private Long usedNonHeapSize;
}
