package org.edec.student.factSheet.ctrl;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.student.factSheet.ctrl.renderer.FactSheetHistoryRenderer;
import org.edec.factSheet.service.FactSheetService;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class FactSheetHistoryCtrl extends SelectorComposer<Component> {

    private FactSheetService factSheetService = new FactSheetServiceImpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();
    private FactSheetHistoryRenderer factSheetHistoryRenderer = new FactSheetHistoryRenderer();

    public static final String PROFILE_PAGE = "profile_page";

    @Wire
    private Listbox lbFactSheet;

    @Wire
    private Window index;

    @Wire
    private Button btnClose;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        fill();
    }

    private void fill () {
        lbFactSheet.setItemRenderer(new FactSheetHistoryRenderer());
        lbFactSheet.setModel(new ListModelList<>(factSheetService.getFactSheetsHistory(template.getCurrentUser().getIdHum())));
        lbFactSheet.renderAll();
    }

    @Listen("onClick = #btnClose")
    public void delete () {
        factSheetHistoryRenderer.deleteFactSheet(lbFactSheet);
        index.detach();
    }
}

