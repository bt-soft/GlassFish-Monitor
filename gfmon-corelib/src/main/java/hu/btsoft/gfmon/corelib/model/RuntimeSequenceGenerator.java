/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-corelib (gfmon-corelib)
 *  File:    RuntimeSequenceGenerator.java
 *  Created: 2018.01.03. 16:20:47
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Runtime szekvencia generátor
 * A még csak a memóriában létező, adatbázisba még le nem mentett entitás rekordoknál használjuk
 *
 * @author BT
 */
public class RuntimeSequenceGenerator {

    private static final AtomicLong COUNT = new AtomicLong(1);

    /**
     * Újabb Integer érték elkérése
     *
     * @return új Integer érték
     */
    public static Integer getNextInt() {
        return new Long(COUNT.getAndIncrement()).intValue();
    }

    /**
     * Újabb Long érték elkérése
     *
     * @return új Long érték
     */
    public static Long getNextLong() {
        return COUNT.getAndIncrement();
    }

}
