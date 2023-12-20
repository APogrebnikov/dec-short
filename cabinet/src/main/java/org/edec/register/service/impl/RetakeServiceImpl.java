package org.edec.register.service.impl;

import lombok.extern.log4j.Log4j;
import org.edec.model.GroupModel;
import org.edec.register.manager.RegisterManager;
import org.edec.register.manager.RetakeManager;
import org.edec.register.model.*;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.register.service.RetakeService;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.zk.PopupUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Dimukhametov
 */

@Log4j
public class RetakeServiceImpl implements RetakeService {
    private RetakeManager retakeManager = new RetakeManager();
    private RegisterManager registerManager = new RegisterManager();

    @Override
    public List<GroupModel> getGroupsByFilter (String qualification, Integer course, Long idSem, String groupname) {
        return retakeManager.getGroupsByFilter(qualification, course, idSem, groupname);
    }

    @Override
    public List<RetakeSubjectModel> getRetakeSubjects (Long idSem, String listOfIdGroup, String filter) {
        return devideSubjectsByFOC(retakeManager.getSubjectsByFitler(idSem, listOfIdGroup, filter));
    }

//    @Override
//    public boolean openRetake (Long idLGSS, Integer foc, Date dateOfBegin, Date dateOfEnd, Integer vedomType) {
//        return retakeManager.openRetake(idLGSS, foc, dateOfBegin, dateOfEnd, vedomType);
//    }

    @Override
    public List<StudentModel> getCheckedStudentsForOpenRetake(RetakeSubjectModel subject, List<StudentModel> students) {
        return getCheckedStudentsForOpenRetake(subject, students, new ArrayList<>());
    }
    @Override
    public List<StudentModel> getCheckedStudentsForOpenRetake(RetakeSubjectModel subject, List<StudentModel> students, List<StudentModel> canceledStudents) {

        // Получаем список людей SSS, у них список SR, к списку SR - список SRH
        // Проверяем, чтобы человек удовлетворял следующим критериям
        // 1) Не был отчислен в том семестре                                                                        СДЕЛАНО
        // 2) Существовал в текущем семестре                                                                        СДЕЛАНО
        // 3) Не был отчислен в текущем семестре                                                                    СДЕЛАНО
        // 4) Не должно быть оценки по предмету по этой ФК                                                          СДЕЛАНО
        // 5) Если открывается общая пересдача - то не должна быть открыта общая пересдача                          СДЕЛАНО
        // 6) Если открывается индивидуальная пересдача - то не должна быть открыта индивидуальная пересдача        СДЕЛАНО
        // 7) Если человек востановился (is_transfered_student = 1) - общую пересдачу не открываем на него          СДЕЛАНО
        // 8) Проверяем, чтобы эта группа была текущей для студента, если нет - не открываем.                       СДЕЛАНО
        // 9) Не открываем пересдачу слушателям(is_listener = 1)                                                    СДЕЛАНО
        // 10) Не открывает по is_notactual = 1                                                                     СДЕЛАНО
        // 11) Не открываем академщикам                                                                             СДЕЛАНО
        // 12) не открываем задолжникам по оплате (проверяется в запросе по получению студентов)
        // 13) не открываем студентам с оценкой -1 (проверяем в функции isMarkNegative)

        if (canceledStudents == null) {
            canceledStudents = new ArrayList<>();
        }

        List<StudentModel> studentsForRetake = new ArrayList<>();

        //Не определена форма контроля - фиаско
        if (subject.getFocInt() == null) {
            PopupUtil.showError("Не определена форма контроля!");
            return studentsForRetake;
        }

        List<RetakeModel> listRetakes = separateListRetakesByIdSRH(registerManager.getListRatingByListGroupSubjects(
                Long.toString(subject.getIdLGSS()),
                getFokQueryForSubject(subject.getFocInt()),
                getFocQueryForLeftJoin(subject.getFocInt())
        ));

        for(RetakeModel retakeModel: listRetakes){
            for(StudentModel student : students){
                if (!retakeModel.getIdSSS().equals(student.getIdSSS())) {
                    continue;
                }

                boolean isRetakeOpen = false;

                //проверяем чтоб не была открыта общая или индивидуальная пересдача
                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED) {
                        isRetakeOpen = true;
                        student.setReason("Открыта общая пересдача");
                        canceledStudents.add(student);
                        break;
                    }
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED) {
                        isRetakeOpen = true;
                        student.setReason("Открыта индивидуальная пересдача");
                        canceledStudents.add(student);
                        break;
                    }
                }

                if(isRetakeOpen) continue;

