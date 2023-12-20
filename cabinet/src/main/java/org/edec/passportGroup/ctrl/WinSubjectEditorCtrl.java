package org.edec.passportGroup.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.passportGroup.model.*;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

@Log4j
public class WinSubjectEditorCtrl extends SelectorComposer<Component> {

    public static final String SUBJECT = "subject";
    public static final String GROUP = "groups";
    public static final String UPDATE_SUBJECTS = "update_subjects";

    private PassportGroupService service = new PassportGroupServiceESO();
    private SubjectReportModel subject;

    @Wire
    private Window winSubjectEditor;

    @Wire
    private Doublebox inputHoursСount, inputHoursAudCount, inputHoursLection, inputHoursLabor, inputHoursPractic;

    @Wire
    private Combobox cmbNameSubject, cmbNameChair, cmbNameGroup;

    @Wire
    private Textbox tbCodeSubject;

    @Wire
    private Checkbox chPass, chExam, chCourseProject, chDifPass, chCourseWork, chbSynchMine, chbPractice;

    @Wire
    private Label windowName;

    protected TemplatePageCtrl template = new TemplatePageCtrl();

    private List<DepartmentModel> departmentList;
    private List<SubjectModel> subjectList;
    private List<GroupModel> groupList;

    Runnable updateSubjects;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        subject = (SubjectReportModel) Executions.getCurrent().getArg().get(SUBJECT);
        groupList = (List<GroupModel>) Executions.getCurrent().getArg().get(GROUP);
        updateSubjects = (Runnable) Executions.getCurrent().getArg().get(UPDATE_SUBJECTS);

        departmentList = service.getDepartmentList();
        subjectList = service.getSubjectList();

        departmentList.sort((o1, o2) -> {
            Long firstCode = o1.getOtherDbID();
            Long secondCode = o2.getOtherDbID();

            if (firstCode == null && secondCode == null) {
                return 0;
            }
            if (firstCode == null) {
                return 1;
            }
            if (secondCode == null) {
                return -1;
            }

            return firstCode.compareTo(secondCode);
        });

        for (SubjectModel subj : subjectList) {
            cmbNameSubject.appendChild(new Comboitem(subj.getSubjectName()));
        }

        for (DepartmentModel department : departmentList) {
            cmbNameChair.appendChild(new Comboitem(Long.toString(department.getOtherDbID()) + " " + department.getFullTitle()));
        }

        for (GroupModel group : groupList) {
            cmbNameGroup.appendChild(new Comboitem(group.getGroupName()));
        }

