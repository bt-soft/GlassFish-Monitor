/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    TransActionService.java
 *  Created: 2017.12.27. 14:30:44
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot.taservice;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/transaction-service
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_TRANSACT_SERVICE", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class TransActionService extends SnapshotBase {

    private final static int STRING_FIELD_LENGHT = 2048;

    /**
     * • activecount
     * <p>
     * Provides the number of transactions that are currently active
     */
    @Column(name = "ACTIVE_COUNT")
    @ColumnPosition(position = 30)
    private Long activeCount;

    /**
     * • activeids
     * <p>
     * Provides the IDs of the transactions that are currently active a.k.a. in-flight transactions.
     * Every such transaction can be rolled back after freezing the transaction service
     */
    @Size(max = STRING_FIELD_LENGHT, message = "Az activeIds mező hossza nem lehet nagyobb mint {max}")
    @Column(name = "ACTIVE_IDS", length = STRING_FIELD_LENGHT)
    @ColumnPosition(position = 31)
    private String activeIds;

    /**
     * • committedcount
     * <p>
     * Provides the number of transactions that have been committed.
     */
    @Column(name = "COMMITTED_COUNT")
    @ColumnPosition(position = 32)
    private Long committedCount;

    /**
     * • rolledbackcount
     * <p>
     * Provides the number of transactions that have been rolled back.
     */
    @Column(name = "ROLLEDBACK_COUNT")
    @ColumnPosition(position = 33)
    private Long rolledbackCount;

    /**
     * • state
     * <p>
     * Indicates if the transaction service has been frozen.
     */
    @Size(max = STRING_FIELD_LENGHT, message = "A state mező hossza nem lehet nagyobb mint {max}")
    @Column(name = "TRANSACT_STATE", length = STRING_FIELD_LENGHT)
    @ColumnPosition(position = 34)
    private String state;
}