                // Человек должен существовать в текущем семестре
                if (retakeModel.getDeductedCurSem() == null) {
                    continue;
                }

                // Человек не должен быть отчислен в текущем семестре
                if (retakeModel.getDeductedCurSem()) {
                    student.setReason("Отчислен в текущем семестре");
                    canceledStudents.add(student);
                    continue;
                }

                // Не должно быть оценки по предмету
                switch (FormOfControlConst.getName(subject.getFocInt())) {
                    case EXAM:
                        if (!isMarkNegative(retakeModel.getExamRating()) || !isMarkNegative(retakeModel.getEsoExamRating())) {
                            student.setReason("Есть положительная оценка");
                            canceledStudents.add(student);
                            continue;
                        }
                        break;
                    case PASS:
                        if (!isMarkNegative(retakeModel.getPassRating()) || !isMarkNegative(retakeModel.getEsoPassRating())) {
                            student.setReason("Есть положительная оценка");
                            canceledStudents.add(student);
                            continue;
                        }
                        break;
                    case CP:
                        if (!isMarkNegative(retakeModel.getCpRating()) || !isMarkNegative(retakeModel.getEsoCpRating())) {
                            student.setReason("Есть положительная оценка");
                            canceledStudents.add(student);
                            continue;
                        }
                        break;
                    case CW:
                        if (!isMarkNegative(retakeModel.getCwRating()) || !isMarkNegative(retakeModel.getEsoCwRating())) {
                            student.setReason("Есть положительная оценка");
                            canceledStudents.add(student);
                            continue;
                        }
                        break;
                    case PRACTIC:
                        if (!isMarkNegative(retakeModel.getPracticRating())) {
                            student.setReason("Есть положительная оценка");
                            canceledStudents.add(student);
                            continue;
                        }
                        break;
                    default:
                        continue;
                }

                long idGroup = retakeManager.getGroupByIdLgss(subject.getIdLGSS());

                // Если это не текущая группа
                if (!retakeModel.getIdCurDicGroup().equals(idGroup)) {
                    student.setReason("Группа не указана как текущая");
                    canceledStudents.add(student);
                    continue;
                }

                // Человек не должен быть в академическом отпуске в текущем семестре
                if (retakeModel.getAcademicLeaveCurSem()) {
                    student.setReason("В академ. отпуске");
                    canceledStudents.add(student);
                    continue;
                }

                student.setIdSemester(retakeModel.getIdSemester());
                student.setIdSR(retakeModel.getIdSR());
                student.setType(retakeModel.getType());

                studentsForRetake.add(student);

