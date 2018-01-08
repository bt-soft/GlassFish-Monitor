/*
 *  ------------------------------------------------------------------------------------
 *
 *  GF Monitor project
 *
 *  Module:  gfmon-ui (gfmon-ui)
 *  File:    CollectorSettingsView.java
 *  Created: 2018.01.06. 18:09:36
 *
 *  ------------------------------------------------------------------------------------
 */
package hu.btsoft.gfmon.ui.view.settings;

import hu.btsoft.gfmon.corelib.model.entity.server.CollectorDataUnit;
import hu.btsoft.gfmon.corelib.model.service.CollectorDataUnitService;
import hu.btsoft.gfmon.ui.view.ViewBase;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;

/**
 *
 * @author BT
 */
@Named(value = "collectorSettingsView")
@ViewScoped
public class CollectorSettingsView extends ViewBase {

    @EJB
    private CollectorDataUnitService collectorDataUnitService;

    @Getter
    private List<CollectorDataUnit> collectorDataUnits;

    @PostConstruct
    protected void init() {
        collectorDataUnits = collectorDataUnitService.findAll();
    }
}
