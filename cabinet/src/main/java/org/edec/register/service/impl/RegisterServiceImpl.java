package org.edec.register.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.edec.commons.entity.mine.Marks;
import org.edec.commons.manager.mine.EntityManagerRegisterMine;
import org.edec.register.manager.RegisterManager;
import org.edec.register.model.*;
import org.edec.register.model.dao.RegisterModelEso;
import org.edec.register.model.dao.RegisterWithSignDateModelESO;
import org.edec.register.model.dao.RetakeModelEso;
import org.edec.register.model.mine.StudentMineModel;
import org.edec.register.service.RegisterService;
import org.edec.utility.DateUtility;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.constants.RatingConst;
import org.edec.utility.constants.RegisterConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONObject;
import org.zkoss.zk.ui.util.Clients;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by antonskripacev on 10.06.17.
 */
public class RegisterServiceImpl implements RegisterService {
    private final Logger log = Logger.getLogger(RegisterServiceImpl.class.getName());
    private RegisterManager manager = new RegisterManager();
    private EntityManagerRegisterMine mineRegisterManager = new EntityManagerRegisterMine();
    //private EntityManagerRegisterMine mineRegisterManager = null;

    @Override
    public List<StudentMineModel> getFilteredStudentsForRegisterFromMine(RegisterModel register) {
        if (register.getIdRegisterMine() != null) {
            List<Marks> mineRegisterMarks = mineRegisterManager.getMarksByRegister(register.getIdRegisterMine());

            return mineRegisterMarks.stream().map(e -> {
                StudentMineModel studentMineModel = new StudentMineModel();
                studentMineModel.setFio(e.getStudent().getFamily() + " " + e.getStudent().getName() + " " + e.getStudent().getPatronymic());
                studentMineModel.setMainMark(RatingConst.getByMineRating(e.getMainMark()));
                studentMineModel.setMark1(RatingConst.getByMineRating(e.getRetake1()));
                studentMineModel.setMark2(RatingConst.getByMineRating(e.getRetake2()));

                return studentMineModel;
            }).filter(e -> register.getStudents().stream().map(studentModel -> studentModel.getFio().trim()).collect(Collectors.toList())
                    .contains(e.getFio())).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @Override
    public List<RegisterModel> getRetakesByFilter(Long idInstitute, int formOfStudy, Long idSemester, String groupname, String fioStudent,
                                                  String fioTeacher, boolean[] checkBoxes) {
        List<RegisterModel> retakes = groupBySubjectAndFoc(manager.getListRetakes(idInstitute, formOfStudy, idSemester, groupname, checkBoxes));

        return filterList(retakes, checkBoxes, fioStudent, fioTeacher);
    }

    @Override
    public void updateDatesRegister(Date dateBegin, Date dateEnd, Long idSRH) {
        manager.updateSrDates(dateBegin, dateEnd, idSRH);
    }

    @Override
    public void updateDateMainRegister(Date dateBegin, Date dateEnd, Long idLgss) {
        manager.updateDateMainRegister(dateBegin, dateEnd, idLgss);
    }

    @Override
    public File getFileRegister(String registerUrl, Long idRegister) {
        if (registerUrl == null) {
            return null;
        }

        if (registerUrl.contains(".pdf")) {
            return new FileManager().getFileByRelativePath(registerUrl);
        }

        return new FileManager().getFileByRelativePath(registerUrl + File.separator + idRegister + ".pdf");
    }

    @Override
    public boolean cancelRegister(RegisterModel model) {
        /**
         * 1 модификация sr - обнуляем поля оценок устанавливаем статус 0.0.0
         * 2 модификация srh - инвертируем retake_count
         * 3 Обнуляем поля в register
         * 4 переносим файл
         */
        File registerFile = null;

        try {
            manager.updateCancelSignInTransaction(model);

            /** ==== 4 ==== **/
            registerFile = new FileManager().getFileByRelativePath(model.getRegisterUrl());
            String registerUrl = registerFile.getAbsolutePath();
            String[] splitUrlArray = registerUrl.split("\\.");
            String urlTo = splitUrlArray[0] + "_canceled/";
            File directory = new File(urlTo);
            if (!directory.exists()) {
                new File(urlTo).mkdirs();
            }
            String canceledFile =
                    urlTo + registerFile.getName().split("\\.")[0] + "_" + new SimpleDateFormat("yyyy-MM-dd--HH-mm-ss").format(new Date()) +
                            ".pdf";
            registerFile.renameTo(new File(canceledFile));

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            try {
                registerFile.renameTo(new FileManager().getFileByRelativePath(model.getRegisterUrl()));
            } catch (Exception e2) {
                System.out.println("Houston, we have a problem with register: ");

                if (model.getIdRegister() != null) {
                    System.out.println(model.getIdRegister());
                } else {
                    System.out.println("ou fck, we don't have id, god damned");
                }
            }

            return false;
        }
    }

    private List<RegisterModel> filterList(List<RegisterModel> retakes, boolean[] filters, String fioStudent, String fioTeacher) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);

        for (int i = 0; i < retakes.size(); ++i) {
            // Выпускники
            if (!filters[ENDED] && retakes.get(i).getDateOfGroupFinish() != null && retakes.get(i).getDateOfGroupFinish().before(new Date()))
            {
                retakes.remove(i);
                i--;
                continue;
            }

            if (filters[OPENED] && retakes.get(i).getIdRegister() == null)
            {
                retakes.remove(i);
                i--;
                continue;
            }

            if (filters[SIGNED] && retakes.get(i).getSignDate() == null) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[OPENED] && (retakes.get(i).getCertNumber() == null || retakes.get(i).getCertNumber().equals(""))) {

                if (retakes.get(i).getDateOfEnd() == null || retakes.get(i).getDateOfEnd().after(cal.getTime())
                    || (retakes.get(i).getDateOfSecondSignEnd() != null
                        && DateUtility.isSameDayOrAfter(retakes.get(i).getDateOfSecondSignEnd(), new Date()))) {
                    // Просроченные
                    if (filters[OUT_OF_DATE]) {
                        // Если ведомость старая - учитываем подпись без ЭП
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dataOld = null;
                        try {
                            dataOld = sdf.parse("2017-01-01");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (retakes.get(i).getSignDate() != null && retakes.get(i).getSignDate().before(dataOld)) {
                            retakes.remove(i);
                            i--;
                            continue;
                        }
                        // Если ведомость новая - учитываем подпись ЭП
                        else {
                            if((retakes.get(i).getCertNumber() == null || retakes.get(i).getCertNumber() == "")
                                && retakes.get(i).getMainDate() != null
                                && DateUtility.isAfterDate(new Date(), retakes.get(i).getMainDate())
                                && !DateUtility.isSameDayOrNextWithWeekends(new Date(), retakes.get(i).getMainDate())
                                //&& (retakes.get(i).getRetakeCount() == -1 || retakes.get(i).getRetakeCount() == 1)
                                //&& retakes.get(i).getIdRegister() != null
                            ) {
                                continue;
                            } else {
                                retakes.remove(i);
                                i--;
                                continue;
                            }
                        }
                    }

                }
            }

            if (!filters[SIGNED] && retakes.get(i).getCertNumber() != null) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[OUT_OF_DATE] && (retakes.get(i).getCertNumber() == null || retakes.get(i).getCertNumber().equals(""))) {
                if ((retakes.get(i).getDateOfEnd() != null && retakes.get(i).getDateOfEnd().before(cal.getTime()))
                && (retakes.get(i).getDateOfSecondSignEnd() == null || !DateUtility.isSameDayOrAfter(retakes.get(i).getDateOfSecondSignEnd(), new Date()))) {
                    retakes.remove(i);
                    i--;
                    continue;
                }
            }

            if (!filters[MAIN] && (retakes.get(i).getRetakeCount() == RegisterConst.TYPE_MAIN ||
                    retakes.get(i).getRetakeCount() == RegisterConst.TYPE_MAIN_NOT_SIGNED)) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[MAIN_RETAKE] && (retakes.get(i).getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN ||
                    retakes.get(i).getRetakeCount() == RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED)) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[INDIVIDUAL_RETAKE] && (retakes.get(i).getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV ||
                    retakes.get(i).getRetakeCount() == RegisterConst.TYPE_RETAKE_INDIV_NOT_SIGNED)) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[COMMISSION] && (retakes.get(i).getRetakeCount() == RegisterConst.TYPE_COMMISSION ||
                    retakes.get(i).getRetakeCount() == RegisterConst.TYPE_COMMISSION_NOT_SIGNED)) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[BACHELOR] && retakes.get(i).getQualification().equals(QualificationConst.BACHELOR.getValue())) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[MASTER] && retakes.get(i).getQualification().equals(QualificationConst.MASTER.getValue())) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!filters[SPECIALITY] && retakes.get(i).getQualification().equals(QualificationConst.SPECIALIST.getValue())) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (filters[WITH_NUMBER] && (retakes.get(i).getRegisterNumber() == null || retakes.get(i).getRegisterNumber().equals(""))) {
                retakes.remove(i);
                i--;
                continue;
            }

            if (!fioTeacher.equals("")) {
                if (!retakes.get(i).getTeachers().toString().toLowerCase().contains(fioTeacher.toLowerCase())) {
                    retakes.remove(i);
                    i--;
                    continue;
                }
            }

            if (fioStudent.equals("")) {
                continue;
            }

            boolean isFinded = false;
            outer:
            for (StudentModel student : retakes.get(i).getStudents()) {
                for (int j = 0; j < student.getFio().length(); j++) {
                    if (student.getFio().substring(j).toLowerCase().startsWith(fioStudent.toLowerCase())) {
                        isFinded = true;
                        break outer;
                    }

                    if (student.getFio().substring(j).length() < fioStudent.length()) {
                        break;
                    }
                }
            }

            if (!isFinded) {
                retakes.remove(i);
                i--;
            }
        }

        return retakes;
    }

    private List<RegisterModel> groupBySubjectAndFoc(List<RegisterModelEso> listEso) {
        // costil, из-за некоторых криво заполненных оценок в бд
        listEso = listEso.stream().filter(e -> e.getFoc() != null).collect(Collectors.toList());

        List<RegisterModel> listRetakes = new ArrayList<>();

        RegisterModel prevModel = null;
        RegisterModelEso prevItem = null;

        for (int i = 0; i < listEso.size(); ++i) {
            RegisterModelEso item = listEso.get(i);
            if (i != 0) {
                prevItem = listEso.get(i - 1);
            }

            if (prevModel == null) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!Objects.equals(item.getIdSemester(), prevModel.getIdSemester())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!item.getGroupName().equals(prevModel.getGroupName())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!item.getIdSubject().equals(prevModel.getIdSubject())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!item.getFoc().equals(prevModel.getFoc())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!Objects.equals(item.getIdRegister(), prevModel.getIdRegister())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (!Objects.equals(prevModel.getRetakeCount(), item.getRetakeCount())) {
                prevModel = createRetakeModel(item);
                listRetakes.add(prevModel);
                continue;
            }

            if (prevItem.getIdSSS().equals(item.getIdSSS())) {
                prevModel.getTeachers()
                        .add(item.getFamilyTeacher() + " " + item.getNameTeacher() + " " + item.getPatronymicTeacher() + "\n");
            } else {
                if (Math.abs(item.getRetakeCount()) == 4) {
                    prevModel = createRetakeModel(item);
                    listRetakes.add(prevModel);
                } else {
                    prevModel.getStudents().add(createStudentModel(item));
                }
            }
        }

        return listRetakes;
    }

    private RegisterModel createRetakeModel(RegisterModelEso eso) {
        RegisterModel model = new RegisterModel();

        model.setCertNumber(eso.getCertNumber());
        model.setSignDate(eso.getSignDate());
        model.setDateOfEnd(eso.getDateOfEndComission() == null ? eso.getDateOfEnd() : eso.getDateOfEndComission());
        model.setDateOfBegin(eso.getDateOfBegin());
        model.setDateOfSecondSignBegin(eso.getDateOfSecondSignBegin());
        model.setDateOfSecondSignEnd(eso.getDateOfSecondSignEnd());
        model.setFoc(eso.getFoc());
        model.setQualification(eso.getQualification());
        model.setFos(eso.getFos());
        model.setIdRegister(eso.getIdRegister());
        model.setIdRegisterMine(eso.getIdRegisterMine());
        model.setIdSemester(eso.getIdSemester());
        model.setIdInstitute(eso.getIdInstitute());
        model.setSynchStatus(eso.getSynchStatus());
        model.setIdSubject(eso.getIdSubject());
        model.setRegisterNumber(eso.getRegisterNumber());
        model.setRegisterUrl(eso.getRegisterUrl());
        model.setRetakeCount(eso.getRetakeCount());
        model.setSemester(eso.getSemester());
        model.setSubjectName(eso.getSubjectName());
        model.setGroupName(eso.getGroupName());
        model.setTeachers(new HashSet<>());
        model.setExamDate(eso.getExamDate());
        model.setPassDate(eso.getPassDate());
        model.setCwDate(eso.getCwDate());
        model.setCpDate(eso.getCpDate());
        model.setPracticDate(eso.getPracticDate());
        model.setIdLgss(eso.getIdLgss());
        model.setDateBeginMainRegister(eso.getBeginDateMainRegister());
        model.setDateEndMainRegister(eso.getEndDateMainRegister());
        model.setDateOfGroupFinish(eso.getDateOfGroupFinish());

        if (eso.getFamilyTeacher() != null) {
            model.getTeachers().add(eso.getFamilyTeacher() + " " + eso.getNameTeacher() + " " + eso.getPatronymicTeacher() + "\n");
        }

        model.getStudents().add(createStudentModel(eso));

        return model;
    }

    private StudentModel createStudentModel(RegisterModelEso eso) {
        StudentModel model = new StudentModel();

        model.setChangeDateTime(eso.getChangeDateTime());
        model.setIdSRH(eso.getIdSRH());
        model.setRating(eso.getRating());
        model.setFamily(eso.getFamily());
        model.setName(eso.getName());
        model.setIdSSS(eso.getIdSSS());
        model.setPatronymic(eso.getPatronymic());
        model.setType(eso.getType());

        return model;
    }

    @Override
    public boolean openRetake(SubjectModel subjectModel, int typeRetake, Date dateOfBegin, Date dateOfEnd, String userFio) {
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

        // TODO МОЖЕМ ЛИ МЫ ОТКРЫВАТЬ ВЕДОМОСТИ В АКАДЕМ ОТПУСКЕ?
        String ids = subjectModel.getIdLgss().toString();

        //Не определена форма контроля - фиаско
        if (subjectModel.getFoc() == null) {
            return false;
        }

        List<RetakeModel> listRetakes = separateListRetakesByIdSRH(
                manager.getListRatingByListGroupSubjects(ids, getFokQueryForSubject(subjectModel),
                        getFocQueryForLeftJoin(subjectModel.getFoc())
                ));

        //если уже открыта или прошла основная пересдача - не открываем
        if (typeRetake == MAIN_RETAKE) {
            for (RetakeModel retakeModel : listRetakes) {
                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && Math.abs(historyModel.getRetakeCount()) == 2) {
                        return true;
                    }
                }
            }
        }

        retakeCycle:
        for (RetakeModel retakeModel : listRetakes) {
            // Человек должен существовать в текущем семестре
            if (retakeModel.getDeductedCurSem() == null) {
                continue;
            }

            // Человек не должен быть отчислен в текущем семестре
            if (retakeModel.getDeductedCurSem() == true) {
                continue;
            }

            // Не должно быть оценки по предмету
            switch (FormOfControlConst.getName(subjectModel.getFoc())) {
                case EXAM:
                    if (!isMarkNegative(retakeModel.getExamRating())) {
                        continue;
                    }
                    break;
                case PASS:
                    if (!isMarkNegative(retakeModel.getPassRating())) {
                        continue;
                    }
                    break;
                case CP:
                    if (!isMarkNegative(retakeModel.getCpRating())) {
                        continue;
                    }
                    break;
                case CW:
                    if (!isMarkNegative(retakeModel.getCwRating())) {
                        continue;
                    }
                    break;
                case PRACTIC:
                    if (!isMarkNegative(retakeModel.getPracticRating())) {
                        continue;
                    }
                    break;
                default:
                    continue;
            }

            //Если человек востановился - на него общую персдачу не открываем
            /*if(typeRetake == MAIN_RETAKE && (retakeModel.getTransferedStudent() || retakeModel.getTransferedStudentCurSem())) {
                continue;
            }*/

            // Если это не текущая группа
            if (!retakeModel.getIdCurDicGroup().equals(subjectModel.getIdGroup())) {
                continue;
            }

            // Человек не должен быть в академическом отпуске в текущем семестре
            if (retakeModel.getAcademicLeaveCurSem() == true) {
                continue;
            }

            // если уже открыта индивидуальная пересдача - не открывать
            if (typeRetake == INDIVIDUAL_RETAKE) {
                for (SessionRatingHistoryModel historyModel : retakeModel.getListSRH()) {
                    if (historyModel.getIdSRH() != null && historyModel.getRetakeCount().intValue() == -4) {
                        manager.updateSrDates(dateOfBegin, dateOfEnd, historyModel.getIdSRH());
                        continue retakeCycle;
                    }
                }
            }

//            if (!manager.createRetakeForModel(FormOfControlConst.getName(subjectModel.getFoc()), retakeModel, typeRetake, dateOfBegin,
//                    dateOfEnd
//            )) {
//                log.error("Не удалось открыть " + (typeRetake == MAIN_RETAKE ? "основную пересдачу" : "индивидуальную пересдачу") +
//                        "; студенту " + retakeModel.getFio() + "; группа " + subjectModel.getGroupName() + "; по предмету " +
//                        subjectModel.getSubjectName() + "; пользователь " + userFio);
//                return false;
//            } else {
//                log.info("Пользователь " + userFio + " открыл " +
//                        (typeRetake == MAIN_RETAKE ? "основную пересдачу" : "индивидуальную пересдачу") + "; студенту " +
//                        retakeModel.getFio() + "; группа " + subjectModel.getGroupName() + "; по предмету " +
//                        subjectModel.getSubjectName());
                //TODO send notification to student

                /*
                NotificationModel notification = new NotificationModel();
				notification.setSubject("Индивидуальная пересдача");
				notification.setMessage("<p>Уважаемый, <strong>"+student.getFamily() + " " + student.getName() + " " +
								student.getPatronymic()+"</strong>, Вам назначена пересдача по предмету <strong>"+subjectname+"("+formofcontrol+")</strong>." +
								" Вам необходимо сдать предмет в срок <strong>с "+beginDate.toString() + " по " + finishDate.toString() + "</strong></p>");
			    notification.setPriority(5);
			    notification.setSenderId(2493l);
				notification.setType("danger");
				notification.setDate(new Date());

				ServerNotification serverNotif = new ServerNotification();
				List<String> reciptients = new ArrayList<>();
				reciptients.add(String.valueOf(student.getId()));
				serverNotif.addNotification(notification);
                 */
          //  }
        }

        return true;
    }

    private boolean isMarkNegative(Integer mark) {
        if (mark == null) {
            return true;
        }

        if (mark < 3 && mark != 1) {
            return true;
        }

        return false;
    }

    private String getFokQueryForSubject(SubjectModel subjectModel) {
        switch (FormOfControlConst.getName(subjectModel.getFoc())) {
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
        return retakeModel;
    }

    public List<RegisterWithSignDateModelESO> getAllCommissionsWithNullSignDate() {
        return manager.getAllCommissionsWithNullSignDate();
    }

    public void updateDateSignForRegister(Long idRegister, Long idSRH, Date dateSign) {
        manager.updateDateSignForRegister(idRegister, idSRH, dateSign);
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

    @Override
    public String getAllTeacherForRegister(Long idRegister) {
        return String.join(", ", manager.getListTeachersByRegister(idRegister));
    }

    public boolean reSyncRegister(Long idRegister) {

        manager.unSignRegister(idRegister);

        String urlSignRegister = "http://localhost:8081/sync/register";

        JSONObject jsonRegister = new JSONObject();
        jsonRegister.put("idRegister", idRegister);

        String statusResponse = HttpClient.makeHttpRequest(urlSignRegister, HttpClient.POST, new ArrayList<>(), jsonRegister.toString());

        try {
            JSONObject jsonStatus = new JSONObject(statusResponse);
            return StringUtils.equalsIgnoreCase(jsonStatus.getString("status"), "success");
        } catch (Exception ignored) {
        }
        return false;
    }

    @Override
    public boolean updateSecondSignDate(Long idRegister, Date dateOfBeginSecondSign, Date dateOfEndSecondSign) {
        return manager.setSecondSignDate(idRegister, dateOfBeginSecondSign, dateOfEndSecondSign);
    }

    @Override
    public List<StudentModel> getStudentForPreview(Long idLgss) {
        return manager.getStudentForPreview(idLgss);
    }

    @Override
    public boolean removeRetake(RegisterModel registerModel, String fioUser) {

        for(StudentModel student : registerModel.getStudents()){
            boolean result = manager.removeSRH(student.getIdSRH());
            String message;

            if (result) {
                message = String.format("Пользователь %s; " +
                              "удалил %s пересдачу " +
                              "студенту %s; " +
                              "группа %s; " +
                              "предмет %s",
                              fioUser,
                              (registerModel.getRetakeCount().equals(RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED)
                                      ? "общ." : "инд."),
                              student.getFio(),
                              registerModel.getGroupName(),
                              registerModel.getSubjectName());

                log.info(message);
            } else {
                Clients.showNotification("Не удалось удалить ведомость: " + manager.getLastMessage());

                message = String.format("Не удалось %s пересдачу;" +
                                        " пользователь %s;" +
                                        " студенту %s;" +
                                        " группа %s;" +
                                        " предмет %s",
                                        (registerModel.getRetakeCount().equals(RegisterConst.TYPE_RETAKE_MAIN_NOT_SIGNED)
                                         ? "общ." : "инд."),
                                        fioUser,
                                        student.getFio(),
                                        registerModel.getGroupName(),
                                        registerModel.getSubjectName());

                log.error(message);

                return false;
            }
        }

        return true;
    }
}
