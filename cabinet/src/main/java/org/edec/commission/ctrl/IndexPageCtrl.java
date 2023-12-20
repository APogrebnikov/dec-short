package org.edec.commission.ctrl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.edec.commission.ctrl.renderer.SubjectDebtRenderer;
import org.edec.commission.model.comporator.ComissionComparator;
import org.edec.commission.component.WinDebtorReport;
import org.edec.commission.ctrl.renderer.StudentCountDebtRenderer;
import org.edec.commission.ctrl.renderer.StudentDebtGroupRenderer;
import org.edec.commission.model.PeriodCommissionModel;
import org.edec.commission.model.StudentCountDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.commission.report.model.NotionModel;
import org.edec.commission.report.model.notion.NotionFilter;
import org.edec.commission.report.model.notion.NotionStudentModel;
import org.edec.commission.report.model.schedule.FormOfStudyModel;
import org.edec.commission.report.model.schedule.ScheduleChairModel;
import org.edec.commission.report.service.CommissionDataManager;
import org.edec.commission.report.service.NotionDataManager;
import org.edec.commission.service.CommissionReportService;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionReportImpl;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.model.SemesterModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.report.сomissionReport.StudentListByFocService;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.ReportCommissionConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.report.model.jasperReport.JasperReport;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class IndexPageCtrl extends CabinetSelector {

    //Создание комиссии
    @Wire
    private Checkbox chBachelor, chEngineer, chMaster, chProlongation;
    @Wire
    private Combobox cmbGovernment, cmbCourse, cmbTypeDebt;
    @Wire
    private Datebox dbBeginCommission, dbEndCommission, dbProlongation;
    @Wire
    private Listbox lbSemester, lbFoundStudents, lbStudentForGrouping;
    @Wire
    private Spinner spinDebt;
    @Wire
    private Textbox tbFioFoundStudent, tbGroupFoundStudent, tbFioStudentForGrouping, tbGroupStudentForGrouping;
    @Wire
    private Vbox vbFormOfStudy, vbInst;

    //Просмотр комиссиии
    @Wire
    private Combobox cmbPeriodCommission, cmbSem, cmbReportPdf, cmbReportDocx, cmbReportExcel, cmbInst, cmbFormOfStudy, cmbPeriodComGraduate;
    @Wire
    private Listbox lbShowCommission;
    @Wire
    private Textbox tbSearchComission;
    @Wire
    private Listheader lhSubject, lhChair;

    private CommissionDataManager dataManager = new CommissionDataManager();
    private CommissionService commissionService = new CommissionServiceESOimpl();
    private CommissionReportService commissionReportService = new CommissionReportImpl();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private PassportGroupService service = new PassportGroupServiceESO();
    private JasperReportService jasperReportService = new JasperReportService();

    private Integer formOfStudy;
    private Long idInst;
    public String listIdSem;
    private List<org.edec.passportGroup.model.SemesterModel> semesters;
    private List<StudentCountDebtModel> listStudentDebt = new ArrayList<StudentCountDebtModel>();
    private List<StudentCountDebtModel> listStudentGroupDebt = new ArrayList<StudentCountDebtModel>();

    @Override
    protected void fill() {
        //formOfStudy = currentModule.getFormofstudy();
        cmbReportPdf.setModel(new ListModelList<>(ReportCommissionConst.getPdfReports()));
        cmbReportDocx.setModel(new ListModelList<>(ReportCommissionConst.getDocxReports()));

        lbFoundStudents.setItemRenderer(new StudentCountDebtRenderer(this));
        lbStudentForGrouping.setItemRenderer(new StudentDebtGroupRenderer(this));
        //this.initListboxSemester();
        componentService.fillCmbFormOfStudy(cmbFormOfStudy, vbFormOfStudy, currentModule.getFormofstudy());
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        Events.echoEvent("onLater", cmbInst, null);

        setSortMethods();
    }

    public void setSortMethods() {
        lhSubject.setSortAscending(new ComissionComparator(ComissionComparator.CompareMethods.BY_SUBJ));
        lhSubject.setSortDescending(new ComissionComparator(ComissionComparator.CompareMethods.BY_SUBJ_REV));

        lhChair.setSortAscending(new ComissionComparator(ComissionComparator.CompareMethods.BY_CHAIR));
        lhChair.setSortDescending(new ComissionComparator(ComissionComparator.CompareMethods.BY_CHAIR_REV));
    }

    @Listen("onLater = #cmbInst")
    public void laterCmbInst(Event event) {
        /*if (cmbInst.getItemAtIndex(0).getLabel().equals("Все")) {
            cmbInst.removeItemAt(0);
        }*/
        cmbInst.setSelectedIndex(0);
        initListboxSemester();
    }

    @Listen("onChange = #cmbInst, #cmbFormOfStudy")
    public void changeFilter() {
        initListboxSemester();
    }

    @Listen("onOK = #tbFioFoundStudent, #tbGroupFoundStudent")
    public void searchByStudentFio(){
        studentSearch(tbFioFoundStudent,tbGroupFoundStudent, listStudentDebt, lbFoundStudents);
    }

    @Listen("onOK = #tbGroupStudentForGrouping, tbGroupStudentForGrouping")
    public void searchByGroupStudent(){
        studentSearch(tbFioStudentForGrouping,tbGroupStudentForGrouping,listStudentGroupDebt,lbStudentForGrouping);
    }

    private void studentSearch(Textbox tbFioStudent, Textbox tbGroupStudent, List<StudentCountDebtModel> studentList, Listbox lb){
        ListModelList lmList = new ListModelList<>(
                commissionService.getStudentsCountDebt(tbFioStudent.getValue(), tbGroupStudent.getValue(), studentList));
        lmList.setMultiple(true);
        lb.setModel(lmList);
        lb.renderAll();
    }

    @Listen("onCheckSelectAll = #lbFoundStudents")
    public void selectAllLbStudent() {
        checkSelectAllListbox(lbFoundStudents, "checkSelectAllLbStudent");
    }

    @Listen("onCheckSelectAll = #lbStudentForGrouping")
    public void selectAllLbGroup() {
        checkSelectAllListbox(lbStudentForGrouping, "checkSelectAllLbGroup");
    }

    private void checkSelectAllListbox(Listbox listbox, String nameAttribute) {
        if (listbox.getItems().size() != 0) {
            if ((Boolean) listbox.getAttribute(nameAttribute)) {
                listbox.selectAll();
                listbox.setAttribute(nameAttribute, false);
            } else {
                listbox.clearSelection();
                listbox.setAttribute(nameAttribute, true);
            }
        }
    }

    @Listen("onClick = #btnMoveToGroup")
    public void addAllSelectStudent() {
        if (lbFoundStudents.getSelectedItems().size() == 0) {
            PopupUtil.showWarning("Необходимо отметить галочкой студентов, которых вы хотите перенести в таблицу для группировки");
            return;
        }

        if (lbFoundStudents.getSelectedItems().size() == lbFoundStudents.getItems().size()) {
            listStudentGroupDebt = new ArrayList<>(listStudentDebt);
            List<StudentCountDebtModel> list = lbFoundStudents.getSelectedItems().stream()
                                                              .map(listitem -> (StudentCountDebtModel) listitem.getValue())
                                                              .collect(Collectors.toList());
            List<StudentCountDebtModel> previousList = lbStudentForGrouping.getItems().stream()
                                                                           .map(listitem -> (StudentCountDebtModel) listitem.getValue())
                                                                           .collect(Collectors.toList());
            list.addAll(previousList);
            ListModelList lmGroup = new ListModelList<>(list);
            lmGroup.setMultiple(true);
            lbStudentForGrouping.setModel(lmGroup);
            lbStudentForGrouping.renderAll();
            return;
        }

        for (Listitem li : lbFoundStudents.getSelectedItems()) {
            StudentCountDebtModel studentCountDebtModel = li.getValue();
            if (listStudentGroupDebt.contains(studentCountDebtModel)) {
                continue;
            }
            listStudentGroupDebt.add(studentCountDebtModel);
        }

        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    @Listen("onClick = #btnRemove")
    public void removeAllSelectStudent() {
        if (lbStudentForGrouping.getSelectedItems().size() == 0) {
            PopupUtil.showWarning("Необходимо отметить галочкой студентов, которых вы хотите удалить");
            return;
        }

        if (lbStudentForGrouping.getSelectedItems().size() == lbStudentForGrouping.getItems().size()) {
            listStudentGroupDebt.clear();
            ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
            lbStudentForGrouping.setModel(lmGroup);
            lbStudentForGrouping.renderAll();
            return;
        }

        for (Listitem li : lbStudentForGrouping.getSelectedItems()) {
            StudentCountDebtModel studentCountDebtModel = li.getValue();
            listStudentGroupDebt.remove(studentCountDebtModel);
        }

        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    @Listen("onClick = #btnSearch")
    public void search() {
        Clients.showBusy(lbFoundStudents, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbFoundStudents, null);
    }

    @Listen("onLater = #lbFoundStudents")
    public void laterOnLbStudent() {
        this.initListboxStudent();
        Clients.clearBusy(lbFoundStudents);
    }

    @Listen("onClick = #btnGoToCreateCommission")
    public void goToCreateCommission() {
        if (dbBeginCommission.getValue() == null || dbEndCommission.getValue() == null) {
            PopupUtil.showWarning("Введите обе даты!");
            return;
        }
        if (dbBeginCommission.getValue() != null && dbEndCommission.getValue() != null && dbBeginCommission.getValue().after(dbEndCommission.getValue())) {
            PopupUtil.showWarning("Дата начала не может быть позже даты окончания периода комиссии!");
            return;
        }
        if (listStudentGroupDebt.size() == 0) {
            PopupUtil.showWarning("Выберите студентов для группировки");
            return;
        }
        StringBuilder listIdSSS = new StringBuilder();
        List<StudentCountDebtModel> list = lbStudentForGrouping.getItems().stream().map(item -> (StudentCountDebtModel) item.getValue())
                                                               .collect(Collectors.toList());

        for (StudentCountDebtModel student : list) {
            listIdSSS.append(student.getListSSS()).append(",");
        }

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinCreateCommission.LIST_ID_SSS, listIdSSS.substring(0, listIdSSS.length() - 1));
        arg.put(WinCreateCommission.FORM_OF_STUDY, formOfStudy);
        arg.put(WinCreateCommission.DATE_BEGIN_COMMISSION, dbBeginCommission.getValue());
        arg.put(WinCreateCommission.DATE_END_COMMISSION, dbEndCommission.getValue());

        ComponentHelper.createWindow("winCreateCommission.zul", "winCreateCommission", arg).doModal();
    }

    @Listen("onSelect = #tabShowCommission")
    public void selectedTabShowComission() {
        tbSearchComission.setValue("");
        addComboitem(cmbSem, commissionService.getAllSemesterWithCommission(formOfStudy));
        initComboboxPeriodCommission();
        Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbShowCommission, null);
    }

    @Listen("onClick = #btnSearchComission; onOK = #tbSearchComission; onChange = #cmbPeriodCommission;")
    public void searchCommission() {
        // костыль для Иевлевой для отображения комиссий у выпускников 2020
        PeriodCommissionModel graduatesPeriod = cmbPeriodCommission.getSelectedItem().getValue();
        if (graduatesPeriod != null && graduatesPeriod.isGraduate()){
            cmbSem.setVisible(false);
            cmbPeriodComGraduate.setVisible(true);
            cmbPeriodComGraduate.getItems().clear();
            List<PeriodCommissionModel> periods = commissionService.getCommissionForGraduteBySem(formOfStudy, graduatesPeriod.getIdSem());
            Comboitem ciAll = new Comboitem();
            ciAll.setLabel("Все");
            ciAll.setParent(cmbPeriodComGraduate);
            for (PeriodCommissionModel period : periods) {
                Comboitem ci = new Comboitem(DateConverter.convertDateToString(period.getDateOfBegin()) + "-" +
                        DateConverter.convertDateToString(period.getDateOfEnd()));
                ci.setParent(cmbPeriodComGraduate);
                ci.setValue(period);
            }
            cmbPeriodComGraduate.setSelectedItem(ciAll);
            List<SubjectDebtModel> list = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),graduatesPeriod.getIdSem(),
                    cmbPeriodCommission.getSelectedItem().getValue(), formOfStudy);

            lbShowCommission.setModel(new ListModelList<>(list));
            lbShowCommission.setItemRenderer(new SubjectDebtRenderer(this));
            lbShowCommission.renderAll();
        } else {
            cmbSem.setVisible(true);
            cmbPeriodComGraduate.setVisible(false);
            Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
            Events.echoEvent("onLater", lbShowCommission, null);

        }
    }

    @Listen("onChange = #cmbSem")
    public void changeSem() {
        Clients.showBusy(lbShowCommission, "Загрузка данных из БД");
        Events.echoEvent("onLater", lbShowCommission, null);
    }
