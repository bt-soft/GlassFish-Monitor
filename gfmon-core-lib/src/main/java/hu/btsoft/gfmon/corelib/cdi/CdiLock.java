/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    CdiLock.java
 *  Created: 2017.12.22. 18:52:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.cdi;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;
import javax.ejb.LockType;
import javax.interceptor.InterceptorBinding;

/**
 * CDI lock annotáció
 *
 * @author BT
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface CdiLock {

    //A default lock a write
    LockType value() default LockType.WRITE;
}
