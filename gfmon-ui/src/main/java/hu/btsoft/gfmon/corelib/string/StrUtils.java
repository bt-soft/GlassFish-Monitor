/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon (gfmon)
 *  File:    StrUtils.java
 *  Created: 2018.01.24. 17:21:42
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.string;

/**
 * String utils
 *
 * @author BT
 */
public class StrUtils {

    /**
     * Idézőjeltelenít
     *
     * @param str tetszőleges String
     *
     * @return str kiszedett idézőjeles string
     */
    public static String deQuote(String str) {
        if (str != null) {
            return str.replaceAll("\"", "");
        }
        return null;
    }

    /**
     * Aposztróftalanít
     *
     * @param str tetszőleges String
     *
     * @return str kiszedett aposztrófos String
     */
    public static String deApostroph(String str) {
        if (str != null) {
            return str.replaceAll("\'", "");
        }
        return null;
    }

    /**
     * "null" -> null
     *
     * @param str tetszőleges String
     *
     * @return str kiszedett aposztrófos String
     */
    public static String deNull(String str) {
        return "null".equals(str) ? null : str;
    }

}
