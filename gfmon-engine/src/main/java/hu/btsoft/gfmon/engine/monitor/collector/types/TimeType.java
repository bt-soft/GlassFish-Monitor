/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-engine (gfmon-engine)
 *  File:    TimeType.java
 *  Created: 2017.12.25. 9:45:56
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.engine.monitor.collector.types;

import hu.btsoft.gfmon.corelib.time.Elapsed;
import hu.btsoft.gfmon.engine.IGFMonEngineConstants;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 * ElapsedTime vagy TimeStamp típus
 *
 * @author BT
 */
@Slf4j
public class TimeType {

    public enum TypeName {
        ELAPSED,
        TIMESTAMP
    };

    /**
     * A paraméterben megadott értéket String-re konvertál
     *
     * @param typeName  idő típus
     * @param timeValue idő értéke Stringesen
     *
     * @return Stringes dátum/elapsed időérték, vagy "-1" ha az jött, vagy épp nem konvertálható idővé az érték
     */
    public static String toString(TypeName typeName, String timeValue) {

        //-1?
        if ("-1".equals(timeValue)) {
            return "-1";
        }

        long timeValueL;
        try {
            timeValueL = Long.parseLong(timeValue);
        } catch (NumberFormatException e) {
            log.warn("A(z) '{}' idő érték nem numerikus!", timeValue);
            return "-1";
        }

        String result = null;

        switch (typeName) {
            case ELAPSED:
                result = Elapsed.getMilliStr(timeValueL);
                break;

            case TIMESTAMP:
                result = IGFMonEngineConstants.SDF.format(new Date(timeValueL));
                break;
        }

        return result;
    }

}
