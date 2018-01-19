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
     * Eltelt idő ns -> String
     *
     * @param elapsedNano eltelt idő nanosec-ben
     * @return stringesen
     */
    private static String getNanoStr(long elapsedNano) {

        StringBuilder sb = new StringBuilder();

        //Hetek
        int cnt = (int) (elapsedNano / WEEK_NANO);
        if (cnt > 0L) {
            sb.append(cnt).append("w");
        }

        //Napok
        elapsedNano = elapsedNano % WEEK_NANO;
        cnt = (int) (elapsedNano / DAY_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("d");
        }

        //Órák
        elapsedNano = elapsedNano % DAY_NANO;
        cnt = (int) (elapsedNano / HOUR_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("h");
        }

        //Percek
        elapsedNano = elapsedNano % HOUR_NANO;
        cnt = (int) (elapsedNano / MIN_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("m");
        }

        //Másodpercek
        elapsedNano = elapsedNano % MIN_NANO;
        cnt = (int) (elapsedNano / SEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("s");
        }

        //Ezred másodpercek
        elapsedNano = elapsedNano % SEC_NANO;
        cnt = (int) (elapsedNano / MILISEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("ms");
        }

        //Mikro
        elapsedNano = elapsedNano % MILISEC_NANO;
        cnt = (int) (elapsedNano / MICROSEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("us");
        }

        //Nano
        elapsedNano = elapsedNano % MICROSEC_NANO;
        if (elapsedNano > 0) {
            sb.append(" ").append(elapsedNano).append("ns");
        }

        return sb.toString();

    }

    /**
     * Eltelt idő sztringesen
     *
     * @param startNano a kezdő idő
     *
     * @return szövegesen az eltelt idő
     */
    public static String getElapsedNanoStr(long startNano) {
        return getNanoStr(nowNano() - startNano);
    }

    /**
     * Eltelt idő sztringesen
     *
     * @param elapsedMillisec az eltelt idő ezredmásodpercben
     *
     * @return szövegesen az eltelt idő
     */
    public static String getMilliStr(long elapsedMillisec) {
        return getNanoStr(elapsedMillisec * 1_000 * 1_000);
    }
}
