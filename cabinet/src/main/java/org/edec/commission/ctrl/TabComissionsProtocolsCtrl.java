package org.edec.commission.ctrl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.commission.ctrl.renderer.TabComossionsProtocolRenderer;
import org.edec.commission.model.ComissionsProtocolsModel;
import org.edec.commission.service.ComissionsProtocolsService;
import org.edec.commission.service.impl.ComissionsProtocolsServiceImpl;
import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TabComissionsProtocolsCtrl extends CabinetSelector {
    @Wire
    private Combobox cmbSemester;
    @Wire
    private Listbox lbComissionsProtocols;
    @Wire
    private Button btnPrintReportXls;
    @Wire
    private Datebox dbFrom, dbTo;
    @Wire
    private Checkbox chbSign, chbUnSign;
    @Wire
    private Textbox tbProtocolNumber, tbSubjectname, tbFioStudent, tbChairName, tbGroupname;

    private SemesterModel currentSem;
    private DepartmentModel currentDepartment;

    private ComponentService componentService = new ComponentServiceESOimpl();
    private ComissionsProtocolsService service = new ComissionsProtocolsServiceImpl();

    private List<ComissionsProtocolsModel> comissiosProtocols = new ArrayList<>();
    private List<ComissionsProtocolsModel> filterdComissiosProtocols = new ArrayList<>();

    @Override
    protected void fill() throws InterruptedException {
        chbUnSign.setChecked(true);
        chbSign.setChecked(true);
        currentDepartment = currentModule.getDepartments().get(0);
        componentService.fillCmbSem(cmbSemester, currentDepartment.getIdInstitute(), currentModule.getFormofstudy(), null);
    }

    @Listen("onChange = #cmbSemester")
    public void fillLbComissionsProtocols() {
        currentSem = cmbSemester.getSelectedItem().getValue();
        comissiosProtocols = service.getComissionsProtocols(currentSem.getIdSem());
        lbComissionsProtocols.setModel(new ListModelList(comissiosProtocols));
        lbComissionsProtocols.setItemRenderer(new TabComossionsProtocolRenderer());
        filterProtocolsList();
    }

    @Listen("onClick = #btnPrintReportXls")
    public void printReportXls() throws IOException {
        if (currentSem != null) {
            HSSFWorkbook report;
            String semesterStr = DateConverter.convert2dateToString(currentSem.getDateOfBegin(), currentSem.getDateOfEnd()) + " " + (currentSem.getSeason() == 0 ? "осень" : "весна");
            if (!filterdComissiosProtocols.isEmpty()) {
                filterdComissiosProtocols.stream().forEach(el -> el.setSemester(semesterStr));
                report = service.getComissionsProtocolReport(filterdComissiosProtocols);
            } else {
                comissiosProtocols.stream().forEach(el -> el.setSemester(semesterStr));
                report = service.getComissionsProtocolReport(comissiosProtocols);
            }
            String fn = "Реестр протоколов комиссий за семестр ("+semesterStr+").xls";
            File file = new File(fn);
            FileOutputStream outFile = new FileOutputStream(file);
            report.write(outFile);
            Filedownload.save(file, null);
        } else {
            PopupUtil.showWarning("Выберите семестр!");
        }

    }

    @Listen("onChange = #dbTo, #dbFrom; onClick = #chbSign, #chbUnSign; onOK = #tbProtocolNumber, #tbSubjectname, #tbFioStudent, #tbChairName, #tbGroupname")
    public void filterProtocolsList() {

        List<ComissionsProtocolsModel> filteredBySignProtocolList = new ArrayList<>();
        if (currentSem != null) {
            List<ComissionsProtocolsModel> filteredByDateProtocolList = service.getComissionsProtocols(currentSem.getIdSem());
            if (dbFrom.getValue() != null && dbTo.getValue() != null) {
                filteredByDateProtocolList = comissiosProtocols.stream().filter(el -> (el.getDateComission().after(dbFrom.getValue()) && el.getDateComission().before(dbTo.getValue()))
                        || el.getDateComission().equals(dbFrom.getValue()) || el.getDateComission().equals(dbTo.getValue())).collect(Collectors.toList());
            } else if (dbFrom.getValue() != null) {
                filteredByDateProtocolList = comissiosProtocols.stream().filter(el -> el.getDateComission().after(dbFrom.getValue()) || el.getDateComission().equals(dbFrom)).collect(Collectors.toList());
            } else if (dbTo.getValue() != null) {
                filteredByDateProtocolList = comissiosProtocols.stream().filter(el -> el.getDateComission().before(dbTo.getValue()) || el.getDateComission().equals(dbTo)).collect(Collectors.toList());
            }

            if (chbSign.isChecked() && chbUnSign.isChecked()) {
                filteredBySignProtocolList = filteredByDateProtocolList;
            } else if (chbSign.isChecked()) {
                filteredBySignProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getCertnumber() != null).collect(Collectors.toList());
            } else if (chbUnSign.isChecked()) {
                filteredBySignProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getCertnumber() == null).collect(Collectors.toList());
            }

            filteredByDateProtocolList.retainAll(filteredBySignProtocolList);

            if (tbProtocolNumber.getValue() != null && !tbProtocolNumber.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getProtocolNumber() != null && el.getProtocolNumber().toLowerCase().contains(tbProtocolNumber.getValue().toLowerCase())).collect(Collectors.toList());
            }
            if (tbSubjectname.getValue() != null && !tbSubjectname.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getSubjectname().toLowerCase().contains(tbSubjectname.getValue().toLowerCase())).collect(Collectors.toList());
            }
            if (tbFioStudent.getValue() != null && !tbFioStudent.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getFioStudent().toLowerCase().contains(tbFioStudent.getValue().toLowerCase())).collect(Collectors.toList());
            }
            if (tbChairName.getValue() != null && !tbChairName.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getChairsName().toLowerCase().contains(tbChairName.getValue().toLowerCase())).collect(Collectors.toList());
            }
            if (tbGroupname.getValue() != null && !tbGroupname.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getGroupname().toLowerCase().contains(tbGroupname.getValue().toLowerCase())).collect(Collectors.toList());
            }

            filterdComissiosProtocols = filteredByDateProtocolList;
            lbComissionsProtocols.setModel(new ListModelList(filteredByDateProtocolList));
            lbComissionsProtocols.setItemRenderer(new TabComossionsProtocolRenderer());
        } else {
            PopupUtil.showWarning("Выберите семестр!");
        }
    }
}
