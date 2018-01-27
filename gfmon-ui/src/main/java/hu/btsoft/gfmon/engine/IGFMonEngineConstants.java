/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    IGFMonEngineConstants.java
 *  Created: 2017.12.23. 11:54:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine;

import hu.btsoft.gfmon.corelib.IGFMonCoreLibConstants;

/**
 * Monitor motor konstansok
 *
 * @author BT
 */
public interface IGFMonEngineConstants extends IGFMonCoreLibConstants {

    String LOW_WATERMARK_VAR_POSTFIX = "Lw";
    String HIGH_WATERMARK_VAR_POSTFIX = "Hw";

    String LOWERBOUND_VAR_POSTFIX = "Lb";
    String UPPERBOUND_VAR_POSTFIX = "Ub";

    String MINTIME_VAR_POSTFIX = "Tmin";
    String MAXTIME_VAR_POSTFIX = "Tmax";
    String TOTALTIME_VAR_POSTFIX = "Ttot";

}
