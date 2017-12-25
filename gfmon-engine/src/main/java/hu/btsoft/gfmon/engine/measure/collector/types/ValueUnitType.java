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
package hu.btsoft.gfmon.engine.measure.collector.types;

/**
 * GF monitoradatok mértékegységei
 *
 * @author BT
 */
public enum ValueUnitType {

    COUNT("count", ValueClass.NUMBER),
    MILLISECONDS("millisecond", ValueClass.NUMBER),
    BYTES("bytes", ValueClass.NUMBER),
    STRING("String", ValueClass.STRING),
    LIST("List", ValueClass.STRING);

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
     *
     * @throws IllegalArgumentException ha nincs meg
     */
    public static ValueUnitType fromValue(String value) throws IllegalArgumentException {

        for (ValueUnitType valueUnitType : ValueUnitType.values()) {
            if (valueUnitType.unitName.equals(value)) {
                return valueUnitType;
            }
        }

        return null;
    }
}
