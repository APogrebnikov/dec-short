package org.edec.studentPassport.ctrl;

import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.studentPassport.ctrl.renderer.StudentRenderer;
import org.edec.studentPassport.model.filter.StudentPassportFilter;
import org.edec.studentPassport.service.StudentPassportService;
import org.edec.studentPassport.service.impl.StudentPassportServiceESOimpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.Date;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Checkbox chOnlyDebtors;
    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbSemester;
    @Wire
    private Groupbox gbFilter;
    @Wire
    private Listbox lbStudent;
    @Wire
    private Listheader lhrIndexCard;
    @Wire
    private Textbox tbFio, tbRecordbook, tbGroup;
    @Wire
    private Vbox vbInst, vbFormOfStudy;

    private StudentPassportService stPassportService = new StudentPassportServiceESOimpl();
    private ComponentService componentService = new ComponentServiceESOimpl();

    private ReportsServiceESO reports = new ReportsServiceESO();

    protected void fill () {
        lbStudent.setItemRenderer(new StudentRenderer(this, currentModule));
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbSem(cmbSemester, ((Integer) (1)).longValue(), 1, null);
        if (currentModule.isReadonly()) {
            lhrIndexCard.setVisible(false);
        }
    }

    @Listen("onOK=#tbFio; onOK=#tbRecordbook; onOK=#tbGroup; onCheck = #chOnlyDebtors; onClick = #btnSearch;")
    public void searchStudents () {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, null);
    }

    @Listen("onLater = #lbStudent")
    public void laterLbStudent (Event event) {

        StudentPassportFilter filter = StudentPassportFilter.builder()
                .fio(tbFio.getValue()).recordBook(tbRecordbook.getValue())
                .groupName(tbGroup.getValue())
                .inst(cmbInst.getSelectedItem().getValue())
                .formOfStudy(cmbFormOfStudy.getSelectedItem().getValue())
                .onlyDebtors(chOnlyDebtors.isChecked())
                .build();

        lbStudent.setModel(new ListModelList<>(stPassportService.getStudentsByFilter(filter)));
        lbStudent.renderAll();
        if (event.getData() != null) {
            int index = (int) event.getData();
            //lbStudent.setSelectedIndex(index);
            lbStudent.renderItem(lbStudent.getItemAtIndex(index));
        }
        Clients.clearBusy(lbStudent);
    }

    @Listen("onClick = #btnGetReport")
    public void GetReport (Event event) {
        if (cmbSemester.getSelectedIndex() == -1) {
            return;
        }

        String rep = reports.getGoncharicReport(cmbInst.getSelectedIndex(), cmbInst.getText(),
                                                ((SemesterModel) cmbSemester.getSelectedItem().getValue()).getIdSem(),
                                                cmbSemester.getText(), cmbFormOfStudy.getSelectedIndex()
        );

        Filedownload.save(
                rep, "text/xml", "Отчет [" + cmbSemester.getText() + "](" + DateConverter.convertTimestampToString(new Date()) + ").xml");
    }

    public void updateLb (int index) {
        Clients.showBusy(lbStudent, "Загрузка данных");
        Events.echoEvent("onLater", lbStudent, index);
    }
}
