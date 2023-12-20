package org.edec.factSheet.ctrl;

import org.edec.factSheet.ctrl.renderer.FactSheetDecRenderer;
import org.edec.student.factSheet.ctrl.FactSheetAddCtrl;
import org.edec.factSheet.model.FactSheetStatusEnum;
import org.edec.factSheet.model.FactSheetTableModel;
import org.edec.factSheet.service.FactSheetService;
import org.edec.factSheet.service.impl.FactSheetServiceImpl;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.Button;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnAdd, btnSearch, btnPrint, btnYear, btnReset;
    @Wire
    private Datebox dbFrom, dbTo, dbYear;
    @Wire
    private Listbox lbFactSheet;
    @Wire
    private Textbox tbRegisterNumberSearch, tbFullNameSearch, tbGroupSearch, tbTypeSearch, tbStatusSearch;

    private FactSheetService factSheetService = new FactSheetServiceImpl();
    private JasperReportService jasperReportService = new JasperReportService();

    private List<FactSheetTableModel> factSheets;

    protected void fill() {
        lbFactSheet.setItemRenderer(new FactSheetDecRenderer(this));
        callSearch(null);
    }

    @Listen("onOK=#tbRegisterNumberSearch; onOK=#tbFullNameSearch; onOK=#tbGroupSearch; " +
            "onOK=#tbTypeSearch; onOK=#tbStatusSearch; onClick = #btnSearch; onClick = #btnYear;")
    public void searchFactSheetByFilters(Event e) {

        StringBuilder statusBuilder = new StringBuilder();
        for (FactSheetStatusEnum it : FactSheetStatusEnum.getEnumListByContainsTextInStatusName(tbStatusSearch.getValue())) {
            statusBuilder.append(String.valueOf(it.getStatusId())).append(",");
        }
        String status = statusBuilder.toString();
        if (!status.equals("")) {
            status = status.substring(0, status.length() - 1);
        }

        factSheets = factSheetService.getAllFactSheetsByFilter(tbFullNameSearch.getValue(), tbGroupSearch.getValue(),
                tbTypeSearch.getValue(), status, tbRegisterNumberSearch.getValue(),
                dbFrom.getValue(), dbTo.getValue(),
                currentModule.getDepartments().get(0).getIdInstitute(),
                currentModule.getFormofstudy(), dbYear.getValue()
        );
        lbFactSheet.setModel(new ListModelList<>(factSheets));
        lbFactSheet.renderAll();

        if (e.getData() != null) {
            FactSheetTableModel ratingModel = (FactSheetTableModel) e.getData();
            lbFactSheet.setSelectedIndex(factSheets.indexOf(ratingModel));
            Listitem selectedItem = lbFactSheet.getSelectedItem();
            if (selectedItem != null) {
                selectedItem.focus();
            }
        }

        Clients.clearBusy(lbFactSheet);
    }

    public void callSearch(FactSheetTableModel selectedFactSheet) {
        Clients.showBusy(lbFactSheet, "Загрузка данных");
        Events.echoEvent("onClick", btnSearch, selectedFactSheet);
    }

    @Listen("onClick = #btnAdd")
    public void checkStartPage() {
        Map<String, Object> arg = new HashMap<>();
        arg.put(FactSheetAddCtrl.INDEX_PAGE_CTRL, this);
        arg.put(FactSheetAddCtrl.LB_FACT_SHEET, lbFactSheet);
        arg.put(FactSheetAddCtrl.FORM_OF_STUDY, currentModule.getFormofstudy());
        arg.put(FactSheetAddCtrl.INST, currentModule.getIdInstituteByFirstDepartment());

        ComponentHelper.createWindow("/factSheet/winAddFactSheet.zul", "winAddFactSheet", arg).doModal();
    }

    @Listen("onClick = #btnPrint")
    public void print() {
        jasperReportService
                .getFactSheetByModel(dbFrom.getValue(), dbTo.getValue(), getCurrentUser().getShortFIO(), factSheets)
                .showPdf();
    }

    @Listen("onClick = #btnReset")
    public void reset() {
        clearComponents();
        callSearch(null);
    }

    private void clearComponents() {
        tbFullNameSearch.setValue("");
        tbGroupSearch.setValue("");
        tbTypeSearch.setValue("");
        tbRegisterNumberSearch.setValue("");
        tbStatusSearch.setValue("");
        dbFrom.setValue(null);
        dbTo.setValue(null);
    }
}