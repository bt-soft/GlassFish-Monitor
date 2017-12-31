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
     * CDI bean kikeresése és a példányának visszaadása
     *
     * @param <T>   Keresett osztály típusa
     * @param clazz keresett osztály
     *
     * @return CDI példány
     */
    public static <T> T lookup(Class<T> clazz) {
        Instance<T> instances = CDI.current().select(clazz);
        T instance = instances.get();

        //log.trace("CDI Lokup: {}", instance);
        return instance;
    }

}
