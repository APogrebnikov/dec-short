package org.edec.contingentMovement.ctrl;

import org.apache.log4j.Logger;
import org.edec.studentPassport.model.StudentStatusModel;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер страницы "Дополнительные опции" элемента списка студентов в движении контингента
 */
public class StudentItemOptionsCtrl extends SelectorComposer<Component> {
    private static final Logger log = Logger.getLogger(StudentItemOptionsCtrl.class.getName());

    public static final String STUDENT_MODEL = "student_model";
    public static final String MAIN_PAGE = "main_page";

    @Wire
    private Window winStudentItemOptions;
    @Wire
    private Label lbRegisterNumber;
    @Wire
    private Label lbSemester;
    @Wire
    private Label lbStudentFIO;
    @Wire
    private Label lbCurrentGroup;
    @Wire
    private Button btnResurrection, btnTransfer, btnStatus;

    private IndexPageCtrl indexPageCtrl;
    private StudentStatusModel currentStudent;

    /**
     * Инициализация.
     */
    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        //Заполнение окна
        currentStudent = (StudentStatusModel) Executions.getCurrent().getArg().get(STUDENT_MODEL);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
        fillFields();
    }

    private void fillFields () {
        lbStudentFIO.setValue(currentStudent.getFio());
        lbCurrentGroup.setValue(currentStudent.getGroupname());
        if ((currentStudent.getAcademicLeave() != null && currentStudent.getAcademicLeave()) || (currentStudent.getDeducted() != null && currentStudent.getDeducted())) {
            btnTransfer.setDisabled(true);
        } else {
            btnResurrection.setDisabled(true);
        }
    }

    /**
     * Обработчик события ON_CLICK кнопки "Востановление".
     */
    @Listen("onClick = #btnResurrection")
    public void btnResurrectionOnClick () {
        Map arg = new HashMap();
        arg.put(WinRecoveryCtrl.SELECTED_STUDENT, currentStudent);
        arg.put(WinRecoveryCtrl.MAIN_PAGE, indexPageCtrl);
        arg.put(WinRecoveryCtrl.ACTION_PAGE, WinRecoveryCtrl.Actions.RECOVERY);

        ComponentHelper.createWindow("winRecovery.zul", "WinRecovery", arg).doModal();
        winStudentItemOptions.detach();
    }

    /**
     * Обработчик события ON_CLICK кнопки "Перевод".
     */
    @Listen("onClick = #btnTransfer")
    public void btnTransferOnClick () {
        Map arg = new HashMap();
        arg.put(WinRecoveryCtrl.SELECTED_STUDENT, currentStudent);
        arg.put(WinRecoveryCtrl.MAIN_PAGE, indexPageCtrl);
        arg.put(WinRecoveryCtrl.ACTION_PAGE, WinRecoveryCtrl.Actions.TRANSFER);

        ComponentHelper.createWindow("winRecovery.zul", "WinRecovery", arg).doModal();
        winStudentItemOptions.detach();
    }

    /**
     * Обработчик события ON_CLICK кнопки "Редактор Статусов"
     */
    @Listen("onClick = #btnStatus")
    public void btnStatusOnClick () {
        if (currentStudent.getIdSSS() == null) {
            DialogUtil.exclamation("Данный студент не числится ни в одной группе");
        }else{
            Map arg = new HashMap();
            arg.put(WinEditStatusCtrl.SELECTED_STUDENT, currentStudent);
            arg.put(WinEditStatusCtrl.MAIN_PAGE, indexPageCtrl);

            ComponentHelper.createWindow("winEditStatus.zul", "winEditStatus", arg).doModal();
        }
    }

    /**
     * Обработчик события ON_CLICK кнопки "Перезачет оценок"
     */
    @Listen("onClick = #btnOpenWinResit")
    public void openWinResit() {
        if (currentStudent.getIdSSS() == null) {
            DialogUtil.exclamation("Данный студент не числится ни в одной группе");
        } else {
            Map<String, Object> arg = new HashMap<>();
            arg.put(WinResitCtrl.SELECTED_STUDENT, currentStudent);

            ComponentHelper.createWindow("winResit.zul", "winResit", arg).doModal();
        }
    }
}
