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
package hu.btsoft.gfmon.engine.model.entity.snapshot;

import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import hu.btsoft.gfmon.engine.model.entity.ColumnPosition;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/jvm/memory
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_JVMMEMORY", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class JvmMemory extends SnapshotBase {

    /**
     * • committedheapsize-count
     *
     * Amount of memory in bytes that is committed for the Java virtual machine to use
     */
    @ColumnPosition(position = 20)
    private Long committedHeapSize;

    /**
     * • committednonheapsize-count
     *
     * Amount of memory in bytes that is committed for the Java virtual machine to use
     */
    @ColumnPosition(position = 21)
    private Long committedNonHeapSize;

    /**
     * • initheapsize-count
     *
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    @ColumnPosition(position = 22)
    private Long initialHeapSize;

    /**
     * • initnonheapsize-count
     *
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    @ColumnPosition(position = 23)
    private Long initialNonHeapSize;

    /**
     * • maxheapsize-count
     *
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @ColumnPosition(position = 24)
    private Long maxHeapSize;

    /**
     * • maxnonheapsize-count
     *
     * Maximum amount of memory in bytes that can be used for memory management
     */
    @ColumnPosition(position = 25)
    private Long maxNonHeapSize;

    /**
     * • objectpendingfinalizationcount-count
     *
     * Approximate number of objects for which finalization is pending
     */
    @ColumnPosition(position = 26)
    private Long objectsPendingFinalization;

    /**
     * • usedheapsize-count
     *
     * Amount of used memory in bytes
     */
    @ColumnPosition(position = 27)
    private Long usedHeapSize;

    /**
     * • usednonheapsize-count
     *
     * Amount of used memory in bytes
     */
    @ColumnPosition(position = 28)
    private Long usedNonHeapSize;
}
