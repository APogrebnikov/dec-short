package org.edec.synchroMine.ctrl;

import org.edec.main.model.DepartmentModel;
import org.edec.synchroMine.model.dao.SubjectGroupMineModel;
import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.model.dao.WorkloadModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSubjectService;
import org.edec.synchroMine.service.impl.GroupSubjectImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Окно отображает дисциплины из УП и нагрузки, которые не были загружены на главной странице.
 * Это случается, когда идет несовпадение дисциплин из УП и Нагрузки.
 *
 * @author Max Dimukhametov
 */
public class WinAddNotSynchSubjectCtrl extends SelectorComposer<Window> {
    //Константы
    final static String SELECTED_INST = "selectedInst";
    final static String SELECTED_GROUP = "selectedGroup";
    final static String ALL_DEPARTMENTS = "allDepartments";
    final static String SUBJECTS_DEC = "subjectsDec";
    final static String SUBJECTS_MINE = "subjectsMine";

    //Компоненты
    @Wire
    private Label lNotSynchGroupName, lNotSynchCourse, lNotSynchSemester;
    @Wire
    private Listbox lbNotSynchCurriculumSubjects, lbNotSynchWorkloadSubjects;

    //Сервисы
    private GroupSubjectService groupSubjectService = new GroupSubjectImpl();

    //Переменные
    private GroupMineModel selectedGroup;
    private InstituteModel selectedInst;
    private List<DepartmentModel> departments;
    private List<SubjectGroupModel> subjectsDec;
    private List<SubjectGroupModel> subjectsMine;

    @Override
    public void doAfterCompose(Window comp) throws Exception {
        super.doAfterCompose(comp);

        selectedInst = (InstituteModel) Executions.getCurrent().getArg().get(SELECTED_INST);
        selectedGroup = (GroupMineModel) Executions.getCurrent().getArg().get(SELECTED_GROUP);
        departments = (List<DepartmentModel>) Executions.getCurrent().getArg().get(ALL_DEPARTMENTS);
        subjectsDec = (List<SubjectGroupModel>) Executions.getCurrent().getArg().get(SUBJECTS_DEC);
        subjectsMine = (List<SubjectGroupModel>) Executions.getCurrent().getArg().get(SUBJECTS_MINE);


        fillLabels();
        fillCurriculumSubjects();
        fillWorkloadSubjects();
    }

    private void fillLabels() {
        lNotSynchGroupName.setValue(selectedGroup.getGroupname());
        lNotSynchCourse.setValue(String.valueOf(selectedGroup.getCourse()));
        lNotSynchSemester.setValue(String.valueOf(selectedGroup.getSemester()));
    }

    private void fillWorkloadSubjects() {
        lbNotSynchWorkloadSubjects.setItemRenderer((ListitemRenderer<WorkloadModel>) (listitem, o, i) -> {
            listitem.setValue(o);

            new Listcell(o.getSubjectname()).setParent(listitem);
            new Listcell(String.valueOf(o.getCourse())).setParent(listitem);
            new Listcell(String.valueOf(o.getSemester())).setParent(listitem);
            new Listcell(o.getFamily() + " " + o.getName().substring(0, 1) + ". " + o.getPatronymic().substring(0, 1) + ".")
                    .setParent(listitem);
        });
        Set<Long> existedWorkloads = new HashSet<>();
        subjectsMine.forEach(subjectGroupModel -> existedWorkloads.addAll(subjectGroupModel.getWorkLoads()));
        List<WorkloadModel> workloads = groupSubjectService.getWorkloadByGroup(
                selectedInst.getIdInstMine(),
                selectedGroup.getCourse(),
                selectedGroup.getGroupname(),
                existedWorkloads
        );
        ListModelList<WorkloadModel> lmWorkloads = new ListModelList<>(workloads);
        lmWorkloads.setMultiple(true);
        lbNotSynchWorkloadSubjects.setModel(lmWorkloads);
        lbNotSynchWorkloadSubjects.renderAll();
    }

