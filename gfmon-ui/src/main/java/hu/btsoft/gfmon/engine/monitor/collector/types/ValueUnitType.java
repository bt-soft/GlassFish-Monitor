/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    ValueUnitType.java
 *  Created: 2017.12.25. 8:58:23
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.types;

import lombok.extern.slf4j.Slf4j;

/**
 * GF monitoradatok mértékegységei
 *
 * @author BT
 */
@Slf4j
public enum ValueUnitType {

    COUNT("count", ValueClass.NUMBER),
    NANOSECOND("nanosecond", ValueClass.NUMBER),
    MILLISECOND("millisecond", ValueClass.NUMBER),
    SECONDS("seconds", ValueClass.NUMBER),
    BYTES("bytes", ValueClass.NUMBER),
    STRING("String", ValueClass.STRING),
    LIST("List", ValueClass.STRING),
    //
    //EJB statisztika csacsiságai
    //
    MILLISECONDS("Milliseconds", ValueClass.NUMBER),
    UNIT("unit", ValueClass.NUMBER),
    //
    //Ezek 'kézzel' lesznek feltérképezve (CollectorBase.fetchValues())
    //
    COUNT_CURR_LW_HW("ne-találd-meg", ValueClass.NUMBER), //Current/Low/HighWatermark
    COUNT_CURR_LW_HW_LB_UB("ne-találd-meg", ValueClass.NUMBER), //Current/Low/HighWatermark + LowerBound/UpperBound (EJB methodreadycount)
    COUNT_MT_MT_TT("ne-találd-meg", ValueClass.NUMBER); //Count/MinTime/MaxTime/TotalTime

    private final String unitName;

    public enum ValueClass {
        NUMBER, //JSonNumber lesz
        STRING, //JSonString lesz
    };
    private final ValueClass valueClass;

    /**
     * Konstruktor
     *
     * @param unitName a mértékegység neve
     */
    ValueUnitType(String unitName, ValueClass valueClass) {
        this.unitName = unitName;
        this.valueClass = valueClass;
    }

    /**
     * Enum kikeresése az értéke alapján
     *
     * @param value enum érték
     *
     * @return enum vagy null
     */
    public static ValueUnitType fromValue(String value) {

        for (ValueUnitType valueUnitType : ValueUnitType.values()) {
            if (valueUnitType.unitName.equalsIgnoreCase(value)) {  //Az EJB statisztikában van 'Miliseconds', meg 'unit' is... :-/
                return valueUnitType;
            }
        }

        log.warn("Nincs a(z) '{}' mértékegység kezelve ", value);
        return null;
    }
}
