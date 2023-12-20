package org.edec.newMine.students.ctrl;

import org.edec.newMine.students.model.StudentsModel;
import org.edec.newMine.students.service.StudentsService;
import org.edec.newMine.students.service.impl.StudentsServiceImpl;
import org.edec.utility.constants.StudentStatus;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Window;

import java.util.Arrays;

public class WinCreateStudentInASUctrl extends SelectorComposer<Component> {
    @Wire
    Label lFIO, lGroupname, lRecorbook,lStatus, lCondOfEdu;
    @Wire
    Button btnCreateStudent;
    @Wire
    Window winCreateStudentInASU;

    public static final String SELECTED_STUDENTS = "selected_students";
    public static final String UPDATE_STUDENTS_LISTBOX = "update_students_listbox";

    private StudentsService studentsService = new StudentsServiceImpl();

    private StudentsModel student;
    private Runnable update;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        student = (StudentsModel) Executions.getCurrent().getArg().get(SELECTED_STUDENTS);
        update = (Runnable) Executions.getCurrent().getArg().get(UPDATE_STUDENTS_LISTBOX);
        if (student != null) {
            lFIO.setValue(student.getFio());
            lGroupname.setValue(student.getGroupname());
            lRecorbook.setValue(student.getRecordbook());
            lStatus.setValue(StudentStatus.getStatusByValue(student.getStatus()).getName());
            lCondOfEdu.setValue(student.getCondOfEducationStr());
        }
    }

    @Listen("onClick = #btnCreateStudent")
    public void createStudent() {
        if (student != null) {
            studentsService.refreshStudentEsoByGroup(Arrays.asList(student), student.getGroupname());
            update.run();
            PopupUtil.showInfo("Студент успешно добавлен в АСУ ИКИТ");
        } else {
            PopupUtil.showError("Невозможно добавить студента");
        }
        winCreateStudentInASU.detach();
    }

}
