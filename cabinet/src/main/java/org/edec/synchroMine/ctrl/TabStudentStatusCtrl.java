package org.edec.synchroMine.ctrl;

import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.StudentStatusModel;
import org.edec.synchroMine.service.StudentMineService;
import org.edec.synchroMine.service.impl.StudentMineImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import java.util.ArrayList;
import java.util.List;

public class TabStudentStatusCtrl extends SelectorComposer<Component> {

    static final String SELECTED_FORM_OF_STUDY = "selectedFormOfStudy";
    static final String SELECTED_INST = "selectedInst";
    static final String SELECTED_SEMESTER = "selectedSemester";

    @Wire
    private Listbox lbStudentsStatusMine, lbStudentsStatusESO;

    private List<StudentStatusModel> studentsEso, studentsMine;
    private List<StudentStatusModel> newStudentsMine = new ArrayList<>();
    private List<StudentStatusModel> newStudentsEso = new ArrayList<>();
    private StudentMineService studentService = new StudentMineImpl();

    private FormOfStudy selectedFormOfStudy;
    private SemesterModel selectedSemester;
    private InstituteModel selectedInst;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        selectedFormOfStudy = (FormOfStudy) Executions.getCurrent().getAttribute(SELECTED_FORM_OF_STUDY);
        selectedInst = (InstituteModel) Executions.getCurrent().getAttribute(SELECTED_INST);
        selectedSemester = (SemesterModel) Executions.getCurrent().getAttribute(SELECTED_SEMESTER);

        if (selectedFormOfStudy == null || selectedSemester == null || selectedInst == null) {
            return;
        }

        studentsEso = studentService.getStudentStatusEso(selectedInst.getIdInst(), selectedSemester.getIdSem());
        studentsMine = studentService.getStudentStatusMine(selectedInst.getIdInstMine(), selectedSemester);

        List<StudentStatusModel> studentEsoAndMine = new ArrayList<>();

        for (StudentStatusModel studentMine : studentsMine) {
            for (StudentStatusModel studentEso : studentsEso) {
                if (studentMine.getIdStudCardMine().equals(studentEso.getIdStudCardMine())
                        && (studentMine.getStatus() != studentEso.getStatus()
                        || (studentMine.getCondOfEducation() == 1 && studentEso.getIsBudget() == 0)
                        || (studentMine.getCondOfEducation() == 2 && studentEso.getIsBudget() == 0 && studentEso.getIsTrustAgreement() == 0)
                        || (studentMine.getCondOfEducation() == 3 && (studentEso.getIsBudget() == 1 || studentEso.getIsTrustAgreement() == 1)
                        || !studentMine.getGroupname().equals(studentEso.getGroupname())))
                        && (studentEso.getIsTransferStudent() == 0
                        && studentEso.getIsTransfered() == 0)) {
                    newStudentsMine.add(studentMine);
                    newStudentsEso.add(studentEso);
                }
                if (studentMine.getIdStudCardMine().equals(studentEso.getIdStudCardMine())
                        || (studentMine.getFormOfStudy() != studentEso.getFormOfStudy())) {
                    studentEsoAndMine.add(studentMine);
                }
            }
        }
        studentsMine.removeAll(studentEsoAndMine);

        fillListBoxEso(newStudentsEso);

        fillListBoxMine(newStudentsMine);
        fillListBoxMine(studentsMine);

    }

    private void fillListBoxEso(List<StudentStatusModel> studentsEso) {
        for (StudentStatusModel student : studentsEso) {
            Listitem li = new Listitem();
            li.appendChild(new Listcell(student.getFio()));
            li.appendChild(new Listcell(student.getGroupname()));
            li.appendChild(new Listcell(getStringStatus(student.getStatus())));
            li.appendChild(new Listcell(getStringConditionOfStudy(student.getCondOfEducation())));

            li.setStyle("background: #a9ffa1;");
            lbStudentsStatusESO.appendChild(li);

        }
    }

    private void fillListBoxMine(List<StudentStatusModel> studentsMine) {
        for (StudentStatusModel student : studentsMine) {

            Listitem li = new Listitem();
            li.appendChild(new Listcell(student.getFio()));
            li.appendChild(new Listcell(student.getGroupname()));
            li.appendChild(new Listcell(getStringStatus(student.getStatus())));
            li.appendChild(new Listcell(getStringConditionOfStudy(student.getCondOfEducation())));
            for (StudentStatusModel studentEso : newStudentsEso){
                if (student.getIdStudCardMine().equals(studentEso.getIdStudCardMine())){
                    li.setStyle("background: #a9ffa1;");
                    break;
                } else {
                    li.setStyle("background: #f79494;");
                }
            }
            lbStudentsStatusMine.appendChild(li);
        }
    }

    private String getStringStatus(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "Обучается";
                break;
            case -1:
                statusStr = "В академе";
                break;
            case 3:
                statusStr = "Отчислен";
                break;
            case 4:
                statusStr = "Закончил обучение";
                break;
        }

        return statusStr;
    }

    private String getStringConditionOfStudy(Integer condOfstudy){
        String condStr = "";
        switch (condOfstudy) {
            case 1:
                condStr = "Бюджетник";
                break;
            case 2:
                condStr = "Целевик";
                break;
            case 3:
                condStr = "Договорник";
                break;
        }

        return condStr;
    }
}