//// костыль для Иевлевой для отображения комиссий у выпускников 2020
    @Listen("onChange = #cmbPeriodComGraduate")
    public void changeGraduatesComissionByPeriod() {
        PeriodCommissionModel graduatesPeriod = cmbPeriodCommission.getSelectedItem().getValue();
        if (graduatesPeriod != null && graduatesPeriod.isGraduate()){
            cmbSem.setVisible(false);
            cmbPeriodComGraduate.setVisible(true);
            PeriodCommissionModel selectedPeriod = cmbPeriodComGraduate.getSelectedItem().getValue();
            List<SubjectDebtModel> list;
            if (selectedPeriod != null) {
                list = commissionService.getSubjectCommissionGraduateByPeriod(tbSearchComission.getValue(),graduatesPeriod.getIdSem(),
                        cmbPeriodComGraduate.getSelectedItem().getValue(), formOfStudy);
            } else {
                list = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),graduatesPeriod.getIdSem(),
                        cmbPeriodCommission.getSelectedItem().getValue(), formOfStudy);
            }
            lbShowCommission.setModel(new ListModelList<>(list));
            lbShowCommission.setItemRenderer(new SubjectDebtRenderer(this));
            lbShowCommission.renderAll();
        }
    }


    @Listen("onLater = #lbShowCommission")
    public void laterForComission() {
        List<SubjectDebtModel> list = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),
                                                                                           cmbSem.getSelectedIndex() == 0
                                                                                           ? null
                                                                                           : ((SemesterModel) cmbSem.getSelectedItem()
                                                                                                                    .getValue()).getIdSem(),
                                                                                           cmbPeriodCommission.getSelectedItem().getValue(),
                                                                                           formOfStudy
        );

        lbShowCommission.setModel(new ListModelList<>(list));
        lbShowCommission.setItemRenderer(new SubjectDebtRenderer(this));
        lbShowCommission.renderAll();
        Clients.clearBusy(lbShowCommission);
    }

    @Listen("onClick = #btnShowPdf")
    public void showPdf() {
        if (cmbReportPdf.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите один из отчетов");
            return;
        }
        ReportCommissionConst report = cmbReportPdf.getSelectedItem().getValue();
        switch (report) {
            case SCHEDULE:
                getScheduleJasper().showPdf();
                break;
            case NOTION:
                showNotion(false);
                break;
            case LIST_OF_STUDENT_BY_CHAIR:
                showFilterStudentsByChair(false);
                break;
            case LIST_OF_STUDENT_BY_FOS:
                JasperReport jasperReport = getScheduleByFormOfStudyJasper();
                if (jasperReport != null) {
                    jasperReport.showPdf();
                }
                break;
            default:
                PopupUtil.showError("Такой вид отчетов не обрабатывается");
        }
    }

    @Listen("onClick = #btnDownloadDocx")
    public void downloadDocx() throws IOException {
        if (cmbReportDocx.getSelectedItem() == null) {
            PopupUtil.showWarning("Выберите один из отчетов");
            return;
        }
        ReportCommissionConst report = cmbReportDocx.getSelectedItem().getValue();
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        switch (report) {
            case SCHEDULE:
                getScheduleJasper().downloadDocx();
                break;
            case NOTION:
                showNotion(true);
                break;
            case LIST_OF_STUDENT_BY_CHAIR:
                showFilterStudentsByChair(true);
                break;
            case LIST_OF_STUDENT_BY_FOS:
//                JasperReport jasperReport = getScheduleByFormOfStudyJasper();
//                if (jasperReport != null) {
//                    jasperReport.downloadDocx();
//                }
                PeriodCommissionModel periodCommissions = cmbPeriodCommission.getSelectedItem().getValue();
                Date dateOfBegin = periodCommissions.getDateOfBegin();
                Date dateOfEnd = periodCommissions.getDateOfEnd();
                List<FormOfStudyModel> listStudentForComissionByFos = dataManager.getListStudentForComissionByFos(dateOfBegin, dateOfEnd, formOfStudy);
                XWPFDocument reportdoc = new StudentListByFocService().createDocReport(listStudentForComissionByFos, dateOfBegin, dateOfEnd );
                File file = new File("Общий список студентов (по форме обучения).docx");
                FileOutputStream outFile = new FileOutputStream(file);
                reportdoc.write(outFile);
                Filedownload.save(file, null);
                break;
            case LIST_OF_STUDENT:
                AMedia aMedia = new AMedia("Список студентов  (" + DateConverter.convertDateToString(new Date()) + ").xls", "xls",
                                           "application/xls", commissionReportService
                                                   .getXlsxForStudentCommission(formOfStudy, periodCommission.getDateOfBegin(),
                                                                                periodCommission.getDateOfEnd()
                                                   )
                );
                Filedownload.save(aMedia);
                break;
            default:
                PopupUtil.showError("Такой вид отчетов не обрабатывается");
        }
    }

    @Listen("onClick = #btnDebtorReports")
    public void clickBtnDebtorReports() {
        WinDebtorReport winDebtorReport = new WinDebtorReport(formOfStudy, idInst);
        winDebtorReport.setParent(vbFormOfStudy);
        winDebtorReport.doModal();
    }

    private void showNotion(boolean docx) {

        List<NotionStudentModel> notions = getNewNotions();

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinSelectStudentForNotion.STUDENTS, notions);
        arg.put(WinSelectStudentForNotion.IS_DOCX, docx);

        ComponentHelper.createWindow("winSelectStudentForNotion.zul", "winSelectStudentForNotion", arg).doModal();
    }

    private JasperReport getScheduleJasper() {
        JRBeanCollectionDataSource jrBeanCollectionDataSource = dataManager.getSchedule(commissionService
                                                                                                .getSubjectCommissionByFilterAndSem(
                                                                                                        tbSearchComission.getValue(),
                                                                                                        cmbSem.getSelectedIndex() == 0
                                                                                                        ? null
                                                                                                        : ((SemesterModel) cmbSem
                                                                                                                .getSelectedItem()
                                                                                                                .getValue()).getIdSem(),
                                                                                                        cmbPeriodCommission
                                                                                                                .getSelectedItem()
                                                                                                                .getValue(), formOfStudy
                                                                                                ));

        return jasperReportService.getScheduleJasper(jrBeanCollectionDataSource);
    }

    private void showFilterStudentsByChair(Boolean docx) {
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        List<ScheduleChairModel> chairs = dataManager
                .getListOfStudentByChair(periodCommission.getDateOfBegin(), periodCommission.getDateOfEnd(), formOfStudy);

        Map<String, Object> arg = new HashMap<>();
        arg.put(WinFilterStudentsByChairCtrl.CHAIRS, chairs);
        arg.put(WinFilterStudentsByChairCtrl.DOCX, docx);

        ComponentHelper.createWindow("winFilterStudentsByChair.zul", "winFilterStudentsByChair", arg).doModal();
    }

    private JasperReport getScheduleByFormOfStudyJasper() {
        PeriodCommissionModel periodCommission = cmbPeriodCommission.getSelectedItem().getValue();
        Date dateOfBegin = periodCommission.getDateOfBegin();
        Date dateOfEnd = periodCommission.getDateOfEnd();
        JRBeanCollectionDataSource jrBeanCollectionDataSource = dataManager.getScheduleByFormOfStudy(dateOfBegin, dateOfEnd, formOfStudy);

        return jasperReportService.getScheduleByFormOfStudyJasper(dateOfBegin, dateOfEnd, jrBeanCollectionDataSource);
    }

    private List<NotionStudentModel> getNewNotions() {

        NotionDataManager notionDataManager = new NotionDataManager();

        NotionFilter notionFilter = NotionFilter.builder().commonFilter(tbSearchComission.getValue()).formOfStudy(formOfStudy)
                                                .idInst(idInst).idSemester(
                        cmbSem.getSelectedIndex() == 0 ? null : ((SemesterModel) cmbSem.getSelectedItem().getValue()).getIdSem())
                                                .period(cmbPeriodCommission.getSelectedItem().getValue()).build();

        return notionDataManager.getNotionsModelByFilter(notionFilter);
    }

    private List<NotionModel> getNotions() {
        List<SubjectDebtModel> listComissions = commissionService.getSubjectCommissionByFilterAndSem(tbSearchComission.getValue(),
                                                                                                     cmbSem.getSelectedIndex() == 0
                                                                                                     ? null
                                                                                                     : ((SemesterModel) cmbSem
                                                                                                             .getSelectedItem().getValue())
                                                                                                             .getIdSem(),
                                                                                                     cmbPeriodCommission.getSelectedItem()
                                                                                                                        .getValue(),
                                                                                                     formOfStudy
        );

        CommissionDataManager dataManager = new CommissionDataManager();
        return dataManager.getListForNotionByListComissions(listComissions);
    }

    public void removeStudentDebt(Listitem selectedItem) {
        StudentCountDebtModel studentCountDebtModel = selectedItem.getValue();
        listStudentGroupDebt.remove(studentCountDebtModel);
        lbStudentForGrouping.getItems().remove(selectedItem);
    }

    public void addStudentRetake(Listitem selectedItem) {
        StudentCountDebtModel studentCountDebtModel = selectedItem.getValue();
        if (listStudentGroupDebt.contains(studentCountDebtModel)) {
            PopupUtil.showWarning("В таблице уже есть этот студент!");
            return;
        }
        listStudentGroupDebt.add(studentCountDebtModel);
        ListModelList lmStudent = new ListModelList<>(listStudentGroupDebt);
        lmStudent.setMultiple(true);
        lbStudentForGrouping.setModel(lmStudent);
        lbStudentForGrouping.renderItem(selectedItem);
    }

    public void initComboboxPeriodCommission() {
        List<PeriodCommissionModel> periods = commissionService.getPeriodCommission(formOfStudy);
        cmbPeriodCommission.getItems().clear();
        new Comboitem("Все").setParent(cmbPeriodCommission);
        for (PeriodCommissionModel period : periods) {
            Comboitem ci = new Comboitem(DateConverter.convertDateToString(period.getDateOfBegin()) + "-" +
                                         DateConverter.convertDateToString(period.getDateOfEnd()));
            ci.setParent(cmbPeriodCommission);
            ci.setValue(period);
            if (periods.size() == (periods.indexOf(period) + 1)) {
                cmbPeriodCommission.setSelectedItem(ci);
            }
        }
        // костыль для Иевлевой для отображения комиссий у выпускников 2020
        if (formOfStudy == 1) {
            Comboitem ciPeriodAutumn = new Comboitem("Выпуск'20 (осень 19/20)");
            ciPeriodAutumn.setValue(commissionService.getPeriodCommissionForGradute(formOfStudy, 68L));
            ciPeriodAutumn.setParent(cmbPeriodCommission);
            Comboitem ciPeriodSpring = new Comboitem("Выпуск'20 (весна 19/20)");
            ciPeriodSpring.setValue(commissionService.getPeriodCommissionForGradute(formOfStudy, 70L));
            ciPeriodSpring.setParent(cmbPeriodCommission);
        }

    }

    public void initListboxSemester() {
        //componentService.
        formOfStudy = ((FormOfStudy) cmbFormOfStudy.getSelectedItem().getValue()).getType();
        idInst = ((InstituteModel) cmbInst.getSelectedItem().getValue()).getIdInst();
        ListModelList lmSemester = new ListModelList<>(commissionService.getSemesterByInstAndFOS(idInst, formOfStudy));
        lmSemester.setMultiple(true);
        lbSemester.setModel(lmSemester);
        lbSemester.renderAll();
    }

    public void initListboxStudent() {
        if (chProlongation.isChecked() && dbProlongation.getValue() == null) {
            PopupUtil.showWarning("Выберите дату, до которого учитывать продление.");
            return;
        }
        if (cmbFormOfStudy.getSelectedIndex() == -1 || cmbInst.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите институт или форму обучения.");
            return;
        }

        initListboxStudentForGrouping();
        listIdSem = "";
        if (lbSemester.getSelectedCount() > 0) {
            for (Listitem li : lbSemester.getSelectedItems()) {
                SemesterModel sem = li.getValue();
                listIdSem += sem.getIdSem() + ",";
            }
            listIdSem = listIdSem.substring(0, listIdSem.length() - 1);
        }
        String qualification =
                (chEngineer.isChecked() ? "1," : "") + (chBachelor.isChecked() ? "2," : "") + (chMaster.isChecked() ? "3," : "");
        qualification = qualification.substring(0, qualification.length() - 1);
        listStudentDebt = commissionService.getListStudentCountDebt(qualification, cmbCourse.getSelectedIndex(), spinDebt.getValue(),
                                                                    getConditionalByString(cmbTypeDebt.getValue()),
                                                                    cmbGovernment.getSelectedIndex(),
                                                                    lbSemester.getSelectedCount() == 0 ? null : listIdSem,
                                                                    chProlongation.isChecked(), dbProlongation.getValue(), formOfStudy,
                                                                    idInst
        );
        ListModelList lmStudent = new ListModelList<>(listStudentDebt);
        lmStudent.setMultiple(true);
        lbFoundStudents.setAttribute("checkSelectAllLbStudent", true);
        lbFoundStudents.setModel(lmStudent);
        lbFoundStudents.renderAll();
    }

    public void initListboxStudentForGrouping() {
        if (listStudentGroupDebt.size() != 0) {
            listStudentGroupDebt.clear();
        }
        ListModelList lmGroup = new ListModelList<>(listStudentGroupDebt);
        lmGroup.setMultiple(true);
        lbStudentForGrouping.setAttribute("checkSelectAllLbGroup", true);
        lbStudentForGrouping.setModel(lmGroup);
        lbStudentForGrouping.renderAll();
    }

    private void addComboitem(Combobox cmb, List<SemesterModel> semesters) {
        while (cmb.getChildren().size() > 1) {
            cmb.getChildren().remove(1);
        }
        for (SemesterModel sem : semesters) {
            Comboitem ci = new Comboitem(DateConverter.convert2dateToString(sem.getDateOfBegin(), sem.getDateOfEnd()) + " " +
                                         (sem.getSeason() == 0 ? "осень" : "весна"));
            ci.setValue(sem);
            ci.setParent(cmb);
        }
    }

    private String getConditionalByString(String value) {
        if (value.equals("Больше либо равно")) {
            return ">=";
        } else if (value.equals("Меньше либо равно")) {
            return "<=";
        } else if (value.equals("Равно")) {
            return "=";
        } else if (value.equals("Не равно")) {
            return "<>";
        } else {
            return "=";
        }
    }
}
