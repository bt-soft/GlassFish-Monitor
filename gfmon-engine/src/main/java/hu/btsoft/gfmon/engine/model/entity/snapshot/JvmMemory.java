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
    private Long committedHeapSize;

    /**
     * • committednonheapsize-count
     *
     * Amount of memory in bytes that is committed for the Java virtual machine to use
     */
    private Long committedNonHeapSize;

    /**
     * • initheapsize-count
     *
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    private Long initialHeapSize;

    /**
     * • initnonheapsize-count
     *
     * Amount of memory in bytes that the Java virtual machine initially requests from the operating system for memory management
     */
    private Long initialNonHeapSize;

    /**
     * • maxheapsize-count
     *
     * Maximum amount of memory in bytes that can be used for memory management
     */
    private Long maxHeapSize;

    /**
     * • maxnonheapsize-count
     *
     * Maximum amount of memory in bytes that can be used for memory management
     */
    private Long maxNonHeapSize;

    /**
     * • objectpendingfinalizationcount-count
     *
     * Approximate number of objects for which finalization is pending
     */
    private Long objectsPendingFinalization;

    /**
     * • usedheapsize-count
     *
     * Amount of used memory in bytes
     */
    private Long usedHeapSize;

    /**
     * • usednonheapsize-count
     *
     * Amount of used memory in bytes
     */
    private Long usedNonHeapSize;

}