    private void fillCurriculumSubjects() {
        lbNotSynchCurriculumSubjects.setItemRenderer((ListitemRenderer<SubjectGroupMineModel>) (listitem, o, i) -> {
            listitem.setValue(o);

            new Listcell(o.getSubjectname() + " (Семестр: " + o.getSemesternumber() + ")").setParent(listitem);
            new Listcell(o.printAllFocForSubject()).setParent(listitem);

            Listcell lcAction = new Listcell();
            lcAction.setParent(listitem);

            final Popup popup = new Popup();
            popup.setParent(lcAction);
            popup.setId("popup" + o.getIdSubjMine() + lbNotSynchCurriculumSubjects.getUuid());

            Vbox content = new Vbox();
            content.setParent(popup);
            new Label("Часы: " + o.getHoursExam() + "; " + o.getHoursIndependent() + "; " + o.getHoursLabaratory() + "; " +
                    o.getHoursLecture() + "; " + o.getHoursPractice() + ". Всего: " + o.getHoursCount()).setParent(content);


            Span spanAction = new Span();
            spanAction.setParent(lcAction);
            spanAction.setSclass("z-icon-question");
            spanAction.setStyle("font-size: 16pt; width: 20px;");
            spanAction.addEventListener(Events.ON_CLICK, event -> {

            });
            lcAction.setPopup("popup" + o.getIdSubjMine() + lbNotSynchCurriculumSubjects.getUuid());
        });
        Set<Long> subjects = new HashSet<>();
        subjectsDec.forEach(subjectGroupModel -> subjects.add(subjectGroupModel.getIdSubjMine()));
        List<SubjectGroupMineModel> subjectsFromCurriculumNotSynched = groupSubjectService.getSubjectMineModel(
                selectedInst.getIdInstMine(),
                selectedGroup.getSemester(),
                selectedGroup.getCourse(),
                selectedGroup.getGroupname(),
                subjects
        );
        lbNotSynchCurriculumSubjects.setModel(new ListModelList<>(subjectsFromCurriculumNotSynched));
        lbNotSynchCurriculumSubjects.renderAll();
    }

    @Listen("onClick = #btnSynchCurrWithWorkload")
    public void clickOnSynchSubjects() {
        if (lbNotSynchCurriculumSubjects.getSelectedCount() == 0) {
            Messagebox.show("Нужно выбрать дисциплину из УП!", "Предупреждение", Messagebox.OK, Messagebox.EXCLAMATION);
            return;
        }
        if (lbNotSynchWorkloadSubjects.getSelectedCount() == 0 && lbNotSynchWorkloadSubjects.getItemCount() != 0) {
            Messagebox.show("Вы уверены, что хотите продолжить без выбора дисциплин нагрузки?",
                    "Предупреждение",
                    Messagebox.YES | Messagebox.NO,
                    Messagebox.EXCLAMATION, event -> {
                        if (event.getName().equals(Messagebox.ON_YES)) {
                            synchSubjects();
                        }
                    });
            return;
        }
        synchSubjects();
    }

    private void synchSubjects() {
        SubjectGroupMineModel selectedCurrSubject = lbNotSynchCurriculumSubjects.getSelectedItem().getValue();

        //TODO: Может быть стоит в окно передавать семестр и использовать его тут
        SubjectGroupModel subjectGroup = groupSubjectService.getSubjectGroupByMineModel(selectedCurrSubject, new Date());

        DepartmentModel selectedDepartment = departments.stream()
                .filter(departmentModel -> departmentModel.getIdDepartmentMine() != null
                        && departmentModel.getIdDepartmentMine().equals(subjectGroup.getIdChairMine()))
                .findFirst().orElse(null);

        if (selectedDepartment == null) {
            PopupUtil.showWarning("Не существует такой кафедры!");
            return;
        }

        subjectGroup.setIdChair(selectedDepartment.getIdChair());

        for (Listitem listitem : lbNotSynchWorkloadSubjects.getSelectedItems()) {
            WorkloadModel workload = listitem.getValue();

            Long idEmp = groupSubjectService.searchIdEmpByFio(workload.getFamily(), workload.getName(), workload.getPatronymic());

            if (idEmp != null) {
                subjectGroup.getEmployees().add(idEmp);
            }
        }

        if (groupSubjectService.createSubjectSRforLGS(selectedGroup.getIdLGS(), subjectGroup)) {
            Messagebox.show("Предмет успешно создан", "Уведомление", Messagebox.OK, Messagebox.INFORMATION);
            lbNotSynchCurriculumSubjects.getChildren().remove(lbNotSynchCurriculumSubjects.getSelectedItem());
            lbNotSynchWorkloadSubjects.getChildren().removeAll(lbNotSynchWorkloadSubjects.getSelectedItems());
            Events.echoEvent("onLater", getSelf(), null);
        }
    }
}