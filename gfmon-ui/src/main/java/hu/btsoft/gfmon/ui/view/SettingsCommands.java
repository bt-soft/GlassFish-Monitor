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
    COPY_SELECTED, //selectedServer -> modifiedServer copy
    SAVE_MODIFIED, //modifiedServer -> servers replace, megváltoztatta a szerver beállításokat
    CANCEL_MODIFY, //modifiedServer -> eldobni, mégsem akarja megváltoztatni
    DELETE_SELECTED, //selectedServer -> törölni, a kiválasztott szervert törölte

}
