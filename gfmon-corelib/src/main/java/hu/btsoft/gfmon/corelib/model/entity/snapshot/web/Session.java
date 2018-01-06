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
package hu.btsoft.gfmon.corelib.model.entity.snapshot.web;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;
import hu.btsoft.gfmon.corelib.model.colpos.ColumnPosition;
import hu.btsoft.gfmon.corelib.model.colpos.EntityColumnPositionCustomizer;
import hu.btsoft.gfmon.corelib.model.entity.snapshot.SnapshotBase;
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
 * REST PATH: monitoring/domain/server/web/session
 *
 * @author BT
 */
@Entity
@Table(name = "SNOT_WEB_SESSION", catalog = "", schema = IGFMonCoreLibConstants.DATABASE_SCHEMA_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Cacheable(false)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Customizer(EntityColumnPositionCustomizer.class)
public class Session extends SnapshotBase {

    /**
     * • activatedsessionstotal
     *
     * Total number of sessions ever activated
     */
    @ColumnPosition(position = 20)
    private Long activatedSessionsTotal;

    /**
     * • activesessionscurrent
     *
     * Number of active sessions
     */
    @ColumnPosition(position = 21)
    private Long activeSessions;

    /**
     * • expiredsessionstotal
     *
     * Total number of sessions ever expired
     */
    @ColumnPosition(position = 22)
    private Long expiredSessionsTotal;

    /**
     * • passivatedsessionstotal
     *
     * Total number of sessions ever passivated
     */
    @ColumnPosition(position = 23)
    private Long passivatedSessionsTotal;

    /**
     * • persistedsessionstotal
     *
     * Total number of sessions ever persisted
     */
    @ColumnPosition(position = 24)
    private Long persistedSessionsTotal;

    /**
     * • rejectedsessionstotal
     *
     * Total number of sessions ever rejected
     */
    @ColumnPosition(position = 25)
    private Long rejectedSessionsTotal;

    /**
     * • sessionstotal
     *
     * Total number of sessions ever created
     */
    @ColumnPosition(position = 26)
    private Long sessionsTotal;

}
