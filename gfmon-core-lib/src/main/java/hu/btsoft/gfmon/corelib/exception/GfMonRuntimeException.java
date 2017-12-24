/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    GfMonRuntimeException.java
 *  Created: 2017.12.22. 19:09:00
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.exception;

/**
 * GF montitor unchecked kivétel osztály
 *
 * @author BT
 */
public class GfMonRuntimeException extends RuntimeException {

    public GfMonRuntimeException(String message) {
        super(message);
    }

    public GfMonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GfMonRuntimeException(Throwable cause) {
        super(cause);
    }

}
