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
     *
     * @return szövegesen az eltelt idő
     */
    public static String getNanoStr(long startNano) {

        StringBuilder sb = new StringBuilder();

        long elapsednano = nowNano() - startNano;

        //Hetek
        int cnt = (int) (elapsednano / WEEK_NANO);
        if (cnt > 0L) {
            sb.append(cnt).append("w");
        }

        //Napok
        elapsednano = elapsednano % WEEK_NANO;
        cnt = (int) (elapsednano / DAY_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("d");
        }

        //Órák
        elapsednano = elapsednano % DAY_NANO;
        cnt = (int) (elapsednano / HOUR_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("h");
        }

        //Percek
        elapsednano = elapsednano % HOUR_NANO;
        cnt = (int) (elapsednano / MIN_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("m");
        }

        //Másodpercek
        elapsednano = elapsednano % MIN_NANO;
        cnt = (int) (elapsednano / SEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("s");
        }

        //Ezred másodpercek
        elapsednano = elapsednano % SEC_NANO;
        cnt = (int) (elapsednano / MILISEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("ms");
        }

        //Mikro
        elapsednano = elapsednano % MILISEC_NANO;
        cnt = (int) (elapsednano / MICROSEC_NANO);
        if (cnt > 0) {
            sb.append(" ").append(cnt).append("us");
        }

        //Nano
        elapsednano = elapsednano % MICROSEC_NANO;
        if (elapsednano > 0) {
            sb.append(" ").append(elapsednano).append("ns");
        }

        return sb.toString();
    }
}
