package org.edec.student.curriculum.ctrl;

import org.edec.main.model.UserModel;
import org.edec.student.curriculum.model.StudentsCurriculumModel;
import org.edec.student.curriculum.model.SubjectCurriculumModel;
import org.edec.student.curriculum.renderer.StudentsCurriculumRenderer;
import org.edec.student.curriculum.service.StudentsCurriculumService;
import org.edec.student.curriculum.service.impl.StudentsCurriculumServiceImpl;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.zk.CabinetSelector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Label lGroupname, lSpeciality, lYear, lCodeSpeciality;
    @Wire
    private Listbox lbCurriculum;
    @Wire
    private Combobox cmbSemster;
    @Wire
    private Textbox tbFilterSemester, tbFilterSubjectCode, tbFilterSubjectname, tbFilterFoc;
    @Wire
    private Checkbox chbClosedSubjects, chbNotLearnedSubjects;

    private UserModel currentUser = template.getCurrentUser();
    private StudentsCurriculumModel curriculumMine = new StudentsCurriculumModel();
    private List<SubjectCurriculumModel> subjetsISIT = new ArrayList<>();
    private StudentsCurriculumService service = new StudentsCurriculumServiceImpl();

    @Override
    protected void fill() {

        Long otherIdStudentcard = service.getOtherIdStudentcard(currentUser.getIdHum());
        curriculumMine = service.getCurriculum(otherIdStudentcard);
        subjetsISIT = service.getSubjectsFromISIT(otherIdStudentcard);

        curriculumMine.setSubjects(service.compareSubjectsList(curriculumMine.getSubjects(), subjetsISIT));

        if (curriculumMine.getFilename() != null) {
            lGroupname.setValue("Группа: " + curriculumMine.getGroupnmae());
            lSpeciality.setValue("Cпециальность: " + curriculumMine.getSpeciallity());
            lCodeSpeciality.setValue("Код специальности: " + curriculumMine.getCodeSpeciality());
            cmbSemster.setSelectedIndex(0);

            ListModelList<SubjectCurriculumModel> lmSubjects = new ListModelList<>(curriculumMine.getSubjects());

            lbCurriculum.setItemRenderer(new StudentsCurriculumRenderer());
            lbCurriculum.setModel(lmSubjects);
            lbCurriculum.renderAll();
        } else {
            lbCurriculum.setEmptyMessage("Учебный план не найден.");
        }
    }

    @Listen("onChange = #cmbSemster; onOK = #tbFilterSemester, #tbFilterSubjectCode, #tbFilterSubjectname, #tbFilterFoc;" +
            "onCheck = #chbClosedSubjects, #chbNotLearnedSubjects")
    public void getSubjectsBySem() {
        List<SubjectCurriculumModel> listSubjects = new ArrayList<>(curriculumMine.getSubjects());
        Integer curSem = service.getCurSem(currentUser.getFormofstudystudent(), subjetsISIT);
        if (cmbSemster.getSelectedIndex() == 1) { // просмотр текущего и предыдущих семестров
            listSubjects = listSubjects.stream().filter(subject -> subject.getSemesterNumber() >= curSem).collect(Collectors.toList());
        } else if (cmbSemster.getSelectedIndex() == 0) { // просмотр всех семестров
            listSubjects = new ArrayList<>(curriculumMine.getSubjects());
        }
//        else if (cmbSemster.getSelectedIndex() == 2){  // просмотр только следующих семестров
//            listSubjects = listSubjects.stream().filter(subject-> subject.getSemesterNumber()> curSem).collect(Collectors.toList());
//        }

        if (tbFilterSemester.getValue() != null && !tbFilterSemester.getValue().equals("")) {
            listSubjects = listSubjects.stream().filter(subject -> tbFilterSemester.getValue().contains(String.valueOf(subject.getSemesterNumber()))).collect(Collectors.toList());
        }

        if (tbFilterSubjectCode.getValue() != null && !tbFilterSubjectCode.getValue().equals("")) {
            listSubjects = listSubjects.stream().filter(subject -> subject.getCodeSubject().toLowerCase().contains(tbFilterSubjectCode.getValue().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (tbFilterSubjectname.getValue() != null && !tbFilterSubjectname.getValue().equals("")) {
            listSubjects = listSubjects.stream().filter(subject -> subject.getSubjectname().toLowerCase().contains(tbFilterSubjectname.getValue().toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (tbFilterFoc.getValue() != null && !tbFilterFoc.getValue().equals("")) {
            listSubjects = listSubjects.stream().filter(subject -> {
                for (String foc : subject.getFocList()) {
                    if (foc.toLowerCase().contains(tbFilterFoc.getValue().toLowerCase())) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }

        if (chbClosedSubjects.isChecked()) {
            listSubjects = listSubjects.stream().filter(subject -> subject.getCheckSubject() != null && subject.getCheckSubject() == true).collect(Collectors.toList());
        }
        if (chbNotLearnedSubjects.isChecked()) {
            listSubjects = listSubjects.stream().filter(subject -> subject.getLearnedSubject() == true).collect(Collectors.toList());
        }

        ListModelList<SubjectCurriculumModel> lmSubjects = new ListModelList<>(listSubjects);
        lbCurriculum.setItemRenderer(new StudentsCurriculumRenderer());
        lbCurriculum.setModel(lmSubjects);
        lbCurriculum.renderAll();
    }
}
