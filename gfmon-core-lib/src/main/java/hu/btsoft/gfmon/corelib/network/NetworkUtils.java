/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-core-lib (gfmon-core-lib)
 *  File:    NetworkUtils.java
 *  Created: 2017.12.25. 17:30:02
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.corelib.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author BT
 */
@Slf4j
public class NetworkUtils {

    public static String getIpAddressByHostName(String hostName) {
        try {
            InetAddress address = InetAddress.getByName(hostName);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            log.error("IP cím megállapítási hiba", e);
        }

        return "***unknown***";
    }

}
