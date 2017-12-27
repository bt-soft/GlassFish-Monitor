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

import java.text.SimpleDateFormat;

/**
 * Monitor konstansok
 *
 * @author BT
 */
public interface IGFMonEngineConstants {

    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String SHORT_APP_NAME = "GF-MON";

    String DATABASE_SCHEMAN_NAME = "GFMON";
    String PROTOCOL_HTTP = "http://";
    String PROTOCOL_HTTPS = "https://";

    String LOW_WATERMARK_VAR_POSTFX = "Lw";
    String HIGH_WATERMARK_VAR_POSTFX = "Hw";

}
