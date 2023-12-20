package org.edec.passportGroup.ctrl;

import org.edec.passportGroup.ctrl.renderer.TeachersListRenderer;
import org.edec.passportGroup.model.SubjectReportModel;
import org.edec.passportGroup.model.TeacherModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;


public class WinTeachersCtrl extends SelectorComposer<Component> {
    PassportGroupService service = new PassportGroupServiceESO();
    @Wire
    private Window winTeachers;
    @Wire
    private Listbox listTeachers;
    @Wire
    private Textbox tbSearchEmployee, tbSearchInst, tbSearchDep;

    private SubjectReportModel subject;
    private List<TeacherModel> teachers;
    private Runnable updateSubjects;
    private Runnable updateTeachers;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);

        subject = (SubjectReportModel) Executions.getCurrent().getArg().get("subject");
        updateSubjects = ((Runnable) Executions.getCurrent().getArg().get("updateSubjects"));
        updateTeachers = ((Runnable) Executions.getCurrent().getArg().get("updateTeachers"));

        fillTeachers();
    }

    private void fillTeachers() {
        teachers = service.getTeachers(tbSearchEmployee.getValue(), tbSearchInst.getValue(), tbSearchDep.getValue());

        listTeachers.setItemRenderer(new TeachersListRenderer(teachers));
        listTeachers.setModel(new ListModelList<>(teachers));
        listTeachers.renderAll();
    }

    @Listen("onClick = #attach_btn")
    public void attach() {
        if (listTeachers.getSelectedItem() != null) {
            if (service.addTeacherToSubject(teachers.get(listTeachers.getSelectedIndex()).getIdTeacher(), subject.getIdLgss())) {
                PopupUtil.showInfo("Преподаватель был успешно прикреплен!");

                updateSubjects.run();
                updateTeachers.run();
            } else {
                PopupUtil.showWarning("Преподаватель уже был прикриплен!");
            }
        } else {
            Messagebox.show("Выберите преподавателя", "Ошибка", Messagebox.OK, Messagebox.EXCLAMATION);
        }
    }

    /*
    @Listen("onOK = #tbSearchEmployee, #tbSearchInst, #tbSearchDep")
    public void searchTeacher() {
        fillTeachers();
        listTeachers.clearSelection();
    }
     */


    @Listen("onChanging = #tbSearchEmployee")
    public void searchTeacher(InputEvent event) {
        tbSearchEmployee.setValue(event.getValue());
        fillTeachers();
        listTeachers.clearSelection();
    }

    @Listen("onChanging = #tbSearchInst")
    public void searchInst(InputEvent event) {
        tbSearchInst.setValue(event.getValue());
        fillTeachers();
        listTeachers.clearSelection();
    }

    @Listen("onChanging = #tbSearchDep")
    public void searchDep(InputEvent event) {
        tbSearchDep.setValue(event.getValue());
        fillTeachers();
        listTeachers.clearSelection();
    }

    @Listen("onClick = #report_close_btn")
    public void close() {
        winTeachers.detach();
    }
}
