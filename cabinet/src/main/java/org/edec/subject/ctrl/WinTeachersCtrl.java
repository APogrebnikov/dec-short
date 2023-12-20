package org.edec.subject.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.subject.ctrl.renderer.TeachersListRenderer;
import org.edec.subject.model.SubjectModel;
import org.edec.subject.model.TeacherModel;
import org.edec.subject.service.SubjectService;
import org.edec.subject.service.impl.SubjectServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

@Log4j
public class WinTeachersCtrl extends SelectorComposer<Component> {

    SubjectService service = new SubjectServiceImpl();
    @Wire
    private Window winTeachers;
    @Wire
    private Listbox listTeachers;
    @Wire
    private Textbox tbSearchEmployee;
    @Wire
    private Checkbox chbHiddenFilter;

    private TemplatePageCtrl template = new TemplatePageCtrl();

    private SubjectModel subject;
    private long idDep;
    private List<TeacherModel> teachers, filteredTeachers;
    private Runnable updateSubjects;
    private Runnable updateTeachers;
    private Runnable filterTeacherList = this::fillTeachers;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        subject = (SubjectModel) Executions.getCurrent().getArg().get("subject");
        updateSubjects = ((Runnable) Executions.getCurrent().getArg().get("updateSubjects"));
        updateTeachers = ((Runnable) Executions.getCurrent().getArg().get("updateTeachers"));
        idDep = (Long) (Executions.getCurrent().getArg().get("idDep"));

        fillTeachers();
    }

    private void fillTeachers () {
        teachers = service.getTeachers(tbSearchEmployee.getValue(), idDep);

        fillTeacherList();
    }

    private void fillTeacherList () {
        filteredTeachers = service.filterTeachers(teachers, tbSearchEmployee.getValue(), chbHiddenFilter.isChecked());

        listTeachers.setItemRenderer(new TeachersListRenderer(filteredTeachers, filterTeacherList));
        listTeachers.setModel(new ListModelList<>(filteredTeachers));
        listTeachers.renderAll();
    }

    @Listen("onClick = #attach_btn")
    public void attach () {
        if (listTeachers.getSelectedItem() != null) {
            if (service.addTeacherToSubject(filteredTeachers.get(listTeachers.getSelectedIndex()).getIdTeacher(), subject.getIdLgss())) {
                PopupUtil.showInfo("Преподаватель успешно прикреплен!");

                log.info("Пользователь " + template.getCurrentUser().getFio() + " успешно прикрепил преподавателя " +
                         filteredTeachers.get(listTeachers.getSelectedIndex()).getFullName() + " к предмету \'" + subject.getSubjectName() +
                         "\'" + " у группы " + subject.getGroupName());

                updateSubjects.run();
                updateTeachers.run();
            } else {
                log.info("Пользователь " + template.getCurrentUser().getFio() + " не удалось прикрепить преподавателя" +
                         filteredTeachers.get(listTeachers.getSelectedIndex()).getFullName() + " к предмету \'" + subject.getSubjectName() +
                         "\'" + " у группы " + subject.getGroupName());

                PopupUtil.showError("Не удалось прикрепить преподавателя!");
            }
        } else {
            PopupUtil.showWarning("Выберите преподавателя");
        }
    }

    @Listen("onOK = #tbSearchEmployee, #tbSearchInst, #tbSearchDep;onClick = #chbHiddenFilter")
    public void searchTeacher () {
        fillTeacherList();
        listTeachers.clearSelection();
    }

    @Listen("onClick = #report_close_btn")
    public void close () {
        winTeachers.detach();
    }
}
