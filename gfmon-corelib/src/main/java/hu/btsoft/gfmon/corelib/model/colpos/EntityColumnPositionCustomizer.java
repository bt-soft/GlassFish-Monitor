/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    EntityColumnPositionCustomizer.java
 *  Created: 2017.12.26. 20:23:32
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model.colpos;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Entitásokból kreált tábla oszlopsorrendjének meghatározása (Eclipselink)
 *
 * forrás: http://marsparanoma.blogspot.hu/2014/01/how-to-set-column-order-in-jpa-under.html
 *
 * @author BT
 */
public class EntityColumnPositionCustomizer implements DescriptorCustomizer {

    /**
     *
     * Customize the provided descriptor. This method is called after the
     * descriptor is populated form annotations/XML/defaults but before it is
     * initialized.
     *
     * @param descriptor
     *
     * @throws Exception
     */
    @Override
    public void customize(ClassDescriptor descriptor) throws Exception {

        descriptor.setShouldOrderMappings(true);

        List<DatabaseMapping> mappings = descriptor.getMappings();

        this.addWeight(this.getClass(descriptor.getJavaClassName()), mappings);
    }

    /**
     *
     * @param clazz
     * @param mappings
     */
    private void addWeight(Class<?> clazz, List<DatabaseMapping> mappings) {

        Map<String, Integer> fieldOrderMap = getColumnPositions(clazz, null);

        mappings.forEach((mapping) -> {
            String key = mapping.getAttributeName();
            Object obj = fieldOrderMap.get(key);
//            int weight = 1;
            int weight = Integer.MAX_VALUE;
            if (obj != null) {
                weight = Integer.parseInt(obj.toString());
            }
            mapping.setWeight(weight);
        });
    }

    /**
     *
     * @param javaFileName
     *
     * @return
     *
     * @throws ClassNotFoundException
     */
    private Class<?> getClass(String javaFileName) throws ClassNotFoundException {

        Class<?> clazz = null;

        if (javaFileName != null && !javaFileName.equals("")) {
            clazz = Class.forName(javaFileName);
        }

        return clazz;
    }

    /**
     *
     * @param classFile
     * @param columnOrder
     *
     * @return
     */
    private Map<String, Integer> getColumnPositions(Class<?> classFile, Map<String, Integer> columnOrder) {

        if (columnOrder == null) {
            columnOrder = new HashMap<>();
        }

        Field[] fields = classFile.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(ColumnPosition.class)) {
                ColumnPosition cp = field.getAnnotation(ColumnPosition.class);
                columnOrder.put(field.getName(), cp.position());
            }
        }

        //rekurzívan végginézzük az ősöket is
        if (classFile.getSuperclass() != null && classFile.getSuperclass() != Object.class) {
            this.getColumnPositions(classFile.getSuperclass(), columnOrder);
        }

        return columnOrder;
    }
}