                break;
            }
        }

        return studentsForRetake;
    }

    @Override
    public boolean openRetake(RetakeSubjectModel subject, Integer foc, List<StudentModel> studentsForRetake, Date dateOfBegin, Date dateOfEnd, Integer typeRetake) {

        List<RetakeModel> retakeModels = new ArrayList<>();
        if (typeRetake == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED){

            for (StudentModel student : studentsForRetake){
                RetakeModel retake = new RetakeModel();
                retake.getStudents().add(student);
                retake.setIdSemester(student.getIdSemester());
                retake.setType(student.getType());
                retake.setTypeRetake(typeRetake);

                retakeModels.add(retake);
            }

            if (registerManager.createRetakeForModel(FormOfControlConst.getName(foc), retakeModels, dateOfBegin, dateOfEnd)){
                return true;
            }

        } else if (typeRetake == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED){

            StudentModel student = studentsForRetake.get(0);
            RetakeModel retake = new RetakeModel();
            retake.setStudents(studentsForRetake);
            retake.setIdSemester(student.getIdSemester());
            retake.setType(student.getType());
            retake.setTypeRetake(typeRetake);

            retakeModels.add(retake);

            if (registerManager.createRetakeForModel(FormOfControlConst.getName(foc), retakeModels, dateOfBegin, dateOfEnd)){
                return true;
            }
        }
        return false;
    }

    private List<RetakeModel> separateListRetakesByIdSRH(List<RetakeModelEso> listESO) {
        List<RetakeModel> retakeModels = new ArrayList<>();
        RetakeModel prevModel = null;
        for (RetakeModelEso retakeModelEso : listESO) {
            if (prevModel != null && prevModel.getIdSR().equals(retakeModelEso.getIdSR())) {
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
            } else {
                prevModel = createRetakeModel(retakeModelEso);
                prevModel.getListSRH().add(createSessionRatingHistoryModel(retakeModelEso));
                retakeModels.add(prevModel);
            }
        }

        return retakeModels;
    }

    private String getFokQueryForSubject(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "is_exam = 1 or srh.is_exam is null";
            case PASS:
                return "is_pass = 1 or srh.is_pass is null";
            case CP:
                return "is_courseproject = 1 or srh.is_courseproject is null";
            case CW:
                return "is_coursework = 1 or srh.is_coursework is null";
            case PRACTIC:
                return "is_practic = 1 or srh.is_practic is null";
            default:
                return null;
        }
    }

    private String getFocQueryForLeftJoin(int foc) {
        switch (FormOfControlConst.getName(foc)) {
            case EXAM:
                return "srh.is_exam = 1";
            case PASS:
                return "srh.is_pass = 1";
            case CP:
                return "srh.is_courseproject = 1";
            case CW:
                return "srh.is_coursework = 1";
            case PRACTIC:
                return "srh.is_practic = 1";
            default:
                return null;
        }
    }

    private boolean isMarkNegative(Integer mark) {
        return mark == null || mark < 3 && mark != 1 && mark != RatingConst.NOT_LEARNED.getRating();
    }

    private List<RetakeSubjectModel> devideSubjectsByFOC(List<RetakeSubjectModel> subjects) {
        List<RetakeSubjectModel> result = new ArrayList<>();

        for (RetakeSubjectModel subject : subjects) {
            if (subject.getExam()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Экзамен");
                tmpSubject.setFocInt(1);
                result.add(tmpSubject);
            }
            if (subject.getPass()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Зачет");
                tmpSubject.setFocInt(2);
                result.add(tmpSubject);
            }
            if (subject.getCp()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("КП");
                tmpSubject.setFocInt(3);
                result.add(tmpSubject);
            }
            if (subject.getCw()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("КР");
                tmpSubject.setFocInt(4);
                result.add(tmpSubject);
            }
            if (subject.getPractic()) {
                RetakeSubjectModel tmpSubject = createTmpSubject(subject);
                tmpSubject.setFoc("Практика");
                tmpSubject.setFocInt(5);
                result.add(tmpSubject);
            }
        }

        return result;
    }

    private RetakeSubjectModel createTmpSubject(RetakeSubjectModel subject) {
        RetakeSubjectModel tmpSubject = new RetakeSubjectModel();
        tmpSubject.setGroupname(subject.getGroupname());
        tmpSubject.setSubjectname(subject.getSubjectname());
        tmpSubject.setIdSubj(subject.getIdSubj());
        tmpSubject.setIdLGSS(subject.getIdLGSS());
        tmpSubject.setTeachers(subject.getTeachers());
        return tmpSubject;
    }

    private SessionRatingHistoryModel createSessionRatingHistoryModel(RetakeModelEso retakeModelEso) {
        SessionRatingHistoryModel srhModel = new SessionRatingHistoryModel();
        srhModel.setIdSRH(retakeModelEso.getIdSRH());
        srhModel.setRetakeCount(retakeModelEso.getRetakeCount());
        return srhModel;
    }

    private RetakeModel createRetakeModel(RetakeModelEso retakeModelEso) {
        RetakeModel retakeModel = new RetakeModel();
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeave());
        retakeModel.setIdSemester(retakeModelEso.getIdSemester());
        retakeModel.setCpRating(retakeModelEso.getCpRating());
        retakeModel.setCwRating(retakeModelEso.getCwRating());
        retakeModel.setDeductedCurSem(retakeModelEso.getDeductedCurSem());
        retakeModel.setAcademicLeaveCurSem(retakeModelEso.getAcademicLeaveCurSem());
        retakeModel.setExamRating(retakeModelEso.getExamRating());
        retakeModel.setFio(retakeModelEso.getFio());
        retakeModel.setIdCurDicGroup(retakeModelEso.getIdCurDicGroup());
        retakeModel.setIdSR(retakeModelEso.getIdSR());
        retakeModel.setIdSSS(retakeModelEso.getIdSSS());
        retakeModel.setListenerCurSem(retakeModelEso.getListenerCurSem());
        retakeModel.setTransferedStudent(retakeModelEso.getTransferedStudent());
        retakeModel.setTransferedStudentCurSem(retakeModelEso.getTransferedStudentCurSem());
        retakeModel.setPracticRating(retakeModelEso.getPracticRating());
        retakeModel.setPassRating(retakeModelEso.getPassRating());
        retakeModel.setType(retakeModelEso.getType());
        retakeModel.setEsoExamRating(retakeModelEso.getEsoExamRating());
        retakeModel.setEsoPassRating(retakeModelEso.getEsoPassRating());
        retakeModel.setEsoCpRating(retakeModelEso.getEsoCpRating());
        retakeModel.setEsoCwRating(retakeModelEso.getEsoCwRating());
        return retakeModel;
    }
}
