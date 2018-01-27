/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ThreadSystem.java
 *  Created: 2017.12.27. 10:15:19
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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/jvm/thread-system
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_THRDSYS", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class ThreadSystem extends SnapshotBase {

    private final static int STRING_FIELD_LENGHT = 32_672;

    /**
     * • allthreadids
     * <p>
     * Returns all live thread IDs
     */
    @ColumnPosition(position = 20)
    @Size(max = STRING_FIELD_LENGHT, message = "A liveThreads mező hossza nem lehet nagyobb mint {max}")
    @Column(length = STRING_FIELD_LENGHT)
    private String liveThreads;

    /**
     * • currentthreadcputime
     * <p>
     * Returns the total CPU time for the current thread in nanoseconds
     */
    @ColumnPosition(position = 21)
    private Long currentThreadCpuTime;

    /**
     * • currentthreadusertime
     * <p>
     * Returns the CPU time that the current thread has executed in user mode in nanoseconds
     */
    @ColumnPosition(position = 22)
    private Long currentThreadUserTime;

    /**
     * • daemonthreadcount
     * <p>
     * Returns the current number of live daemon threads
     */
    @ColumnPosition(position = 23)
    private Long daemonThreadCount;

    /**
     * • deadlockedthreads
     * <p>
     * Finds cycles of threads that are in deadlock waiting to acquire object monitors or ownable synchronizers
     */
    @ColumnPosition(position = 24)
    @Size(max = STRING_FIELD_LENGHT, message = "A DeadlockedThreads mező hossza nem lehet nagyobb mint {max}")
    @Column(length = STRING_FIELD_LENGHT)
    private String deadlockedThreads;

    /**
     * • monitordeadlockedthreads
     * <p>
     * Finds cycles of threads that are in deadlock waiting to acquire object monitors
     */
    @ColumnPosition(position = 25)
    @Size(max = STRING_FIELD_LENGHT, message = "A monitorDeadlockedThreads mező hossza nem lehet nagyobb mint {max}")
    @Column(length = STRING_FIELD_LENGHT)
    private String monitorDeadlockedThreads;

    /**
     * • peakthreadcount
     * <p>
     * Returns the peak live thread count since the Java virtual machine started or peak was reset
     */
    @ColumnPosition(position = 26)
    private Long peakThreadCount;

    /**
     * • threadcount
     * <p>
     * Returns the current number of live threads including both daemon and non-daemon threads
     */
    @ColumnPosition(position = 27)
    private Long threadCount;

    /**
     * • totalstartedthreadcount
     * <p>
     * Returns the total number of threads created and also started since the Java virtual machine started
     */
    @ColumnPosition(position = 28)
    private Long totalStartedThreadCount;

    /**
     * A szöveges változókat töröljük, ha lehet
     */
    @PrePersist
    @Override
    protected void prePersist() {
        super.prePersist();

        if (!StringUtils.isEmpty(deadlockedThreads)) {
            if ("None of the threads are deadlocked.".equals(deadlockedThreads)) {
                deadlockedThreads = null;
            }
        }

        if (!StringUtils.isEmpty(monitorDeadlockedThreads)) {
            if ("None of the threads are monitor deadlocked.".equals(monitorDeadlockedThreads)) {
                monitorDeadlockedThreads = null;
            }
        }

    }

}
