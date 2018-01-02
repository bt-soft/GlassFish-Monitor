/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    ViewBase.java
 *  Created: 2017.12.31. 23:37:53
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.ui.view;

import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import lombok.extern.slf4j.Slf4j;

/**
 * JSF ManagedBean ős
 *
 * @author BT
 */
@Slf4j
public abstract class ViewBase implements Serializable {

    /**
     * JSF message megjelenítése
     *
     * @param clientId JSF message id
     * @param severity sújosság
     * @param msg      szöveg
     */
    protected void addJsfMessage(String clientId, FacesMessage.Severity severity, String msg) {
        String summary = "nemtom";
        if (FacesMessage.SEVERITY_INFO == severity) {
            summary = "Információ";
        } else if (FacesMessage.SEVERITY_WARN == severity) {
            summary = "Figyelmeztetés";
        } else if (FacesMessage.SEVERITY_ERROR == severity) {
            summary = "Hiba";
        } else if (FacesMessage.SEVERITY_FATAL == severity) {
            summary = "Súlyos hiba";
        }
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, msg));
    }

}
