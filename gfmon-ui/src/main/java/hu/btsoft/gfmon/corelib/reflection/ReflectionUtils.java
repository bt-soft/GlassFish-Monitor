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
import java.lang.reflect.Method;
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
     * Minden (örökölt) mező leszedése (még az őseké is) a Reflection API használatával
     *
     * @param fields mezők listája
     * @param clazz  osztály példány
     *
     * @return mezők listája
     */
    public static Set<Field> getAllFields(Class<?> clazz, Set<Field> fields) {

        //Leszedjük az összes mezőjét
        fields.addAll(new HashSet<>(Arrays.asList(clazz.getFields())));

        //Ha van őse, akkor azt is (rekurzívan)
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ReflectionUtils.getAllFields(clazz.getSuperclass(), fields);
        }

        return fields;
    }

    /**
     * Minden deklarált mező leszedése (még az őseké is) a Reflection API használatával
     *
     * @param fields mezők listája
     * @param clazz  osztály példány
     *
     * @return mezők listája
     */
    public static Set<Field> getAllDeclaredFields(Class<?> clazz, Set<Field> fields) {

        //Leszedjük az összes mezőjét
        fields.addAll(new HashSet<>(Arrays.asList(clazz.getDeclaredFields())));

        //Ha van őse, akkor azt is (rekurzívan)
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ReflectionUtils.getAllDeclaredFields(clazz.getSuperclass(), fields);
        }

        return fields;
    }

    /**
     * Minden deklarált metódus leszedése (még az őseké is) a Reflection API használatával
     *
     * @param methods mezők listája
     * @param clazz   osztály példány
     *
     * @return mezők listája
     */
    public static Set<Method> getAllDeclaredMethods(Class<?> clazz, Set<Method> methods) {

        //Leszedjük az összes mezőjét
        methods.addAll(new HashSet<>(Arrays.asList(clazz.getDeclaredMethods())));

        //Ha van őse, akkor azt is (rekurzívan)
        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            ReflectionUtils.getAllDeclaredMethods(clazz.getSuperclass(), methods);
        }

        return methods;
    }

}
