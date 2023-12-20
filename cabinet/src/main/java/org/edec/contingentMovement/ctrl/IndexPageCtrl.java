package org.edec.contingentMovement.ctrl;

import org.edec.contingentMovement.ctrl.individualCurriculum.IndividualCurriculumCtrl;
import org.edec.contingentMovement.ctrl.renderer.ReportMovingRenderer;
import org.edec.contingentMovement.ctrl.renderer.StudentRenderer;
import org.edec.contingentMovement.model.ReportModel;
import org.edec.contingentMovement.service.ContingentMovementService;
import org.edec.contingentMovement.service.impl.ContingentMovementImpl;
import org.edec.contingentMovement.service.impl.ReportService;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listfoot;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Include includeIndividualCurriculum;

    @Wire
    private Combobox cmbInst, cmbFos;
    @Wire
    private Hbox hbFilters;
    @Wire
    private Listbox lbSearchStudent;
    @Wire
    private Vbox vbInst, vbFos;
    @Wire
    private Textbox tbSearchStudentFio, tbSearchStudentGroup, tbSearchStudentRecordbook;
    @Wire
    private Radio rbIKIT, rbSFU;
    @Wire
    private Checkbox chDetailSemester;

    @Wire
    private Listbox lbReportMoving;

    /**
     * Вкладка Отчет
     */
    @Wire
    private Datebox dbReportTo, dbReportFrom;
    @Wire
    private Checkbox cbReportZaoch, cbReportOch;
    @Wire
    private Button btnPrintMoveReport;

    //Сервисы
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ContingentMovementService contingentMovementService = new ContingentMovementImpl();

    private Long idSem;

    private Boolean innerSearch = true;

    private ReportService lastReportService = new ReportService();
    private List<ReportModel> lastReportModels = null;

    @Override
    protected void fill() {

        lbSearchStudent.setItemRenderer(new StudentRenderer(this, currentModule, innerSearch));
        componentService.fillCmbInst(cmbInst, vbInst, currentModule.getDepartments());
        componentService.fillCmbFormOfStudy(cmbFos, vbFos, currentModule.getFormofstudy());
        if (!vbInst.isVisible() && !vbFos.isVisible()) {
            hbFilters.setVisible(false);
        }
        Events.echoEvent("onSelect", cmbInst, null);
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        dbReportFrom.setValue(c.getTime());
    }

    @Listen("onSelect = #cmbInst; onSelect = #cmbFos")
    public void selectInstAndFos() {
        if (cmbInst.getSelectedItem() == null || cmbFos.getSelectedItem() == null) {
            DialogUtil.exclamation("Пожалуйста, выберите институт и форму обучения. " +
                    "Модуль может работать некорректно с невыбранной формой обучения.");
            return;
        }
        InstituteModel inst = cmbInst.getSelectedItem().getValue();
        FormOfStudy fos = cmbFos.getSelectedItem().getValue();
        idSem = contingentMovementService.getCurrentSem(inst.getIdInst(), fos.getType());

        IndividualCurriculumCtrl.setPropertiesToInclude(includeIndividualCurriculum, inst, fos, idSem);
        includeIndividualCurriculum.invalidate();
    }

    @Listen("onOK=#tbSearchStudentFio; onOK=#tbSearchStudentRecordbook; onOK=#tbSearchStudentGroup; onCheck = #chDetailSemester")
    public void searchStudents() {
        if (rbIKIT.isSelected() || rbSFU.isSelected()) {
            innerSearch = true;
            Clients.showBusy(lbSearchStudent, "Загрузка данных");
            Events.echoEvent("onLater", lbSearchStudent, null);
        }
    }

    @Listen("onClick=#rbExternal")
    public void createNewExternalStudent() {
        innerSearch = false;
        Map<String, Object> arg = new HashMap<>();
        arg.put(WinRecoveryCtrl.SELECTED_STUDENT, null);
        arg.put(WinRecoveryCtrl.MAIN_PAGE, this);
        arg.put(WinRecoveryCtrl.ACTION_PAGE, WinRecoveryCtrl.Actions.NEW);

        ComponentHelper.createWindow("winRecovery.zul", "WinRecovery", arg).doModal();
    }

    @Listen("onLater = #lbSearchStudent")
    public void laterLbTab1Student(Event event) {
        if (rbIKIT.isSelected()) { //Пытаемся найти в ИКИТ
            innerSearch = true;

            if (chDetailSemester.isChecked()) {
                lbSearchStudent.setModel(new ListModelList<>(sortStudents(contingentMovementService.getStudentsByFilterDetail(
                        tbSearchStudentFio.getValue(),
                        tbSearchStudentRecordbook.getValue(),
                        tbSearchStudentGroup.getValue()
                ))));
            } else {
                lbSearchStudent.setModel(new ListModelList<>(sortStudents(contingentMovementService.getStudentsByFilterWithNull(
                        tbSearchStudentFio.getValue(),
                        tbSearchStudentRecordbook.getValue(),
                        tbSearchStudentGroup.getValue()
                ))));
            }
            lbSearchStudent.setItemRenderer(new StudentRenderer(this, currentModule, innerSearch));
            lbSearchStudent.renderAll();
            if (event.getData() != null) {
                int index = (int) event.getData();
                lbSearchStudent.renderItem(lbSearchStudent.getItemAtIndex(index));
                lbSearchStudent.setSelectedIndex(index);
            }
        } else if (rbSFU.isSelected()) { //Если пытаемся найти в Шахтах
            innerSearch = false;
            lbSearchStudent.setModel(new ListModelList<>(sortStudents(contingentMovementService
                    .getStudentsByFilterInDBO(tbSearchStudentFio.getValue(), tbSearchStudentRecordbook.getValue(),
                            tbSearchStudentGroup.getValue()
                    ))));
            lbSearchStudent.setItemRenderer(new StudentRenderer(this, currentModule, innerSearch));
            lbSearchStudent.renderAll();

        }
        // TODO: Отсортировать по группе
        Clients.clearBusy(lbSearchStudent);
    }

    /**
     * Сортировка списка полученных студентов (Текущая группа сверху)
     */
    private List<StudentStatusModel> sortStudents(List<StudentStatusModel> list) {
        list.sort(nullsLast(comparing(StudentStatusModel::getFamily))
                .thenComparing(
                        nullsLast(
                                comparing(StudentStatusModel::getIdSemester, nullsLast(naturalOrder()))
                                        .reversed())
                )
        );
        return list;
    }

    public Long getIdSem() {
        return idSem;
    }

    @Listen("onClick = #btnReport")
    public void generateReportMoving() {
        ReportService reportService = new ReportService();
        List<ReportModel> reportModels = null;
        String fos = "";
        if (cbReportOch.isChecked())
        {
            fos = "1";
            if (cbReportZaoch.isChecked()) {
                fos += ",2,3";
            }
        } else if (cbReportZaoch.isChecked()) {
            fos = "2,3";
        } else {
            fos = "0";
        }

        try {
            // reportModels = reportService.initReport(dateFormat.parse("2019-02-28"), dateFormat.parse("2019-04-01"));
            reportModels = reportService.initReport(fos, dbReportFrom.getValue(),dbReportTo.getValue());

            ReportMovingRenderer reportMovingRenderer = new ReportMovingRenderer(reportService);
            lbReportMoving.setItemRenderer(reportMovingRenderer);
            lbReportMoving.setModel(new ListModelList<>(reportModels));
            lbReportMoving.renderAll();
            if(lbReportMoving.getListfoot() == null) {
                lbReportMoving.appendChild(new Listfoot());
            }
            lbReportMoving.getListfoot().getChildren().clear();
            reportMovingRenderer.calcFooter(lbReportMoving.getListfoot());
            //tbReport.setValue(reportService.printReport(reportModels));

            // Заносим в БД все измерения за конкретную дату
            reportService.insertAllMeasures(reportModels, new Date());
            reportService.generateReportCSV(reportModels);

            reportModels = reportService.prepareReportModelForXlsx(reportModels);

            lastReportModels = reportModels;
            lastReportService = reportService;
            btnPrintMoveReport.setDisabled(false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Listen("onClick = #btnPrintMonthMoveReport")
    public void generateReportMovingStatic() {
        ReportService reportService = new ReportService();
        List<ReportModel> reportModels = null;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        Date toDate = c.getTime();

        c.add(Calendar.MONTH, -1);
        Date fromDate = c.getTime();

        reportModels = reportService.prepareReportModelForXlsxDates(fromDate, toDate);

        AMedia aMedia = new AMedia("Движение контингента  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                "xls", "application/xls", reportService.generateReportXlsx(reportModels)
        );
        Filedownload.save(aMedia);
    }

    @Listen("onClick = #btnPrintMoveReport")
    public void generateReportMovingDinamic() {
        AMedia aMedia = new AMedia("Движение контингента  (" + DateConverter.convertDateToString(new Date()) + ").xls",
                "xls", "application/xls", lastReportService.generateReportXlsx(lastReportModels)
        );
        Filedownload.save(aMedia);
    }
}
