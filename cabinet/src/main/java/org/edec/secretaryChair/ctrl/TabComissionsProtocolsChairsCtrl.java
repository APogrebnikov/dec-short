package org.edec.secretaryChair.ctrl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.edec.main.model.DepartmentModel;
import org.edec.secretaryChair.ctrl.renderer.TabComossionsProtocolChairRenderer;
import org.edec.secretaryChair.model.ChairsComissionsProtocolsModel;
import org.edec.secretaryChair.service.ChairsComissionProtocolsService;
import org.edec.secretaryChair.service.impl.ChairsComissionsProtocolsServiceImpl;
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

public class TabComissionsProtocolsChairsCtrl extends CabinetSelector {
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
    private Textbox tbProtocolNumber, tbSubjectname, tbFioStudent, tbGroupname;
    @Wire
    private Radio rFullTime, rExtramural;

    private SemesterModel currentSem;
    private DepartmentModel currentDepartment;
    private int fos;
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ChairsComissionProtocolsService service = new ChairsComissionsProtocolsServiceImpl();

    private List<ChairsComissionsProtocolsModel> comissiosProtocols = new ArrayList<>();
    private List<ChairsComissionsProtocolsModel> filterdComissiosProtocols = new ArrayList<>();

    @Override
    protected void fill() throws InterruptedException {
        currentDepartment = currentModule.getDepartments().get(0);
        rFullTime.setChecked(true);
        chbSign.setChecked(true);
        chbUnSign.setChecked(true);
        fillCmbSemester();

    }

    @Listen("onClick = #btnSearchComissionsProtocols")
    public void fillLbComissionsProtocols() {
        currentSem = cmbSemester.getSelectedItem().getValue();
        comissiosProtocols = service.getComissionsProtocols(currentSem.getIdSem(), currentDepartment.getIdChair());
        if (!comissiosProtocols.isEmpty()){
            lbComissionsProtocols.setModel(new ListModelList(comissiosProtocols));
            lbComissionsProtocols.setItemRenderer(new TabComossionsProtocolChairRenderer());
            filterProtocolsList();
        } else {
            PopupUtil.showWarning("Нет протоклов в этом семестре!");
        }
    }
    @Listen("onClick = #rFullTime, #rExtramural")
    public void fillCmbSemester() {
        if (currentDepartment == null) {
            lbComissionsProtocols.setEmptyMessage("Не удается определить кафедру");
        } else {
            if (rFullTime.isChecked()) {
                fos = 1;
            } else if (rExtramural.isChecked()) {
                fos = 2;
            }
            componentService.fillCmbSem(cmbSemester, currentDepartment.getIdInstitute(), fos, null);
        }
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
                report = service.getComissionsProtocolReport(service.getComissionsProtocols(currentSem.getIdSem(), currentDepartment.getIdChair()));
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

    @Listen("onChange = #dbTo, #dbFrom; onClick = #chbSign, #chbUnSign; onOK = #tbProtocolNumber, #tbSubjectname, #tbFioStudent, #tbGroupname")
    public void filterProtocolsList() {
        List<ChairsComissionsProtocolsModel> filteredByDateProtocolList = service.getComissionsProtocols(currentSem.getIdSem(), currentDepartment.getIdChair()); ;
        List<ChairsComissionsProtocolsModel> filteredBySignProtocolList = new ArrayList<>();
        if (currentSem != null) {
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
            if (tbGroupname.getValue() != null && !tbGroupname.getValue().equals("")) {
                filteredByDateProtocolList = filteredByDateProtocolList.stream().filter(el -> el.getGroupname().toLowerCase().contains(tbGroupname.getValue().toLowerCase())).collect(Collectors.toList());
            }

            filterdComissiosProtocols = filteredByDateProtocolList;
            lbComissionsProtocols.setModel(new ListModelList(filteredByDateProtocolList));
            lbComissionsProtocols.setItemRenderer(new TabComossionsProtocolChairRenderer());
        } else {
            PopupUtil.showWarning("Выберите семестр!");
        }
    }

}
