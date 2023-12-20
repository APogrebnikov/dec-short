package org.edec.secretaryChair.ctrl;

import org.edec.main.model.DepartmentModel;
import org.edec.register.model.RegisterModel;
import org.edec.secretaryChair.ctrl.renderer.CommissionRenderer;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.model.comporator.CommissionModelComp;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.secretaryChair.service.impl.SecretaryChairImpl;
import org.edec.utility.component.model.SemesterModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Главная страница для секретарей кафедры для управлениями комиссиями
 */
public class IndexPageCtrl extends CabinetSelector {

    //Компоненты
    @Wire
    private Checkbox chSigned, chbPrintGroups, chbPrintMembersComm;
    @Wire
    private Combobox cmbSem, cmbDepartment;
    @Wire
    private Listbox lbCommission;
    @Wire
    private Listheader lhrSubject, lhrSemester, lhrDateCommission;
    @Wire
    private Vbox vbDepartment;
    @Wire
    private Textbox tbComissionSubjectFilter;

    //Сервисы
    private ComponentService componentService = new ComponentServiceESOimpl();
    private JasperReportService jasperReportService = new JasperReportService();
    private SecretaryChairService chairService = new SecretaryChairImpl();

    //Данные
    private List<CommissionModel> listCommission;
    int currentIndex = 0;

    @Override
    protected void fill() {
        lhrDateCommission.setSortAscending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_DATE));
        lhrDateCommission.setSortDescending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_DATE_REV));
        lhrSemester.setSortAscending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_SEMESTER));
        lhrSemester.setSortDescending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_SEMESTER_REV));
        lhrSubject.setSortAscending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_SUBJECT));
        lhrSubject.setSortDescending(new CommissionModelComp(CommissionModelComp.CompareMethods.BY_SUBJECT_REV));
        lbCommission.setItemRenderer(new CommissionRenderer(this));
        componentService.fillCmbDepartment(cmbDepartment, vbDepartment, currentModule.getDepartments());
        Events.echoEvent("onChange", cmbDepartment, null);
    }

    @Listen("onChange = #cmbDepartment")
    public void changeDepartment() {
        cmbSem.getItems().clear();
        DepartmentModel selectedDepartment = cmbDepartment.getSelectedItem().getValue();
        List<SemesterModel> semesters = chairService.getSemesterByChair(selectedDepartment.getIdChair(), currentModule.getFormofstudy());
        new Comboitem("Все").setParent(cmbSem);
        for (SemesterModel semester : semesters) {
            Comboitem ci = new Comboitem(DateConverter.convert2dateToString(semester.getDateOfBegin(), semester.getDateOfEnd()) + " " +
                                         (semester.getSeason() == 0 ? "осень" : "весна") + " " +
                                         (semester.getFormofstudy() == 1 ? "(очная)" : "(заочная)"));
            ci.setParent(cmbSem);
            ci.setValue(semester);
        }
        cmbSem.setSelectedIndex(0);
        refreshLb("Загрузка данных", null);
    }

    @Listen("onChange = #cmbSem; onCheck = #chSigned;")
    public void changeSem() {
        refreshLb("Загрузка данных", null);
    }

    @Listen("onLater = #lbCommission")
    public void laterOnCommission(Event e) {
        currentIndex = lbCommission.getSelectedIndex();
        setInfo(e.getData() instanceof CommissionModelComp ? (CommissionModelComp) e.getData() : null);
        Clients.clearBusy(lbCommission);
        if (e.getData() != null && e.getData() instanceof CommissionModel) {
            CommissionModel commission = (CommissionModel) e.getData();
            lbCommission.setSelectedIndex(currentIndex);
            if (lbCommission.getSelectedItem() != null) {
                lbCommission.getSelectedItem().focus();
            }
            /*
            int index = listCommission.indexOf(listCommission.stream()
                    .filter(commissionModel -> commission.getId().equals(commissionModel.getId()))
                    .findFirst().orElse(null));
            lbCommission.setSelectedIndex(index);
            lbCommission.getSelectedItem().focus();
             */
        }
    }

    @Listen("onClick = #btnScheduleReport")
    public void showSchedule() {
        DepartmentModel selectedDepartment = cmbDepartment.getSelectedItem().getValue();
        try {
            jasperReportService.getJasperRepotCommissionSchedule(currentModule.getFormofstudy(), selectedDepartment.getIdChair(),chbPrintGroups.isChecked(), chbPrintMembersComm.isChecked()).showPdf();
        } catch (Exception e) {
            PopupUtil.showError("Открыть расписание невозможно");
            e.printStackTrace();
        }
    }
    @Listen("onClick = #btnScheduleReportXLS")
    public void showScheduleExcel() throws ClassNotFoundException {

    }

    @Listen("onSort = #lhrSubject")
    public void sortSubject() {
        refreshLb("Сортировка", getComporatorByListhead(lhrSubject));
    }

    @Listen("onSort = #lhrSemester")
    public void sortSemester() {
        refreshLb("Сортировка", getComporatorByListhead(lhrSemester));
    }

    @Listen("onSort = #lhrDateCommission")
    public void sortCommissionDate() {
        refreshLb("Сортировка", getComporatorByListhead(lhrDateCommission));
    }

    private void setInfo(CommissionModelComp comparator) {
        listCommission = chairService.getCommission(
                cmbSem.getSelectedItem().getValue() == null ? null : ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem(),
                ((DepartmentModel) cmbDepartment.getSelectedItem().getValue()).getIdChair(), currentModule.getFormofstudy(),
                chSigned.isChecked()
        );
        List<CommissionModel> filtredList = filterComissions();
        if (comparator != null) {
            filtredList.sort(comparator);
        }
        lbCommission.setModel(new ListModelList<>(filtredList));
        lbCommission.renderAll();
    }

    private CommissionModelComp getComporatorByListhead(Listheader lh) {
        if (lh.getSortDirection().equals("natural") || lh.getSortDirection().equals("descending")) {
            return (CommissionModelComp) lh.getSortAscending();
        } else {
            return (CommissionModelComp) lh.getSortDescending();
        }
    }

    void refreshLb(String msg, Object data) {
        Clients.showBusy(lbCommission, msg);
        Events.echoEvent("onLater", lbCommission, data);
    }


    @Listen("onOK = #tbComissionSubjectFilter")
    public List<CommissionModel> filterComissions() {
        List<CommissionModel> filteredList = new ArrayList<>();

        if (listCommission != null) {
            for (CommissionModel registerModel : listCommission) {
                if (!tbComissionSubjectFilter.getValue().equals("")) {
                    if (!registerModel.getSubjectName().toLowerCase().contains(tbComissionSubjectFilter.getValue().toLowerCase())) {
                        continue;
                    }
                    filteredList.add(registerModel);
                } else {
                    // В случае, если нет рабочих фильтров
                    filteredList.add(registerModel);
                }
            }
        }
        lbCommission.setModel(new ListModelList<>(filteredList));
        lbCommission.renderAll();
        return filteredList;
    }
}