/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    SettingsCommands.java
 *  Created: 2018.01.02. 16:43:06
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.ui.view;

/**
 * A SettingsView commandHandler paraméterében megadható parancsok
 *
 * @author BT
 */
public enum SettingsCommands {
    NEW_SERVER, //új szerver felvétele: new modifiedServer
    SAVE_NEW_SERVER, //új szerver mentése: new modifiedServer
    CANCEL_NEW_SERVER, //mégsem menti az új szervert
    //
    COPY_SELECTED, //meg akarja változtatni a szerver beálíltásokat: selectedServer -> modifiedServer
    SAVE_MODIFIED, //megváltoztatta a szerver beállításokat: modifiedServer -> servers replace
    CANCEL_MODIFIED, //mégsem akarja megváltoztatni: modifiedServer -> eldobni
    DELETE_SELECTED, //a kiválasztott szervert törölte: selectedServer -> törölni

}
