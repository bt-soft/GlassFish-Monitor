/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IApplicationCollector.java
 *  Created: 2017.12.25. 11:02:43
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.application;

import hu.btsoft.gfmon.engine.monitor.collector.ICollectorBase;

/**
 * GF REST Alkalmazás adatgyűjtés interfész
 *
 * @author BT
 */
public interface IApplicationCollector extends ICollectorBase {
    //üres interface, hogy a CDI megtalálja a megfelelő példányokat
}