        if (subject != null) {
            init();
        }
    }

    private void init () {
        windowName.setValue("Редактирование предмета");

        cmbNameSubject.setText(subject.getSubjectName());
        cmbNameGroup.setText(subject.getGroupName());

        tbCodeSubject.setValue(subject.getSubjectCode());

        for (int i = 0; i < departmentList.size(); ++i) {
            if (subject.getIdChair() != null) {
                if (departmentList.get(i).getIdChair() == subject.getIdChair()) {
                    cmbNameChair.setSelectedIndex(i);
                }
            }
        }

        if (subject.getCp()) {
            chCourseProject.setChecked(true);
        }
        if (subject.getCw()) {
            chCourseWork.setChecked(true);
        }

        if (subject.getExam()) {
            chExam.setChecked(true);
        }
        if (subject.getPass()) {
            if (subject.getType() == 1) {
                chDifPass.setChecked(true);
            } else {
                chPass.setChecked(true);
            }
        }

        inputHoursСount.setValue(subject.getHoursCount());
        inputHoursAudCount.setValue(subject.getHoursAudCount());
        inputHoursLection.setValue(subject.getHoursLection());
        inputHoursLabor.setValue(subject.getHoursLabor());
        inputHoursPractic.setValue(subject.getHoursPractic());

        chbSynchMine.setChecked(subject.getSynchMine());
        chbPractice.setChecked(subject.getPractice());
    }

    @Listen("onClick=#btnSave")
    public void onClick () {
        if (cmbNameGroup.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите группу");
            return;
        }

        if (cmbNameChair.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите кафедру");
            return;
        }

        if (!chPass.isChecked() && !chExam.isChecked() && !chCourseProject.isChecked() &&
            !chDifPass.isChecked() && !chCourseWork.isChecked()) {
            PopupUtil.showWarning("Выберите как минимум одну форму контроля");
            return;
        }

        if (inputHoursСount.getValue() == null || inputHoursAudCount.getValue() == null || inputHoursLabor.getValue() == null ||
            inputHoursLection.getValue() == null || inputHoursPractic.getValue() == null) {
            PopupUtil.showWarning("Как минимум одно из полей часов заполненно некорректно");
            return;
        }

        if (cmbNameSubject.getValue().equals("")) {
            PopupUtil.showWarning("Введите название предмета");
            return;
        }

        if (subject == null) {
            try {
                save();
                PopupUtil.showInfo("Предмет добавлен");
                updateSubjects.run();
                winSubjectEditor.detach();
            } catch (Exception e) {
                e.printStackTrace();
                PopupUtil.showError("Не удалось добавить предмет");
            }
        } else {
            try {
                update();
                PopupUtil.showInfo("Предмет обновлен");
                updateSubjects.run();
                winSubjectEditor.detach();
            } catch (Exception e) {
                e.printStackTrace();
                PopupUtil.showError("Не удалось обновить предмет");
            }
        }
    }

    // TODO dezmontx методы save и update имеют одинаковый код по заполнению subject model, возможно имеет смысл вынести в отдельный метод?
    public void save () {
        long idDicSubject;

        if (cmbNameSubject.getSelectedIndex() == -1) {
            idDicSubject = service.createDicSubject(cmbNameSubject.getValue());
        } else {
            idDicSubject = subjectList.get(cmbNameSubject.getSelectedIndex()).getIdDicSubject();
        }

        GroupModel selectedGroup = groupList.get(cmbNameGroup.getSelectedIndex());

        SubjectModel subjectModel = new SubjectModel();

        subjectModel.setType(chDifPass.isChecked() ? 1 : 0);
        subjectModel.setPass(chPass.isChecked() || chDifPass.isChecked());
        subjectModel.setExam(chExam.isChecked());
        subjectModel.setCp(chCourseProject.isChecked());
        subjectModel.setCw(chCourseWork.isChecked());

        if (cmbNameChair.getSelectedIndex() != -1) {
            subjectModel.setIdChair(departmentList.get(cmbNameChair.getSelectedIndex()).getIdChair());
        }
        subjectModel.setIdCurriculum(selectedGroup.getIdCurriculum());

        subjectModel.setIdDicSubject(idDicSubject);

        subjectModel.setSemesterNumber(selectedGroup.getSemesterNumber());

        subjectModel.setSubjectCode(tbCodeSubject.getValue());

        subjectModel.setCountHours(inputHoursСount.getValue() == null ? 0 : inputHoursСount.getValue());
        subjectModel.setHoursAudCount(inputHoursAudCount.getValue() == null ? 0 : inputHoursAudCount.getValue());
        subjectModel.setHoursLabor(inputHoursLabor.getValue() == null ? 0 : inputHoursLabor.getValue());
        subjectModel.setHoursLection(inputHoursLection.getValue() == null ? 0 : inputHoursLection.getValue());
        subjectModel.setHoursPractic(inputHoursPractic.getValue() == null ? 0 : inputHoursPractic.getValue());
        subjectModel.setSynchMine(chbSynchMine.isChecked());
        subjectModel.setPractice(chbPractice.isChecked());
        subjectModel.setIsActive(true);

        long idSubject = service.createSubject(subjectModel);

        LinkGroupSemesterSubjectModel lgss = new LinkGroupSemesterSubjectModel();

        lgss.setIdLGS(selectedGroup.getIdLgs());
        lgss.setIdSubject(idSubject);
        lgss.setPrintStatus(1);

        lgss.setHoursCP(0d);
        lgss.setHoursCW(0d);
        lgss.setHoursConrolDistance(0d);
        lgss.setHoursConsult(0d);
        lgss.setHoursControlSelfStudy(0d);
        lgss.setFormExam(false);
        lgss.setLessonCount(0);

        service.createLinkGroupSemesterSubject(lgss);

        List<Long> listSSS = service.getStudentSemesterStatusList(selectedGroup.getIdLgs());

        for (Long sss : listSSS) {
            SessionRatingModel sr = new SessionRatingModel();

            sr.setIdSubject(idSubject);
            sr.setType(chDifPass.isChecked() ? 1 : 0);
            sr.setPass(chPass.isChecked() || chDifPass.isChecked());
            sr.setExam(chExam.isChecked());
            sr.setCp(chCourseProject.isChecked());
            sr.setCw(chCourseWork.isChecked());
            sr.setIdSSS(sss);
            sr.setEsoGradeCurrent(0d);
            sr.setExamRating(0);
            sr.setPassRating(0);
            sr.setCpRating(0);
            sr.setCwRating(0);
            sr.setPracticRating(0);
            sr.setEsoGradeMax(0d);
            sr.setSkipCount(0);
            sr.setVisitCount(0);
            sr.setEsoExamRating(0);
            sr.setEsoPassRating(0);
            sr.setEsoCPRating(0);
            sr.setEsoCWRating(0);
            sr.setRetakeCount(0);
            sr.setIdDopEso2(0);
            sr.setAttendenceCount(0);
            sr.setEsoStudy(true);
            sr.setPractic(chbPractice.isChecked());
            sr.setStatus("0.0.0");

            service.createSessionRating(sr);
        }

        log.info("Пользователь " + template.getCurrentUser().getFio() + " создал предмет " + cmbNameSubject.getValue() + " у группы " +
                 selectedGroup.getGroupName());
    }

    public void update () {
        SubjectModel subjectModel = new SubjectModel();

        GroupModel selectedGroup = groupList.get(cmbNameGroup.getSelectedIndex());

        long idDicSubject;

        if (cmbNameSubject.getSelectedIndex() == -1) {
            idDicSubject = service.createDicSubject(cmbNameSubject.getValue());
        } else {
            idDicSubject = subjectList.get(cmbNameSubject.getSelectedIndex()).getIdDicSubject();
        }

        subjectModel.setIdSubject(subject.getIdSubject());
        subjectModel.setIdDicSubject(idDicSubject);
        subjectModel.setIdLgss(subject.getIdLgss());

        subjectModel.setType(chDifPass.isChecked() ? 1 : 0);
        subjectModel.setPass(chPass.isChecked() || chDifPass.isChecked());
        subjectModel.setExam(chExam.isChecked());
        subjectModel.setCp(chCourseProject.isChecked());
        subjectModel.setCw(chCourseWork.isChecked());

        if (cmbNameChair.getSelectedIndex() != -1) {
            subjectModel.setIdChair(departmentList.get(cmbNameChair.getSelectedIndex()).getIdChair());
        }
        subjectModel.setIdCurriculum(selectedGroup.getIdCurriculum());
        subjectModel.setSemesterNumber(selectedGroup.getSemesterNumber());

        subjectModel.setSubjectCode(tbCodeSubject.getValue());

        subjectModel.setCountHours(inputHoursСount.getValue() == null ? 0 : inputHoursСount.getValue());
        subjectModel.setHoursAudCount(inputHoursAudCount.getValue() == null ? 0 : inputHoursAudCount.getValue());
        subjectModel.setHoursLabor(inputHoursLabor.getValue() == null ? 0 : inputHoursLabor.getValue());
        subjectModel.setHoursLection(inputHoursLection.getValue() == null ? 0 : inputHoursLection.getValue());
        subjectModel.setHoursPractic(inputHoursPractic.getValue() == null ? 0 : inputHoursPractic.getValue());

        subjectModel.setSynchMine(chbSynchMine.isChecked());
        subjectModel.setPractice(chbPractice.isChecked());

        service.updateSubject(subjectModel);

        service.updateGroup(subjectModel.getIdLgss(), selectedGroup.getIdLgs());

        //Получаем всех студентов у которых есть данный предмет
        List<Long> listSSS = service.getStudentSemesterStatusList(selectedGroup.getIdLgs());

        for (Long sss : listSSS) {

            //Получаем все оценки у каждого студента
            List<SessionRatingModel> listSR = service.getSRbySSS(sss);

            for (SessionRatingModel sr : listSR) {
                if (sr.getIdSubject().equals(subject.getIdSubject())) {
                    sr.setType(chDifPass.isChecked() ? 1 : 0);
                    sr.setPass(chPass.isChecked() || chDifPass.isChecked());
                    sr.setExam(chExam.isChecked());
                    sr.setCp(chCourseProject.isChecked());
                    sr.setCw(chCourseWork.isChecked());

                    service.updateSessionRating(sr);
                }
            }
        }

        log.info("Пользователь " + template.getCurrentUser().getFio() + " обновил предмет " + cmbNameSubject.getValue() + " у группы " +
                 selectedGroup.getGroupName());
    }
}
