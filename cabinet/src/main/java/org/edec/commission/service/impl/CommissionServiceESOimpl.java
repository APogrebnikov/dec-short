package org.edec.commission.service.impl;

import org.edec.commission.manager.EntityManagerCommission;
import org.edec.commission.model.*;
import org.edec.commission.service.CommissionService;
import org.edec.manager.EntityManagerSemesterESO;
import org.edec.model.SemesterModel;
import org.edec.secretaryChair.model.StudentCommissionModel;
import org.edec.utility.converter.DateConverter;

import java.util.*;
import java.util.stream.Collectors;

public class CommissionServiceESOimpl implements CommissionService {
    private EntityManagerCommission emCommission = new EntityManagerCommission();
    private EntityManagerSemesterESO emSemesterESO = new EntityManagerSemesterESO();

    @Override
    public List<StudentCountDebtModel> getListStudentCountDebt (String qualification, Integer course, Integer debtCount, String typeDebt,
                                                                Integer government, String listIdSem, boolean prolongation,
                                                                Date dateProlongation, Integer formofstudy, Long idInst) {
        List<StudentCountDebtModel> listStudentCountDebt = emCommission.getStudentCountDebt(
                qualification, course, debtCount, typeDebt, government, listIdSem, prolongation, dateProlongation, formofstudy, idInst);

        // убираем студентов, у которых получается 0 долгов
        listStudentCountDebt = listStudentCountDebt.stream().filter(el -> el.getDebt() != 0).collect(Collectors.toList());

        return listStudentCountDebt;
    }

    @Override
    public List<StudentDebtModel> getDividedByFocStudentsDebt(Long idSc, Long idDg, String listIdSem, Integer formOfStudy) {
        List<StudentDebtModel> listStudentDebt = emCommission.getStudentDebt(idSc, idDg, listIdSem, formOfStudy);
        listStudentDebt = getDividedByFoc(listStudentDebt, false);
        return listStudentDebt;
    }

    @Override
    public List<StudentDebtModel> getStudentByRegisterCommission (Long idRegComm) {
        return emCommission.getStudentByIdRegCommission(idRegComm);
    }

