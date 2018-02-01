/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ThreadPoolFieldsBase.java
 *  Created: 2017.12.27. 14:23:47
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
public abstract class ThreadPoolFieldsBase extends SnapshotBase {

    /**
     * • corethreads
     * <p>
     * Core number of threads in the thread pool
     */
    @Column(name = "CORE_THREADS")
    @ColumnPosition(position = 30)
    private Long coreThreads;

    /**
     * • currentthreadcount
     * <p>
     * Provides the number of request processing threads currently in the listener thread pool
     */
    @Column(name = "CURRENT_THREAD_COUNT")
    @ColumnPosition(position = 31)
    private Long currentThreadCount;

    /**
     * • currentthreadsbusy
     * <p>
     * Provides the number of request processing threads currently in use in the listener thread pool serving requests
     */
    @Column(name = "CURRENT_THREAD_BUSY")
    @ColumnPosition(position = 32)
    private Long currentThreadsBusy;

    /**
     * • maxthreads
     * <p>
     * Maximum number of threads allowed in the thread pool
     */
    @Column(name = "MAX_THREADS")
    @ColumnPosition(position = 33)
    private Long maxThreads;

    /**
     * • totalexecutedtasks
     * <p>
     * Provides the total number of tasks, which were executed by the thread pool
     */
    @Column(name = "TOTAL__EXECUTED_TASKS_COUNT")
    @ColumnPosition(position = 34)
    private Long totalExecutedTasksCount;

}
