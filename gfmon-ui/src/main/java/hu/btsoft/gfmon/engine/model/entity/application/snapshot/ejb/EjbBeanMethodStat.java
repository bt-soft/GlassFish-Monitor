/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbBeanMethodStat.java
 *  Created: 2018.01.26. 12:39:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 *
 * @author BT
 */
@Entity
@Table(name = "APP_EJBMETHOD_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Data
@ToString(callSuper = true, exclude = {"ejbStat"})
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbBeanMethodStat extends AppSnapshotBase {

    /**
     * Az EJB metódus statisztika melyik EJB-hez tartozik?
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APP_EJBSTAT_ID", referencedColumnName = "ID", nullable = false)
    @ColumnPosition(position = 11)
    private EjbStat ejbStat;

    /**
     * Az EJB neve
     */
    @NotNull(message = "A methodName nem lehet null")
    @Size(min = 3, max = 255, message = "A methodName mező hossza {min} és {max} között lehet")
    @Column(name = "METHOD_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 19)
    private String methodName;

    /**
     * • ExecutionTime
     * <p>
     * Provides the time in milliseconds spent during the last successful/unsuccessful attempt to execute the operation.
     */
    @ColumnPosition(position = 20)
    private Long executionTime;

    /**
     * • MethodStatistic
     * <p>
     * Provides the number of times an operation was called, the total time that was spent during the invocation and so on
     */
    @ColumnPosition(position = 20)
    private Long methodStatistic;

    /**
     * Mintime
     */
    @ColumnPosition(position = 21)
    private Long methodStatisticTmin;

    /**
     * MaxTime
     */
    @ColumnPosition(position = 22)
    private Long methodStatisticTmax;

    /**
     * TotoalTime
     */
    @ColumnPosition(position = 23)
    private Long methodStatisticTtot;

    /**
     * • TotalNumErrors
     * <p>
     * Provides the total number of errors that occured during invocation or execution of an operation.
     */
    @ColumnPosition(position = 24)
    private Long totalNumErrors;

    /**
     * • TotalNumSuccess
     * <p>
     * Provides the total number of successful invocations of the method.
     */
    @ColumnPosition(position = 25)
    private Long totalNumSuccess;

}
