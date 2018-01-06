/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    CdiUtils.java
 *  Created: 2017.12.27. 9:12:48
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.cdi;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import lombok.extern.slf4j.Slf4j;

/**
 * CDI utils
 *
 * @author BT
 */
@Slf4j
public class CdiUtils {

    /**
     * CDI bean kikeresése és az összes példány visszaadása
     *
     * @param <T>   Keresett osztály típusa
     * @param clazz keresett osztály
     *
     * @return összes CDI példány
     */
    public static <T> Instance<T> lookupAll(Class<T> clazz) {
        Instance<T> instances = CDI.current().select(clazz);
        return instances;
    }

    /**
     * CDI bean kikeresése és egy példányának visszaadása
     *
     * @param <T>   Keresett osztály típusa
     * @param clazz keresett osztály
     *
     * @return egy CDI példány
     */
    public static <T> T lookupOne(Class<T> clazz) {
        Instance<T> allInstances = lookupAll(clazz);
        if (allInstances == null) {
            return null;
        }
        return allInstances.get();
    }

}
