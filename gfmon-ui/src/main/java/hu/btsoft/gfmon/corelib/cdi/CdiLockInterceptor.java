/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    CdiLockInterceptor.java
 *  Created: 2017.12.22. 18:54:28
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.cdi;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 *
 * @author BT
 */
@CdiLock
@Interceptor
public class CdiLockInterceptor {

    /**
     * lock objektum
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    /**
     * CDI lock interceptor
     *
     * @param ctx interceptor context
     * @return a hívott metódus visszatérési értéke
     * @throws Exception a hiba továbbadva
     */
    @AroundInvoke
    public Object concurrencyControl(InvocationContext ctx) throws Exception {
        CdiLock lockAnotation = ctx.getMethod().getAnnotation(CdiLock.class);

        if (lockAnotation == null) {
            lockAnotation = ctx.getTarget().getClass().getAnnotation(CdiLock.class);
        }

        Object returnValue = null;
        switch (lockAnotation.value()) {
            case WRITE:
                ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
                try {
                    writeLock.lock();
                    returnValue = ctx.proceed();
                } finally {
                    writeLock.unlock();
                }
                break;

            case READ:
                ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
                try {
                    readLock.lock();
                    returnValue = ctx.proceed();
                } finally {
                    readLock.unlock();
                }
                break;
        }
        return returnValue;
    }

}
