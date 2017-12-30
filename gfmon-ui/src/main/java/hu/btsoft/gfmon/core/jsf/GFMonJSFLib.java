/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

}
