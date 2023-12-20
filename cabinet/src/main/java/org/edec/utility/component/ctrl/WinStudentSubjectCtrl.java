package org.edec.utility.component.ctrl;

import org.edec.utility.component.model.StudentModel;
import org.edec.utility.component.renderer.SemesterRenderer;
import org.edec.utility.component.service.StudentComponentService;
import org.edec.utility.component.service.impl.StudentComponentImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;


public class WinStudentSubjectCtrl extends SelectorComposer<Component> {
    public static final String STUDENT_MODEL = "student_model";

    @Wire
    private Hbox hbInfo;

    @Wire
    private Label lStudent;

    @Wire
    private Listbox lbGroupSemester, lbStudentSemester;

    private StudentComponentService studentComponentService = new StudentComponentImpl();

    private StudentModel selectedStudentModel;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedStudentModel = (StudentModel) Executions.getCurrent().getArg().get(STUDENT_MODEL);
        lStudent.setValue("Студент: " + selectedStudentModel.getFio());
        lbGroupSemester.setItemRenderer(new SemesterRenderer());
        lbStudentSemester.setItemRenderer(new SemesterRenderer());
        if (selectedStudentModel.getGroupname() == null) {
            Combobox cmbGroup = new Combobox();
            cmbGroup.setParent(hbInfo);
            cmbGroup.setAutocomplete(true);
            cmbGroup.setModel(new ListModelList<>());
        } else {
            fillSubjectListbox(selectedStudentModel.getGroupname());
        }
    }

    private void fillSubjectListbox (String groupname) {
        lbGroupSemester.setModel(new ListModelList<>(studentComponentService.getGroupSemByGroupname(groupname)));
        lbGroupSemester.renderAll();
        lbStudentSemester.setModel(
                new ListModelList<>(studentComponentService.getStudentByIdHumAndGroupname(selectedStudentModel.getIdHum(), groupname)));
        lbStudentSemester.renderAll();
    }
}
