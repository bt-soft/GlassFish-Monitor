/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    IGFMonCoreLibConstants.java
 *  Created: 2017.12.30. 12:18:00
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib;

import java.text.SimpleDateFormat;

/**
 * GFMon közös definíciók
 *
 * @author BT
 */
public interface IGFMonCoreLibConstants {

    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String SHORT_APP_NAME = "GF-MON";
    String LONG_APP_NAME = "GlassFish Monitor";

    String PROTOCOL_HTTP = "http://";
    String PROTOCOL_HTTPS = "https://";

    String DATABASE_SCHEMA_NAME = "GFMON";

    String PERSISTENCE_UNIT_NAME = "gfmon-corelib_PU";

}
