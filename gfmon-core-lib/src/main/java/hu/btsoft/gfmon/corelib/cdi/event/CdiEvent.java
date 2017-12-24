/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    Shutdown.java
 *  Created: 2017.12.23. 7:55:11
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.cdi.event;

import lombok.Getter;

/**
 * CDI Event osztály
 *
 * @author BT
 */
@Getter
public class CdiEvent {

    /**
     * Esemány típusok
     */
    public static enum Type {
        STARTUP, //indulás
        SHUTDOWN, //leállítás
        CONFIG,    //konfiguráció
    }

    /**
     * küldő osztály típusa
     */
    private final Class sourceClass;

    /**
     * Esemény típusa
     */
    private final CdiEvent.Type eventType;

    /**
     * Üzenet szövege
     */
    private final String msg;

    /**
     * Konstruktor
     *
     * @param sourceClass küldő osztály típusa
     * @param eventType esemény típus
     * @param msg üzenet szövege
     */
    public CdiEvent(Class sourceClass, Type eventType, String msg) {
        this.sourceClass = sourceClass;
        this.eventType = eventType;
        this.msg = msg;
    }

    /**
     * Konstruktor
     *
     * @param eventType esemény típus
     * @param msg üzenet szövege
     */
    public CdiEvent(Type eventType, String msg) {
        this.sourceClass = null;
        this.eventType = eventType;
        this.msg = msg;
    }

}
