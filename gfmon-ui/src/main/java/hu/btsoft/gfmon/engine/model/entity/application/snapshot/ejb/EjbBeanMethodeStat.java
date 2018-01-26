/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EjbBeanMethodeStat.java
 *  Created: 2018.01.26. 12:39:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.application.snapshot.ejb;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.application.snapshot.AppSnapshotBase;
import javax.persistence.Cacheable;
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
@Table(name = "APP_EJBMETHODE_STAT", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, exclude = {"ejbStat"})
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class EjbBeanMethodeStat extends AppSnapshotBase {

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
    @NotNull(message = "Az methodeName nem lehet null")
    @Size(min = 3, max = 255, message = "Az methodeName mező hossza {min} és {max} között lehet")
    @Column(name = "METHODE_NAME", length = 255, nullable = false)
    @ColumnPosition(position = 19)
    private String methodeName;

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
     * • TotalNumErrors
     * <p>
     * Provides the total number of errors that occured during invocation or execution of an operation.
     */
    @ColumnPosition(position = 20)
    private Long totalNumErrors;

    /**
     * • TotalNumSuccess
     * <p>
     * Provides the total number of successful invocations of the method.
     */
    @ColumnPosition(position = 20)
    private Long totalNumSuccess;

}
