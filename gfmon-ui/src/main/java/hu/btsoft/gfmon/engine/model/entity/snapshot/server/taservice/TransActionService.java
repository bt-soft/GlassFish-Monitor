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
package hu.btsoft.gfmon.engine.model.entity.snapshot.server.taservice;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@Table(name = "SNOT_TASERVICE", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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
     *
     * Provides the number of transactions that are currently active
     */
    @ColumnPosition(position = 20)
    private Long activeCount;

    /**
     * • activeids
     *
     * Provides the IDs of the transactions that are currently active a.k.a. in-flight transactions.
     * Every such transaction can be rolled back after freezing the transaction service
     */
    @ColumnPosition(position = 21)
    @Size(max = STRING_FIELD_LENGHT, message = "Az activeIds mező hossza nem lehet nagyobb mint {max}")
    @Column(length = STRING_FIELD_LENGHT)
    private String activeIds;

    /**
     * • committedcount
     *
     * Provides the number of transactions that have been committed.
     */
    @ColumnPosition(position = 22)
    private Long committedCount;

    /**
     * • rolledbackcount
     *
     * Provides the number of transactions that have been rolled back.
     */
    @ColumnPosition(position = 23)
    private Long rolledbackCount;

    /**
     * • state
     *
     * Indicates if the transaction service has been frozen.
     */
    @ColumnPosition(position = 24)
    @Size(max = STRING_FIELD_LENGHT, message = "A state mező hossza nem lehet nagyobb mint {max}")
    @Column(length = STRING_FIELD_LENGHT)
    private String state;
}
