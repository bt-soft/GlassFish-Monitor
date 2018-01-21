/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IServerCollector.java
 *  Created: 2017.12.25. 11:02:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.server;

import hu.btsoft.gfmon.engine.monitor.collector.ICollectorBase;

/**
 * GF REST Szerver adatgyűjtés interfész
 *
 * @author BT
 */
public interface IServerCollector extends ICollectorBase {
    //üres interface, hogy a CDI megtalálja a megfelelő példányokat
}
