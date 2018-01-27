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
     *
     * Core number of threads in the thread pool
     */
    @ColumnPosition(position = 20)
    private Long coreThreads;

    /**
     * • currentthreadcount
     *
     * Provides the number of request processing threads currently in the listener thread pool
     */
    @ColumnPosition(position = 21)
    private Long currentThreadCount;

    /**
     * • currentthreadsbusy
     *
     * Provides the number of request processing threads currently in use in the listener thread pool serving requests
     */
    @ColumnPosition(position = 22)
    private Long currentThreadsBusy;

    /**
     * • maxthreads
     *
     * Maximum number of threads allowed in the thread pool
     */
    @ColumnPosition(position = 23)
    private Long maxThreads;

    /**
     * • totalexecutedtasks
     *
     * Provides the total number of tasks, which were executed by the thread pool
     */
    @ColumnPosition(position = 24)
    private Long totalExecutedTasksCount;

}
