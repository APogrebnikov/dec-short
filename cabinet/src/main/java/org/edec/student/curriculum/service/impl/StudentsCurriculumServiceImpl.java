package org.edec.student.curriculum.service.impl;

import org.edec.student.curriculum.manager.StudentsCurriculumManager;
import org.edec.student.curriculum.model.StudentsCurriculumModel;
import org.edec.student.curriculum.model.SubjectCurriculumModel;
import org.edec.student.curriculum.service.StudentsCurriculumService;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentsCurriculumServiceImpl implements StudentsCurriculumService {
    private StudentsCurriculumManager manager = new StudentsCurriculumManager();

    @Override
    public StudentsCurriculumModel getCurriculum(Long otherIdStudentcard) {
        StudentsCurriculumModel curriculumModel = new StudentsCurriculumModel();
        try {
            JSONObject jsonOtherIdStudentcard = new JSONObject();
            jsonOtherIdStudentcard.put("otherIdStudentcard", otherIdStudentcard);
            JSONObject jsonCurriculum = new JSONObject(HttpClient.makeHttpRequest("http://localhost:8081/sync/curriculum", HttpClient.POST, new ArrayList<>(), jsonOtherIdStudentcard.toString()));

            curriculumModel.setFilename(jsonCurriculum.has("filename") ? jsonCurriculum.getString("filename") : "");
            curriculumModel.setGroupnmae(jsonCurriculum.has("groupname") ? jsonCurriculum.getString("groupname") : "");
            curriculumModel.setSpeciallity(jsonCurriculum.has("speciality") ? jsonCurriculum.getJSONObject("speciality").getString("specialityName") : "");
            curriculumModel.setCodeSpeciality(jsonCurriculum.has("speciality") ? jsonCurriculum.getJSONObject("speciality").getString("codeSpeciality") : "");
            curriculumModel.setYear(jsonCurriculum.has("year") ? jsonCurriculum.getString("year") : "");
            JSONArray jArraySubjects = jsonCurriculum.getJSONArray("subjects");

            for (int i = 0; i < jArraySubjects.length(); i++) {
                SubjectCurriculumModel subjectModel = new SubjectCurriculumModel();
                JSONObject json = jArraySubjects.getJSONObject(i);

                subjectModel.setSubjectname(json.has("subjectName") ? json.getString("subjectName") : "");
                subjectModel.setCodeSubject(json.has("codeSubject") ? json.getString("codeSubject") : "");
                subjectModel.setSemesterNumber(json.getInt("semesterNumber"));
                subjectModel.setZE(json.getInt("ZE"));
                subjectModel.setHoursAll(json.getInt("hoursAll"));
                subjectModel.setHoursAud(json.getInt("hoursAud"));
                subjectModel.setOtherIdSubject(json.getLong("otherIdSubject"));
                subjectModel.setIsExam(json.getInt("isExam"));
                subjectModel.setIsPass(json.getInt("isPass"));
                subjectModel.setIsCw(json.getInt("isCw"));
                subjectModel.setIsCp(json.getInt("isCp"));
                subjectModel.setIsPractic(json.getInt("isPractic"));

                JSONArray jsonArrayFoc = jArraySubjects.getJSONObject(i).getJSONArray("foc");
                if (jsonArrayFoc.length() != 0) {
                    for (int j = 0; j < jsonArrayFoc.length(); j++) {
                        subjectModel.getFocList().add(jsonArrayFoc.get(j).toString());
                    }
                }
                curriculumModel.getSubjects().add(subjectModel);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return curriculumModel;
    }

    @Override
    public List<SubjectCurriculumModel> compareSubjectsList(List<SubjectCurriculumModel> subjectsFromMine, List<SubjectCurriculumModel> subjectsFromISIT) {
        for (SubjectCurriculumModel subjectISIT : subjectsFromISIT) {
            for (SubjectCurriculumModel subjectMine : subjectsFromMine) {
                if (subjectMine.getOtherIdSubject().equals(subjectISIT.getOtherIdSubject())
                        && subjectMine.getIsExam() == subjectISIT.getIsExam() && subjectMine.getIsPass() == subjectISIT.getIsPass()
                        && subjectMine.getIsCp() == subjectISIT.getIsCp() && subjectMine.getIsCw() == subjectISIT.getIsCw()
                        && subjectMine.getIsPractic() == subjectISIT.getIsPractic()
                        && subjectMine.getSemesterNumber() == subjectISIT.getSemesterNumber()
                        || (subjectMine.getSemesterNumber() == subjectISIT.getSemesterNumber()
                        && (subjectISIT.getSubjectname().equals(subjectMine.getSubjectname())
                        || ((subjectMine.getSubjectname().contains("Физическая культура")) && (subjectISIT.getSubjectname().contains("Физическая культура")))
                        || ((subjectMine.getSubjectname().contains("Прикладная физическая культура")) && (subjectISIT.getSubjectname().contains("Прикладная физическая культура")))
                        || ((subjectMine.getSubjectname().contains("Практика по получению")) && (subjectISIT.getSubjectname().contains("Практика по получению")))))) {
                    subjectMine.setDateofbeginsemester(subjectISIT.getDateofbeginsemester());
                    subjectMine.setDateofendsemester(subjectISIT.getDateofendsemester());
                    subjectMine.setLearnedSubject(true);
                    if ((subjectISIT.getIsExam() == 1 && subjectISIT.getExamRating() != 0)
                            || (subjectISIT.getIsPass() == 1 && subjectISIT.getPassRating() != 0)
                            || (subjectISIT.getIsCw() == 1 && subjectISIT.getCwRating() != 0)
                            || (subjectISIT.getIsCw() == 1 && subjectISIT.getCwRating() != 0)
                            || (subjectISIT.getIsPractic() == 1 && subjectISIT.getPracticRting() != 0)) {

                        subjectMine.setCheckSubject(true);
                    } else {
                        subjectMine.setCheckSubject(false);
                    }
                }
            }
        }
//        SubjectCurriculumModel model = new SubjectCurriculumModel();
//        SubjectCurriculumModel model1 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model2 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model3 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model4 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model5 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model6 = new SubjectCurriculumModel();
//        SubjectCurriculumModel model7 = new SubjectCurriculumModel();
//
//        model.setSemesterNumber(1);
//        model.setCodeSubject("Б1");
//        model.setSubjectname("Алгебра");
//        model.setZE(1);
//        model.setHoursAud(2);
//        model.setHoursAll(3);
//        model.getFocList().add("Экзамен");
//        model.setIdSem(68L);
//
//        model2.setSemesterNumber(1);
//        model2.setCodeSubject("А1");
//        model2.setSubjectname("Геометрия");
//        model2.setZE(1);
//        model2.setHoursAud(2);
//        model2.setHoursAll(3);
//        model2.getFocList().add("Экзамен");
//        model2.setIdSem(68L);
//
//        model3.setSemesterNumber(1);
//        model3.setCodeSubject("В1");
//        model3.setSubjectname("Анализ");
//        model3.setZE(1);
//        model3.setHoursAud(2);
//        model3.setHoursAll(3);
//        model3.getFocList().add("Экзамен");
//        model3.setIdSem(68L);
//
//        model1.setSemesterNumber(2);
//        model1.setCodeSubject("Б1");
//        model1.setSubjectname("Алгебра");
//        model1.setZE(1);
//        model1.setHoursAud(2);
//        model1.setHoursAll(3);
//        model1.getFocList().add("Экзамен");
//        model1.getFocList().add("КР");
//        model1.setIdSem(70L);
//
//        model4.setSemesterNumber(2);
//        model4.setCodeSubject("А1");
//        model4.setSubjectname("Геометрия");
//        model4.setZE(1);
//        model4.setHoursAud(2);
//        model4.setHoursAll(3);
//        model4.getFocList().add("Экзамен");
//        model4.setIdSem(70L);
//
//        model5.setSemesterNumber(2);
//        model5.setCodeSubject("В1");
//        model5.setSubjectname("Анализ");
//        model5.setZE(1);
//        model5.setHoursAud(2);
//        model5.setHoursAll(3);
//        model5.getFocList().add("Экзамен");
//        model5.getFocList().add("КП");
//        model5.setIdSem(70L);
//
//        model6.setSemesterNumber(3);
//        model6.setCodeSubject("А1");
//        model6.setSubjectname("Геометрия");
//        model6.setZE(1);
//        model6.setHoursAud(2);
//        model6.setHoursAll(3);
//        model6.getFocList().add("Экзамен");
//        model6.setIdSem(71L);
//
//        model7.setSemesterNumber(3);
//        model7.setCodeSubject("В1");
//        model7.setSubjectname("Анализ");
//        model7.setZE(1);
//        model7.setHoursAud(2);
//        model7.setHoursAll(3);
//        model7.getFocList().add("Экзамен");
//        model7.getFocList().add("КП");
//        model7.setIdSem(71L);
//
//        subjectsFromMine.add(model);
//        subjectsFromMine.add(model1);
//        subjectsFromMine.add(model2);
//        subjectsFromMine.add(model3);
//        subjectsFromMine.add(model4);
//        subjectsFromMine.add(model5);
//        subjectsFromMine.add(model6);
//        subjectsFromMine.add(model7);


        subjectsFromMine.sort(
                Comparator.comparing(SubjectCurriculumModel::getSemesterNumber)
                        .thenComparing(SubjectCurriculumModel::getCodeSubject)
        );

        return subjectsFromMine;
    }

    @Override
    public List<SubjectCurriculumModel> getSubjectsFromISIT(Long idOtherStudentcard) {
        return manager.getListSubjectFromISIT(idOtherStudentcard);
    }

    @Override
    public Long getOtherIdStudentcard(Long idHumanface) {
        return manager.getOtheridStudentcard(idHumanface);
    }

    @Override
    public Integer getCurSem(int fos, List<SubjectCurriculumModel> subjectsFromISIT) {
        Long idCurSem = manager.getCurrentSem(fos);
        Integer currentSemesterNumber = 0;
        for (SubjectCurriculumModel subject : subjectsFromISIT) {
            if (subject.getIdSem() == idCurSem) {
                currentSemesterNumber = subject.getSemesterNumber();

            }
        }
        return currentSemesterNumber;
    }
}
