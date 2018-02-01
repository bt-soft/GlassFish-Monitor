/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    EntityAuditListener.java
 *  Created: 2018.02.01. 9:18:40
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.audit;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.sessions.Record;

/**
 *
 * @author BT
 */
@Slf4j
public class EntityAuditListener extends DescriptorEventAdapter {

    private static final ThreadLocal CURRENT_AUDIT_USER = new ThreadLocal();

    public static final String UNKNOWN_USER = "!! Unknown User !!";

    public static final String AUDIT_COLUMN_CREATED_BY = "CREATED_BY";
    public static final String AUDIT_COLUMN_CREATED_DATE = "CREATED_DATE";

    public static final String AUDIT_COLUMN_MODIFIED_BY = "MODIFIED_BY";
    public static final String AUDIT_COLUMN_MODIFIED_DATE = "MODIFIED_DATE";

    /**
     * Aktuális audit user lekérdezése
     *
     * @return user, vagy null
     */
    public static String getCurrentAuditUser() {
        return (String) CURRENT_AUDIT_USER.get();
    }

    /**
     * Aktuális audit user beállítása
     *
     * @param userName user
     */
    public static void setCurrentAuditUser(String userName) {
        CURRENT_AUDIT_USER.set(userName);
    }

    @Override
    public void preRemove(DescriptorEvent event) {
        log.trace("preRemove", event);
    }

    @Override
    public void preDelete(DescriptorEvent event) {
        log.trace("preDelete", event);
    }

    @Override
    public void postDelete(DescriptorEvent event) {
        log.trace("postDelete", event);
    }

    @Override
    public void postRefresh(DescriptorEvent event) {
        log.trace("postRefresh", event);
    }

    @Override
    public void postClone(DescriptorEvent event) {
        log.trace("postClone", event);
    }

    @Override
    public void postBuild(DescriptorEvent event) {
        log.trace("postBuild", event);
    }
//---------------------------------------------

    @Override
    public void postMerge(DescriptorEvent event) {
        log.trace("postMerge", event);
    }

    @Override
    public void preWrite(DescriptorEvent event) {
        log.trace("preWrite", event);
    }

    @Override
    public void postWrite(DescriptorEvent event) {
        log.trace("postWrite", event);
    }

    @Override
    public void prePersist(DescriptorEvent event) {
        log.trace("prePersist", event);
    }

//---------------------------------------------
    @Override
    public void preInsert(DescriptorEvent event) {
        log.trace("preInsert", event);
    }

    @Override
    public void aboutToInsert(DescriptorEvent event) {
        log.trace("aboutToInsert", event);
    }

    @Override
    public void postInsert(DescriptorEvent event) {
        log.trace("postInsert", event);
    }

//---------------------------------------------
    @Override
    public void preUpdateWithChanges(DescriptorEvent event) {
        log.trace("preUpdateWithChanges", event);
    }

    @Override
    public void preUpdate(DescriptorEvent event) {
        log.trace("preUpdate", event);
    }

    @Override
    public void aboutToUpdate(DescriptorEvent event) {
        log.trace("aboutToUpdate", event);

        Record recordMap = event.getRecord();
        ClassDescriptor descriptor = event.getDescriptor();

//        for(DatabaseMapping databaseMappings : descriptor.getMappings()){
//            if(databaseMappings.getAttributeName())
//
//        }
        List<DatabaseTable> tables = descriptor.getTables();
        for (DatabaseTable table : tables) {
            String tableQualifiedName = table.getQualifiedName();

        }

    }

    @Override
    public void postUpdate(DescriptorEvent event) {
        log.trace("postUpdate", event);
    }

}
