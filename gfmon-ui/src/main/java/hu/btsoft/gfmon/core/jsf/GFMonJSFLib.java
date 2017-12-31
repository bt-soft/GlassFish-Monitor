/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    GFMonJSFLib.java
 *  Created: 2017.12.31. 22:12:22
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.core.jsf;

import hu.btsoft.gfmon.corelib.version.VersionUtils;
import hu.btsoft.gfmon.ui.GFMonUIConstants;
import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author BT
 */
@Named(value = "gfMonJsfLib")
@ApplicationScoped
public class GFMonJSFLib implements Serializable {

    public String getVersion() {
        return VersionUtils.getModuleVersionStr(GFMonJSFLib.class);
    }

    public String getShortAppNameAndversion() {
        return GFMonUIConstants.SHORT_APP_NAME + " V" + VersionUtils.getModuleVersionStr(GFMonJSFLib.class);
    }

    /**
     * JSF implementáció verziójának lekérdezése
     *
     * @return Mojarra verziója
     */
    public String getJsfVersion() {
        return String.format("%s v%s (%s)",
                FacesContext.class.getPackage().getImplementationTitle(),
                FacesContext.class.getPackage().getImplementationVersion(),
                FacesContext.class.getPackage().getImplementationVendor()
        );
    }

    /**
     * PrimeFaces verzió lekérdezése
     *
     * @return PF verzió
     */
    public String getPrimefacesVersion() {
        return RequestContext.getCurrentInstance().getApplicationContext().getConfig().getBuildVersion();
    }

    /**
     * Redit a login lapra
     *
     * @return login lap JSF rtedir URL
     */
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return GFMonUIConstants.JSF_START_URL + GFMonUIConstants.MAIN_PAGE + GFMonUIConstants.JSF_REDIRECT;
    }

    /**
     * Aktuális user lekérése
     *
     * @return user
     */
    public static String getCurrentUser() {
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }
}
