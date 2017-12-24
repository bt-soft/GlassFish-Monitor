/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    Elapsed.java
 *  Created: 2017.12.22. 14:35:41
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.time;

/**
 * Időmérés
 *
 * @author BT
 */
public class Elapsed {

    private static final long MICROSEC_NANO = 1_000L;
    private static final long MILISEC_NANO = 1_000L * MICROSEC_NANO;
    private static final long SEC_NANO = 1_000L * MILISEC_NANO;
    private static final long MIN_NANO = 60L * SEC_NANO;
    private static final long HOUR_NANO = 60L * MIN_NANO;
    private static final long DAY_NANO = 24L * HOUR_NANO;
    private static final long WEEK_NANO = 7L * DAY_NANO;

    /**
     * Nanosec-ben az aktuális idő
     *
     * @return nanosec
     */
    public static long nowNano() {
        return System.nanoTime();
    }

    /**
     * Eltelt idő sztringesen
     *
     * @param startNano a kezdő idő
     * @return szövegesen az eltelt idő
     */
    public static String getNanoStr(long startNano) {

        StringBuilder sb = new StringBuilder();

        //Hetek
        int cnt = (int) (startNano / WEEK_NANO);
        if (cnt > 0L) {
            sb.append(cnt).append("w");
        }

        //Napok
        startNano = startNano % WEEK_NANO;
        cnt = (int) (startNano / DAY_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("d");
        }

        //Órák
        startNano = startNano % DAY_NANO;
        cnt = (int) (startNano / HOUR_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("h");
        }

        //Percek
        startNano = startNano % HOUR_NANO;
        cnt = (int) (startNano / MIN_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("m");
        }

        //Másodpercek
        startNano = startNano % MIN_NANO;
        cnt = (int) (startNano / SEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("s");
        }

        //Ezred másodpercek
        startNano = startNano % SEC_NANO;
        cnt = (int) (startNano / MILISEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("ms");
        }

        //Mikro
        startNano = startNano % MILISEC_NANO;
        cnt = (int) (startNano / MICROSEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("us");
        }

        //Nano
        startNano = startNano % MICROSEC_NANO;
        if (startNano > 0) {
            sb.append(" ").append(startNano).append("ns");
        }

        return sb.toString();
    }
}
