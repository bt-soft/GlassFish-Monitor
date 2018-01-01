/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    UIDisablePanel.java
 *  Created: 2018.01.01. 16:20:38
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.core.jsf.component;

import java.io.IOException;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

/**
 * A JSF konténeren elhelyezkedő összes JSF komponens tiltására képes Panel
 *
 * ötlet: https://stackoverflow.com/questions/3152561/how-to-disable-page-form-in-jsf
 *
 * @author BT
 */
@FacesComponent("hu.btsoft.gfmon.core.jsf.component.UIDisablePanel")
public class UIDisablePanel extends UIComponentBase {

    private static final String OWN_ATTRIBUTE_FLAG = UIDisablePanel.class.getSimpleName() + "Flag";
    private static final String DISABLED_ATTRIBUTE = "disabled";

    /**
     * Konstruktor
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UIDisablePanel() {
        setRendererType(null);
    }

    /**
     *
     * @param context
     *
     * @throws IOException
     */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        processDisablePanel(this, isDisabled());
    }

    /**
     * A JSF komponensfán véggigyalogolava engedélyezi/tiltja az erre alkalmas komponenseket
     *
     * @param root      komponensfa gyökér elem
     * @param toDisable tiltás/engedélyezés
     */
    public void processDisablePanel(UIComponent root, boolean toDisable) {

        //végigmegyünk az összes gyerek komponensen
        root.getChildren().stream().map((c) -> {

            //if (c instanceof UIInput || c instanceof UICommand) {
            if (c instanceof UIComponentBase) { //Minden komponensen vágiggyaloglunk

                if (toDisable) {

                    //Most mi az állapota?
                    Boolean curState = (Boolean) c.getAttributes().get(DISABLED_ATTRIBUTE);

                    if (curState == null || curState == false) {
                        c.getAttributes().put(OWN_ATTRIBUTE_FLAG, true); //megjelöljük, hogy mi raktuk rá az attribútumot
                        c.getAttributes().put(DISABLED_ATTRIBUTE, true); //beállítjuk, hogy tiltott a komponens
                    }

                } else {

                    if (c.getAttributes().get(OWN_ATTRIBUTE_FLAG) != null) {  //csak a magunk által beállított attribútummal foglalkozunk
                        c.getAttributes().remove(OWN_ATTRIBUTE_FLAG); //leszedjük a saját attribútumot
                        c.getAttributes().put(DISABLED_ATTRIBUTE, false); //beállítjuk, hogy nem tiltott a komponens
                    }
                }

            }
            return c;

        }).filter((c) -> (c.getChildCount() > 0)).forEachOrdered((c) -> {

            //Ha a komponensnek van(nak) további gyerek komponense(i), akkor rekurzívan azokon is végigmegyünk
            processDisablePanel(c, toDisable);
        });

    }

    /**
     * <p>
     * Return the identifier of the component family to which this component belongs.
     * This identifier, in conjunction with the value of the <code>rendererType</code> property,
     * may be used to select the appropriate {@link Renderer} for this component instance.</p>
     *
     * @return
     */
    @Override
    public String getFamily() {
        return null;
    }

    /**
     * Disabled attribútum elkérése
     *
     * @return true/false
     */
    public boolean isDisabled() {
        return (boolean) getStateHelper().eval(DISABLED_ATTRIBUTE, false);
    }

    /**
     * Disabled attribútum beállítása
     *
     * @param disabled új attribútum
     */
    public void setDisabled(boolean disabled) {
        getStateHelper().put(DISABLED_ATTRIBUTE, disabled);
    }

}
