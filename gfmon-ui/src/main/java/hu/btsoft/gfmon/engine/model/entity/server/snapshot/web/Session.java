/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    Session.java
 *  Created: 2017.12.27. 14:45:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.model.entity.server.snapshot.web;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.engine.model.entity.server.snapshot.SnapshotBase;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.eclipse.persistence.annotations.Customizer;

/**
 * JPA entitás
 * REST PATH: monitoring/domain/server/web/session
 *
 * @author BT
 */
@Entity
@Table(name = "SVR_WEB_SESSION", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Session extends SnapshotBase {

    /**
     * • activatedsessionstotal
     * <p>
     * Total number of sessions ever activated
     */
    @Column(name = "ACTIVATED_SESSIONS_TOTAL")
    @ColumnPosition(position = 30)
    private Long activatedSessionsTotal;

    /**
     * • activesessionscurrent (más a változó neve!!)
     * <p>
     * Number of active sessions
     */
    @Column(name = "ACTIVE_SESSIONS")
    @ColumnPosition(position = 31)
    private Long activeSessions;

    @Column(name = "ACTIVE_SESSIONS_LW")
    @ColumnPosition(position = 32)
    private Long activeSessionsLw;

    @Column(name = "ACTIVE_SESSIONS_HW")
    @ColumnPosition(position = 33)
    private Long activeSessionsHw;

    /**
     * • expiredsessionstotal
     * <p>
     * Total number of sessions ever expired
     */
    @Column(name = "EXPIRED_SESSIONS_TOTAL")
    @ColumnPosition(position = 34)
    private Long expiredSessionsTotal;

    /**
     * • passivatedsessionstotal
     * <p>
     * Total number of sessions ever passivated
     */
    @Column(name = "PASSIVATED_SESSIONS_TOTAL")
    @ColumnPosition(position = 35)
    private Long passivatedSessionsTotal;

    /**
     * • persistedsessionstotal
     * <p>
     * Total number of sessions ever persisted
     */
    @Column(name = "PERSISTED_SESSIONS_TOTAL")
    @ColumnPosition(position = 35)
    private Long persistedSessionsTotal;

    /**
     * • rejectedsessionstotal
     * <p>
     * Total number of sessions ever rejected
     */
    @Column(name = "REJECTED_SESSIONS_TOTAL")
    @ColumnPosition(position = 36)
    private Long rejectedSessionsTotal;

    /**
     * • sessionstotal
     * <p>
     * Total number of sessions ever created
     */
    @Column(name = "SESSIONS_TOTAL")
    @ColumnPosition(position = 37)
    private Long sessionsTotal;

}
