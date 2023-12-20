package org.edec.passportGroup.ctrl;

import org.edec.main.model.UserModel;
import org.edec.passportGroup.ctrl.renderer.GroupListRenderer;
import org.edec.passportGroup.ctrl.renderer.SemesterListRenderer;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.RegisterCurProgressGroupModel;
import org.edec.passportGroup.model.SemesterModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.CurrProgressReportXlsService;
import org.edec.passportGroup.service.impl.PassportGroupReportService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.passportGroup.service.impl.ReportsServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox typeOfSemesterFilter, formOfStudyFilter, courseFilter, cmbInst, cmbReport;

    @Wire
    private Groupbox gbSemester, gbInstitute, gbFormOfStudy;

    @Wire
    private Checkbox engineer, bachelor, master;

    @Wire
    private Textbox groupFilter, tbSearch;

    @Wire
    private Button btnCheckScholarship, btnEditSubject, btnShowGroup, btnShowStudents, btnPdfCurrentProgress, btnReportEok;

    @Wire
    private Listbox semesterList, groupList;

    private List<SemesterModel> semesters;
    private List<GroupModel> groups;
    private PassportGroupService service = new PassportGroupServiceESO();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private UserModel currentUser = template.getCurrentUser();
    private Long idChair = null;


    private ReportsServiceESO reports = new ReportsServiceESO();
    private PassportGroupReportService passportGroupReportService = new PassportGroupReportService();

    protected void fill () {
        semesterList.setItemRenderer(new SemesterListRenderer());
        groupList.setItemRenderer(new GroupListRenderer());
        if (currentModule.isReadonly()) {
            btnCheckScholarship.setVisible(false);
            btnCheckScholarship.setDisabled(true);
//            btnEditSubject.setVisible(false);
//            btnEditSubject.setDisabled(true);
        }

        typeOfSemesterFilter.setSelectedIndex(0);
        cmbReport.setModel(new ListModelList<>(ReportPasportGroup.values()));
        cmbReport.setItemRenderer(((comboitem, o, i) -> {
            comboitem.setValue(o);
            comboitem.setLabel(((ReportPasportGroup) o).name);
        }));
        engineer.setChecked(true);
        bachelor.setChecked(true);
        master.setChecked(true);

        componentService.fillCmbFormOfStudy(formOfStudyFilter, gbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbInst(cmbInst, gbInstitute, currentModule.getDepartments());
        if (currentModule.isReadonly() && currentModule.getRole().getName().equals("Заведующий кафедрой")){
            btnReportEok.setVisible(true);
            idChair = service.getIdChairByHumanface(currentUser.getIdHum());
            formOfStudyFilter.setSelectedIndex(1);
        }
        Events.echoEvent("onLater", cmbInst, null);
    }

    private void fillGroups () {
        if (idChair == null && currentModule.isReadonly() && currentModule.getRole().getName().equals("Заведующий кафедрой")){
            PopupUtil.showWarning("Кафедра не определена, обратитесь к администратору!");
            return;
        }

        if (semesterList.getSelectedIndex() != -1) {

            SemesterModel model = semesterList.getSelectedItem().getValue();

            groups = service.getGroupsByFilter(model.getIdSemester(), courseFilter.getSelectedIndex(), groupFilter.getValue(),
                    bachelor.isChecked(), master.isChecked(), engineer.isChecked(), idChair
            );

            ListModelList<GroupModel> listGroups = new ListModelList<>(groups);
            listGroups.setMultiple(true);
            groupList.setCheckmark(true);
            groupList.setModel(listGroups);
            groupList.renderAll();
            if (courseFilter.getItemCount() == 0) {
                if (groupList.getModel().getSize() > 0) {
                    int countOfCourse = ((GroupModel) groupList.getModel().getElementAt(groupList.getModel().getSize() - 1)).getCourse();
                    courseFilter.appendItem("Все");
                    for (int i = 1; i <= countOfCourse; ++i) {
                        courseFilter.appendItem(String.valueOf(i));
                    }
                }
            }
        }
    }

    @Listen("onSelect = #groupList")
    public void toggleBtnFromTable (Event event) {
        if (groupList.getSelectedCount() == 0) {
            btnShowGroup.setDisabled(true);
            btnEditSubject.setDisabled(true);
        } else if (groupList.getSelectedCount() == 1) {
            btnShowGroup.setDisabled(false);
            btnEditSubject.setDisabled(false);
        } else if (groupList.getSelectedCount() >= 1) {
            btnShowGroup.setDisabled(true);
            btnEditSubject.setDisabled(false);
        }
    }

    @Listen("onLater = #cmbInst")
    public void laterCmbInst (Event event) {
        if (cmbInst.getItemAtIndex(0).getLabel().equals("Все")) {
            cmbInst.removeItemAt(0);
        }
        cmbInst.setSelectedIndex(0);

        changeFilter();
    }

    @Listen("onChange = #cmbInst, #typeOfSemesterFilter, #formOfStudyFilter")
    public void changeFilter () {
        semesters = service.getSemestersByParams(((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                typeOfSemesterFilter.getSelectedIndex(),
                ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType()
        );

        semesterList.setModel(new ListModelList<>(semesters));
        semesterList.renderAll();

        semesterList.addEventListener(Events.ON_CLICK, event -> fillGroups());
    }

    @Listen("onChange = #courseFilter; onOK = #groupFilter; onClick = #engineer, #bachelor, #master")
    public void changeFilterGroup () {
        fillGroups();
    }

    // Кнопка "Отчет по группе"
    @Listen("onClick = #btnShowGroup;")
    public void showGroupReport () {
        if (groupList.getSelectedCount() == 1) {
            Map<String, Object> arg = new HashMap<>();
            arg.put("group", groupList.getSelectedItem().getValue());
            ComponentHelper.createWindow("/passportGroup/winGroupReport.zul", "winGroupReport", arg).doModal();
        } else if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Ни одной группы не выбрано");
        }
    }

    @Listen("onClick = #btnPassportGroupReport")
    public void showPassportGroupReport() {
        if(groupList.getSelectedItems().size() == 0) {
            PopupUtil.showInfo("Не выбрана ни одна группа");

            return;
        }

        JasperReport report = passportGroupReportService.getReport(groupList.getSelectedItems().stream().map(Listitem::<GroupModel>getValue).collect(Collectors.toList()));
        if (report != null) {
            report.showPdf();
        } else {
            PopupUtil.showInfo("Недостаточно данных для построения отчета");
        }
    }

    // Кнопка "Редактор предметов"
    @Listen("onClick = #btnEditSubject;")
    public void ShowSubject () {
        if (groupList.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите хотя бы один предмет");
        } else {
            try {
                Map<String, Object> arg = new HashMap<>();

                arg.put("groupList", groupList.getSelectedItems().stream().map(Listitem::getValue).collect(Collectors.toList()));

                ComponentHelper.createWindow("/passportGroup/winSubjectsGroups.zul", "winSubjectsGroups", arg).doModal();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Listen("onClick = #btnCheckScholarship;")
    public void ShowWindowCheckScholarship () {
        ComponentHelper.createWindow("/passportGroup/winCheckScholarship.zul", "winCheckScholarship", null).doModal();
    }

    @Listen("onClick = #btnShowReport;")
    public void showReport () {
        if (cmbReport.getSelectedIndex() == -1) {
            DialogUtil.exclamation("Выберите один из отчетов!");
            return;
        }
        ReportPasportGroup report = cmbReport.getSelectedItem().getValue();
        switch (report) {
            // Отчет "Список студентов"
            case STUDENT_LIST:
                showStudentlistReport();
                break;
            // Отчет "Должники"
            case DEBTORDS:
                showDebtorsReport();
                break;
            // Отчет "Должники (Бюджет)"
            case DEBTORS_GOVERNMENT:
                showDebtorsBudgetReport();
                break;
            // Отчет "Должники (Договор)"
            case DEBTORS_CONTRACTOR:
                showDebtorsNotBudgetReport();
                break;
            // Отчет "Справка декана"
            case DECAN:
                showDecanReport();
                break;
            // Отчет "Формы контроля"
            case FORM_CONTROL:
                showFormControlReport();
                break;
            // Отчет "Паспорт групп"
            case PASSPORT_GROUP:
                downloadReportPassportGroup();
                break;
            // Отчет "Текущая успеваемость (xls)"
            case CUCURRENT_PROGRESS:
                showXlsCurProgress();
                break;
        }
    }

    /**
     * Скачать договор по паспорту групп
     */
    private void downloadReportPassportGroup () {
        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите одну или более групп");
            return;
        }
        org.edec.report.passportGroup.service.PassportGroupReportService passportGroupReportService = new org.edec.report.passportGroup.service.PassportGroupReportService();
        List<GroupModel> tempListOfGroup = new ArrayList<>();
        for (Listitem li : groupList.getSelectedItems()) {
            tempListOfGroup.add(li.getValue());
        }
        try {
            Filedownload.save(
                    passportGroupReportService.generatePasportGroupXls(semesterList.getSelectedItem().getValue(), tempListOfGroup));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Список студентов
     */
    private void showStudentlistReport () {

        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну группу");
            return;
        }
        String rep = reports.getStudentlistReport(new ArrayList<>(groupList.getSelectedItems()),
                ((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue()
        );
        Filedownload.save(rep, "text/xml", "Список студентов (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Справка декана
     */
    private void showDecanReport () {
        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну группу");
            return;
        }
        String rep = reports.getDecanReport(new ArrayList<>(groupList.getSelectedItems()), groupList.getSelectedItems().size());
        Filedownload.save(rep, "text/xml", "Справка декана (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Отчет по должникам
     */
    private void showDebtorsReport () {
        String rep = reports.getDebtorsReport(typeOfSemesterFilter.getSelectedIndex(),
                ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst(),
                ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                courseFilter.getSelectedIndex(), groupFilter.getValue(), bachelor.isChecked(),
                master.isChecked(), engineer.isChecked(), formOfStudyFilter.getText()
        );
        Filedownload.save(rep, "text/xml", "Отчет по должникам (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    /**
     * Должники
     */
    private void showDebtorsBudgetReport () {
        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну группу");
            return;
        }
        String rep = reports.getReportDeb(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue(), 1,
                ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                new ArrayList<>(groupList.getSelectedItems())
        );
        DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH.mm");

        Date today = java.util.Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Filedownload.save(rep, "text/xml", "Должники(" + df.format(today) + ").xls");
    }

    /**
     * Должники-договорники
     */
    private void showDebtorsNotBudgetReport () {
        if (groupList.getSelectedCount() == 0) {
            PopupUtil.showWarning("Выберите хотя бы одну группу");
            return;
        }
        String rep = reports.getReportDeb(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester().intValue(), 0,
                ((FormOfStudy) formOfStudyFilter.getSelectedItem().getValue()).getType(),
                new ArrayList<>(groupList.getSelectedItems())
        );
        DateFormat df = new SimpleDateFormat("MM.dd.yyyy HH.mm");

        Date today = java.util.Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        Filedownload.save(rep, "text/xml", "Должники-Договор(" + df.format(today) + ").xls");
    }

    /**
     * Формы-контроля за семестр
     */
    private void showFormControlReport () {
        String rep = reports.getFormControlReport(((SemesterModel) semesterList.getSelectedItem().getValue()).getIdSemester());
        Filedownload.save(rep, "text/xml", "Отчет по формам-контролю (" + DateConverter.convertTimestampToString(new Date()) + ").xls");
    }

    public enum ReportPasportGroup {
        STUDENT_LIST("Список студентов"), DEBTORS_CONTRACTOR("Должники (договор)"), DEBTORS_GOVERNMENT("Должники (бюджет)"), FORM_CONTROL(
                "Форма-контроль"), DECAN("Справка декана"), DEBTORDS("Должники (в разработке)"), PASSPORT_GROUP("Паспорт групп"),
        CUCURRENT_PROGRESS("Текущая успеваемость (xls)");
        private String name;

        ReportPasportGroup (String name) {
            this.name = name;
        }
    }

    @Listen("onClick = #btnPdfCurrentProgress;")
    public void showPdfCurProgress() {
        List<Long> listIDlgs = new ArrayList<>();
        for (int i = 0; i < groupList.getSelectedItems().size(); ++i) {
            Listitem selectedItem = (Listitem) groupList.getSelectedItems().toArray()[i];
            listIDlgs.add(selectedItem.<GroupModel>getValue().getIdLgs());
        }

        JasperReportService jasperService = new JasperReportService();

        JasperReport jasperReport = jasperService.getRegisterCurrentProgress(listIDlgs);
        jasperReport.showPdf();
    }

    private void showXlsCurProgress() {
        List<Long> listIDlgs = new ArrayList<>();
        CurrProgressReportXlsService currProgressReportXlsService = new CurrProgressReportXlsService();
        for (int i = 0; i < groupList.getSelectedItems().size(); ++i) {
            Listitem selectedItem = (Listitem) groupList.getSelectedItems().toArray()[i];
            listIDlgs.add(selectedItem.<GroupModel>getValue().getIdLgs());
        }
        if (!listIDlgs.isEmpty()) {
            Filedownload.save(currProgressReportXlsService.getReportCurProgressXls(listIDlgs));
        } else {
            PopupUtil.showError("Выберите группу!");
            return;
        }
    }

    @Listen("onClick = #btnReportEok")
    public void getXlsReportEok(){
        if (semesterList.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите семестр");
            return;
        }
        Filedownload.save(reports.getReportEok(semesterList.getSelectedItem().getValue(), idChair));
    }
}