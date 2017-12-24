/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    GfMonException.java
 *  Created: 2017.12.22. 19:08:01
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.exception;

/**
 * GF montitor kivétel osztály
 *
 * @author BT
 */
public class GfMonException extends Exception {

    public GfMonException(String message) {
        super(message);
    }

    public GfMonException(String message, Throwable cause) {
        super(message, cause);
    }

    public GfMonException(Throwable cause) {
        super(cause);
    }

}
