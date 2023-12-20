package org.edec.commission.ctrl;

import lombok.extern.log4j.Log4j;
import org.apache.log4j.Logger;
import org.edec.commission.model.StudentCountDebtModel;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Date;

/**
 * Created by dmmax
 */
@Log4j
public class WinStudentDebtCtrl extends SelectorComposer<Component> {
    public static final String LIST_ID_SEM = "list_id_sem";
    public static final String STUDENT_DEBT_COUNT = "student_debt_countm";

    @Wire
    private Datebox dbDateBeginComm, dbDateEndComm;
    @Wire
    private Label lStudentDebt;
    @Wire
    private Listbox lbStudentDebtChoosen;
    @Wire
    private Vbox vbAdditionalDebt;

    private CommissionService commissionService = new CommissionServiceESOimpl();
    private TemplatePageCtrl template = new TemplatePageCtrl();

    private String listIdSem;
    private StudentCountDebtModel selectedStudent;
    private int formOfStudy = 1;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedStudent = (StudentCountDebtModel) Executions.getCurrent().getArg().get(STUDENT_DEBT_COUNT);
        listIdSem = (String) Executions.getCurrent().getArg().get(LIST_ID_SEM);
        if (listIdSem.equals("")) {
            listIdSem = null;
        }

        //Спешал фор СРОООЧЧНААААА :) (ф/о заочная по умолчанию)
        //TODO: передавать форму обчения
        /*if (template.get.getId_employee().longValue() == 418) {
            formOfStudy = 2;
        }*/

        lStudentDebt.setValue(selectedStudent.getFio() + " (" + selectedStudent.getGroupname() + ")");
        initListboxDebt(selectedStudent.getIdSC(), selectedStudent.getIdDG(), listIdSem);
    }

    @Listen("onClick = #btnAllDebt")
    public void searchAllDebt() {
        initListboxDebt(selectedStudent.getIdSC(), selectedStudent.getIdDG(), null);
    }

    private void initListboxDebt(Long idSC, Long idDG, String listIdSem) {
        ListModelList lmStudentDebt = new ListModelList<>(commissionService.getDividedByFocStudentsDebt(idSC, idDG, listIdSem, formOfStudy));
        lmStudentDebt.setMultiple(true);
        lbStudentDebtChoosen.setModel(lmStudentDebt);
        lbStudentDebtChoosen.renderAll();
    }

    @Listen("onClick = #btnCreateIndividualCommission")
    public void createIndividualCommission() {
        if (lbStudentDebtChoosen.getSelectedCount() == 0) {
            DialogUtil.exclamation("Выберите хотя бы одну дисциплину, для создания комиссии!", "Предупреждение");
            return;
        }
        if (dbDateBeginComm.getValue() == null || dbDateEndComm.getValue() == null) {
            PopupUtil.showWarning("Заполните даты!");
            return;
        }
        for (Listitem li : lbStudentDebtChoosen.getSelectedItems()) {
            StudentDebtModel studentDebtModel = li.getValue();
            if (studentDebtModel.isOpenComm())
                continue;
            if (commissionService.createIndividualCommission(studentDebtModel, dbDateBeginComm.getValue(), dbDateEndComm.getValue(), template.getCurrentUser().getIdHum()))
                log.info("Создана комиссия " + DateConverter.convertDateToString(new Date()) +
                         " для студента: " + studentDebtModel.getFio() +
                         ", по предмету: " + studentDebtModel.getSubjectname() + "(" + studentDebtModel.getFocStr() + ")"+
                         " сотрудником " + template.getCurrentUser().getFio());
        }
        initListboxDebt(selectedStudent.getIdSC(), selectedStudent.getIdDG(), listIdSem);
    }
}
