package org.edec.studentPassport.ctrl;

import org.edec.scholarship.service.ScholarshipService;
import org.edec.scholarship.service.impl.ScholarshipServiceImpl;
import org.edec.studentPassport.ctrl.renderer.RatingRenderer;
import org.edec.studentPassport.ctrl.renderer.ScholarshipRenderer;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.studentPassport.service.StudentPassportService;
import org.edec.studentPassport.service.impl.StudentPassportServiceESOimpl;
import org.edec.synchroMine.service.StudentMineService;
import org.edec.synchroMine.service.impl.StudentMineImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;


public class WinPersonalStudentCtrl extends SelectorComposer<Component> {
    public static final String INDEX_ELEMENT = "index_element";
    public static final String INDEX_PAGE = "index_page";
    public static final String STUDENT_MODEL = "student_model";

    @Wire
    private Checkbox chDebt, chForeigner, chDebtor;

    @Wire
    private Datebox dbEditDateOfBirth;

    @Wire
    private Listbox lbEduPerformance, lbEduScholarship, lbEduScholarshipHistory;

    @Wire
    private Tabbox tabEduPerformance, tabEduScholarship, tabEduScholarshipHistory;

    @Wire
    private Textbox tbEditFamily, tbEditName, tbEditPatronymic, tbEditRecordbook, tbEditEmail;

    private StudentPassportService stPassportService = new StudentPassportServiceESOimpl();
    private ScholarshipService scholarshipService = new ScholarshipServiceImpl();
    private StudentMineService studentMineService;

    private int selectedIndex;
    private IndexPageCtrl indexPageCtrl;
    private StudentStatusModel currentStudent;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        currentStudent = (StudentStatusModel) Executions.getCurrent().getArg().get(STUDENT_MODEL);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE);
        selectedIndex = (Integer) Executions.getCurrent().getArg().get(INDEX_ELEMENT);
        fillInputs();
    }

    private void fillInputs () {
        tbEditFamily.setValue(currentStudent.getFamily());
        tbEditName.setValue(currentStudent.getName());
        tbEditPatronymic.setValue(currentStudent.getPatronymic());
        tbEditRecordbook.setValue(currentStudent.getRecordBook());
        tbEditEmail.setValue(currentStudent.getEmail());
        dbEditDateOfBirth.setValue(currentStudent.getBirthday());
        chForeigner.setChecked(currentStudent.getForeigner());
        chDebtor.setChecked(currentStudent.getIsDebtor());
    }

    @Listen("onClick = #btnSaveEditStudent")
    public void saveStudent () {
        currentStudent.setFamily(tbEditFamily.getValue());
        currentStudent.setName(tbEditName.getValue());
        currentStudent.setPatronymic(tbEditPatronymic.getValue());
        currentStudent.setRecordBook(tbEditRecordbook.getValue());
        currentStudent.setEmail(tbEditEmail.getValue());
        currentStudent.setBirthday(dbEditDateOfBirth.getValue());
        currentStudent.setForeigner(chForeigner.isChecked());
        currentStudent.setIsDebtor(chDebtor.isChecked());
        if (stPassportService.saveStudentInfo(currentStudent)) {
            PopupUtil.showInfo("Сохранение прошло успешно!");
            indexPageCtrl.updateLb(selectedIndex);
        } else {
            PopupUtil.showError("Не удалось сохранить! Обратитесь к администратору!");
        }
    }

    @Listen("onClick = #tabEduPerformance")
    public void selectedTabEduPerformance () {
        if (lbEduPerformance.getItemCount() != 0) {
            return;
        }
        chDebt.setChecked(false);
        lbEduPerformance.setItemRenderer(new RatingRenderer());
        Clients.showBusy(lbEduPerformance, "Загрузка данных");
        Events.echoEvent("onLater", lbEduPerformance, null);
    }

    @Listen("onClick = #tabEduScholarship")
    public void selectedTabEduScholarship(){
        if(lbEduScholarship.getItemCount() != 0){
            return;
        }
        lbEduScholarship.setItemRenderer(new ScholarshipRenderer(scholarshipService));
        Clients.showBusy(lbEduScholarship, "Загрузка данных");
        Events.echoEvent("onLater", lbEduScholarship, null);
    }

    @Listen("onCheck = #chDebt")
    public void checkedDebt () {
        Clients.showBusy(lbEduPerformance, "Загрузка данных");
        Events.echoEvent("onLater", lbEduPerformance, null);
    }

    @Listen("onLater = #lbEduPerformance")
    public void laterOnLbEduPerformance () {
        lbEduPerformance.setModel(new ListModelList<>(
                stPassportService.getRatingByHumAndDG(currentStudent.getIdHum(), currentStudent.getIdDG(), chDebt.isChecked())));
        lbEduPerformance.renderAll();
        Clients.clearBusy(lbEduPerformance);
    }

    @Listen("onLater = #lbEduScholarship")
    public void laterOnLbEduScholarship() {
        lbEduScholarship.setModel(new ListModelList<>(scholarshipService.getScholarshipsByStudent(currentStudent.getIdStudentCard())));
        lbEduScholarship.renderAll();
        Clients.clearBusy(lbEduScholarship);
    }

    @Listen("onClick = #lbEduScholarshipHistory")
    public void laterOnLbEduScholarshipHistory(){
        lbEduScholarshipHistory.setModel(new ListModelList<>(scholarshipService.getScholarshipHistory(currentStudent.getIdStudentCard())));
        lbEduScholarshipHistory.renderAll();
        Clients.clearBusy(lbEduScholarshipHistory);
    }

    @Listen("onClick = #btnAddHiddenSem")
    public void addHiddenSem () {
        studentMineService = new StudentMineImpl();

        try {
            studentMineService.createSSSforStudent(currentStudent.getIdStudentCard(), currentStudent.getTrustAgreement() ? 1 : 0,
                                                   currentStudent.getGovernmentFinanced() ? 1 : 0,
                                                   currentStudent.getAcademicLeave() ? 1 : 0, null, currentStudent.getGroupname()
            );
            studentMineService.createSRforStudent(currentStudent.getIdStudentCard(), null, currentStudent.getGroupname());
            PopupUtil.showInfo("Семестры успешно добавились");
        } catch (Exception e) {
            e.printStackTrace();
            PopupUtil.showError("Добавить семестры не удалось.");
        }
    }
}
