/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    HttpListener1ThreadPool.java
 *  Created: 2017.12.27. 10:50:58
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
 * REST PATH: monitoring/domain/server/network/connection-queue
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_NET_HTTPL1THRDPOOL", catalog = "", schema = IGFMonEngineConstants.DATABASE_SCHEMAN_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class HttpListener1ThreadPool extends SnapshotBase {

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