    @Override
    public List<StudentCountDebtModel> getStudentsCountDebt(String fio, String group, List<StudentCountDebtModel> list){
        List<StudentCountDebtModel> result = new ArrayList<>(list);

        if(!fio.isEmpty()){
            result = result.stream()
                         .filter(element-> element.getFio().toLowerCase().contains(fio.toLowerCase()))
                         .collect(Collectors.toList());
        }

        if(!group.isEmpty()){
            result = result.stream()
                           .filter(element-> element.getGroupname().toLowerCase().contains(group.toLowerCase()))
                           .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    public List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                                      Integer formOfStudy) {
        List<SubjectDebtModel> subjectDebtModelList = emCommission.getSubjectCommissionByFilterAndSem(fioGroupSubject, idSem, period, formOfStudy);
        for (SubjectDebtModel model : subjectDebtModelList) {
            model.getStudents().addAll(getStudentByRegisterCommission(model.getIdRegComission()));
        }
        return subjectDebtModelList;
    }

    @Override
    public List<SubjectDebtModel> getSubjectCommissionGraduateByPeriod(String fioGroupSubject, Long idSem, PeriodCommissionModel period, Integer formOfStudy) {
        List<SubjectDebtModel> subjectDebtModelList = emCommission.getSubjectCommissionByFilterAndSem(fioGroupSubject, idSem, period, formOfStudy);
        subjectDebtModelList = subjectDebtModelList.stream().filter(e -> e.getDateofbegincomission().equals(period.getDateOfBegin())
        && e.getDateofendcomission().equals(period.getDateOfEnd())).collect(Collectors.toList());
        for (SubjectDebtModel model : subjectDebtModelList) {
            model.getStudents().addAll(getStudentByRegisterCommission(model.getIdRegComission()));
            model.setSemesterStr(DateConverter.convertDateToString(period.getDateOfBegin()) + "-" +
                    DateConverter.convertDateToString(period.getDateOfEnd()));
        }
        return subjectDebtModelList;
    }

    @Override
    public List<SubjectDebtModel> getSubjectCommissionByFilterAndSem (String fioGroupSubject, Long idSem, PeriodCommissionModel period,
                                                                      Integer formOfStudy, String formOfControl, Boolean isIndividual, Long idChair, String teacherStr) {
        return emCommission.getSubjectCommissionByFilterAndSem(fioGroupSubject, idSem, period, formOfStudy, formOfControl, isIndividual, idChair).stream()
                           .filter(subjectDebtModel -> subjectDebtModel.getTeachers().equals(teacherStr))
                           .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDebtModel> getSubjForCreateCommission (String listIdSSS, Date dateBegin, Date dateEnd) {
        List<SubjectDebtModel> result = new ArrayList<>();

        List<StudentDebtModel> listStudent = emCommission.getStudentDebtByListSSSandDateCommission(listIdSSS, dateEnd);
        listStudent = getDividedByFoc(listStudent, true);

        for (StudentDebtModel student : listStudent) {

            SubjectDebtModel subject = result.stream()
                                             .filter(subjectDebtModel -> subjectDebtModel.getIdChair().equals(student.getIdChair()) &&
                                                                         subjectDebtModel.getSemesternumber()
                                                                                         .equals(student.getSemesternumber()) &&
                                                                         subjectDebtModel.getFocStr().equals(student.getFocStr()) &&
                                                                         subjectDebtModel.getSemesterStr()
                                                                                         .equals(student.getSemesterStr()) &&
                                                                         subjectDebtModel.getSubjectname().equals(student.getSubjectname()) /*&&
                                                     subjectDebtModel.getTeachers().equals(student.getTeacherStr())*/) // Не группируем по преподавателям
                                             .findFirst()
                                             .orElse(null);

            if (subject == null) {
                subject = new SubjectDebtModel();
                subject.setIdSemester(student.getIdSemester());
                subject.setSemesterStr(student.getSemesterStr());
                subject.setSemesternumber(student.getSemesternumber());
                subject.setSubjectname(student.getSubjectname());
                subject.setDateofbegincomission(dateBegin);
                subject.setDateofendcomission(dateEnd);
                subject.setFocStr(student.getFocStr());
                subject.setFulltitle(student.getFulltitle());
                subject.setIdChair(student.getIdChair());
                subject.setIdSubj(student.getIdSubj());
                subject.setTeachers(student.getTeacherStr());
                result.add(subject);
            }

            subject.getStudents().add(student);
        }

        Collections.sort(result);
        return result;
    }

    @Override
    public List<SubjectDebtModel> getSubjectsByFilter (String filter, List<SubjectDebtModel> allSubjects) {
        List<SubjectDebtModel> result = new ArrayList<>();
        for (SubjectDebtModel subj : allSubjects) {
            if (subj.getSubjectname().toLowerCase().contains(filter.toLowerCase())) {
                result.add(subj);
                continue;
            }
            StudentDebtModel student = subj.getStudents()
                                           .stream()
                                           .filter(studentModel -> studentModel.getFio().toLowerCase().contains(filter.toLowerCase()) ||
                                                                   studentModel.getGroupname().toLowerCase().contains(filter.toLowerCase()))
                                           .findAny()
                                           .orElse(null);
            if (student != null) {
                result.add(subj);
            }
        }
        return result;
    }

    @Override
    public List<SemesterModel> getAllSemesterWithCommission (Integer formOfStudy) {
        return emCommission.getSemesterCommission(formOfStudy);
    }

    @Override
    public List<SemesterModel> getSemesterByInstAndFOS (Long idInst, Integer fos) {
        return emSemesterESO.getSemesters(idInst, fos, null);
    }

    @Override
    public List<CommissionStructureModel> getCommissionStructure (Long idCommission) {
        return emCommission.getCommissionStructure(idCommission);
    }

    @Override
    public boolean updateStatusNotificationCom(Long idRegCom) {
        return emCommission.updateStatusNotificationComm(idRegCom);
    }

    @Override
    public List<Long> getListSSSForCommission(Long idRegCom) {
        return emCommission.getSSSForComission(idRegCom);
    }
    @Override
    public List<PeriodCommissionModel> getPeriodCommission (Integer formOfStudy) {
        return emCommission.getPeriodCommission(formOfStudy);
    }

    @Override
    public PeriodCommissionModel getPeriodCommissionForGradute(Integer formOfStudy, Long idSem) {
        PeriodCommissionModel model = new PeriodCommissionModel();
        List<PeriodCommissionModel> list = emCommission.getPeriodCommissionForGraduate(formOfStudy, idSem);
        PeriodCommissionModel modelBegin = list.stream().min(Comparator.comparing(PeriodCommissionModel::getDateOfBegin)).orElse(null);
        PeriodCommissionModel modelEnd = list.stream().max(Comparator.comparing(PeriodCommissionModel::getDateOfEnd)).orElse(null);
        model.setDateOfBegin(modelBegin.getDateOfBegin());
        model.setDateOfEnd(modelEnd.getDateOfEnd());
        model.setGraduate(true);
        model.setIdSem(idSem);
        return model;
    }

    @Override
    public  List<PeriodCommissionModel> getCommissionForGraduteBySem(Integer formOfStudy, Long idSem) {
        return emCommission.getPeriodCommissionForGraduate(formOfStudy, idSem);
    }

    @Override
    public boolean createIndividualCommission (StudentDebtModel studentDebtModel, Date dateBegin, Date dateEnd, Long idCurrentUser) {

        return emCommission.createIndividualCommission(studentDebtModel, dateBegin, dateEnd, idCurrentUser);
    }

    @Override
    public boolean createCommonCommission (SubjectDebtModel subjectDebtModel, List<StudentDebtModel> students, Long idCurrentUser) {
        return emCommission.createCommonCommission(subjectDebtModel, students, idCurrentUser);
    }

    @Override
    public boolean addStudentToCommission(StudentDebtModel student, SubjectDebtModel subjectDebtModel, Long idCurrentUser){
        return  emCommission.addStudentToCommission(student, subjectDebtModel, idCurrentUser);
    }

    @Override
    public boolean deleteCommission (Long idRegComm) {
        return emCommission.deleteComission(idRegComm);
    }

    @Override
    public boolean deleteSRHfromCommRegister (String listIDsrh) {
        return emCommission.deleteSRHfromComissionRegister(listIDsrh);
    }

    @Override
    public boolean updateCommissionRegister (Long idRegComm, Date dateBegin, Date dateEnd) {
        return emCommission.updateRegisterComission(idRegComm, dateBegin, dateEnd);
    }

    @Override
    public boolean setCheckKutsSrh (Long idSrh, boolean status) {
        return emCommission.setCheckKutsForSRH(idSrh, status);
    }

    @Override
    public void setStatusSignedForSubjAndStudent (SubjectDebtModel subject) {
        subject.setSigned(true);
        for (StudentDebtModel student : subject.getStudents()) {
            student.setOpenComm(true);
        }
    }

    @Override
    public void setStatusSignedForSubjAndStudents (SubjectDebtModel subject, List<StudentDebtModel> students) {
        for (StudentDebtModel student : subject.getStudents()) {
            if(students.contains(student)) {
                student.setOpenComm(true);
            }
        }
    }

    @Override
    public boolean checkStudentAvailableForCommission(String fio, Date dateCommission) {
        return emCommission.checkStudentCommissionsByDate(fio, dateCommission) == 0;
    }

    /**
     * Делит модель на формы контроля
     *
     * @param tempListStudentDebt - модель
     * @param openComm            - если true, то нужно чтобы не было комиссий у студента по любому из предметов, иначе показываем все долги
     * @return
     */
    public List<StudentDebtModel> getDividedByFoc(List<StudentDebtModel> tempListStudentDebt, boolean openComm) {
        List<StudentDebtModel> result = new ArrayList<>();

        List<StudentDebtModel> examList = new ArrayList<>();
        List<StudentDebtModel> passList = new ArrayList<>();
        List<StudentDebtModel> cpList = new ArrayList<>();
        List<StudentDebtModel> cwList = new ArrayList<>();
        List<StudentDebtModel> practicList = new ArrayList<>();

        for (StudentDebtModel model : tempListStudentDebt) {

            if (model.getExam() && (model.getExamrating() < 3 && model.getEsoExamRating() != -1) && (!openComm || !model.getExamComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getExamComm());
                newModel.setFocStr("Экзамен");
                examList.add(newModel);

            }
            if (model.getPass() && (model.getPassrating() != 1 && model.getEsoPassRating() != -1) && (!openComm || !model.getPassComm()) ) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getPassComm());
                newModel.setFocStr("Зачет");
                passList.add(newModel);
            }
            if (model.getCp() && (model.getCprating() < 3 && model.getEsoCpRating() != -1) && (!openComm || !model.getCpComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getCpComm());
                newModel.setFocStr("КП");
                cpList.add(newModel);
            }
            if (model.getCw() && (model.getCwrating() < 3 && model.getEsoCwRating() != -1) && (!openComm || !model.getCwComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getCwComm());
                newModel.setFocStr("КР");
                cwList.add(newModel);
            }
            if (model.getPractic() && !model.getPass() && model.getPracticrating() < 3 && (!openComm || !model.getPracticComm())) {
                StudentDebtModel newModel = createFOCmodel(model);
                newModel.setOpenComm(model.getPracticComm());
                newModel.setFocStr("Практика");
                practicList.add(newModel);
            }
        }

        result.addAll(examList);
        result.addAll(passList);
        result.addAll(cpList);
        result.addAll(cwList);
        result.addAll(practicList);
        Collections.sort(result);

        return result;
    }

    private StudentDebtModel createFOCmodel (StudentDebtModel tempModel) {
        StudentDebtModel newModel = new StudentDebtModel();
        newModel.setFio(tempModel.getFio());
        newModel.setFulltitle(tempModel.getFulltitle());
        newModel.setIdChair(tempModel.getIdChair());
        newModel.setIdSr(tempModel.getIdSr());
        newModel.setIdSemester(tempModel.getIdSemester());
        newModel.setIdSubj(tempModel.getIdSubj());
        newModel.setGroupname(tempModel.getGroupname());
        newModel.setSemesterStr(tempModel.getSemesterStr());
        newModel.setSemesternumber(tempModel.getSemesternumber());
        newModel.setSubjectname(tempModel.getSubjectname());
        newModel.setIsGovernmentFinanced(tempModel.getIsGovernmentFinanced());
        newModel.setTeacherStr(tempModel.getTeacherStr());
        return newModel;
    }
}
