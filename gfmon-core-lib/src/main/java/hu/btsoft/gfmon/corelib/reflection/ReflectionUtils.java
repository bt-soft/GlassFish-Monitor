/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    ReflectionUtils.java
 *  Created: 2017.12.26. 21:05:37
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.reflection;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Reflection API utils
 *
 * @author BT
 */
public class ReflectionUtils {

    /**
     * Minden mező leszedése (még az őseké is) a Reflection API használatával
     *
     * @param fields mezők listája
     * @param clazz  osztály példány
     *
     * @return mezők listája
     */
    public static Set<Field> getAllFields(Class<?> clazz, Set<Field> fields) {

        //Leszedjük az összes mezőjét
        fields.addAll(new HashSet<>(Arrays.asList(clazz.getDeclaredFields())));

        //Ha van őse, akkor azt is (rekurzívan)
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ReflectionUtils.getAllFields(clazz.getSuperclass(), fields);
        }

        return fields;
    }

}
