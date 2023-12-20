package org.edec.rest.ctrl;

import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.edec.rest.manager.LoginRestDAO;
import org.edec.rest.manager.TeacherRestDAO;
import org.edec.rest.model.UserMsg;
import org.edec.rest.model.student.request.SubjectRatingsMsg;
import org.edec.rest.model.student.request.TeacherRetakeMsg;
import org.edec.rest.model.student.request.TeacherRetakeRatingMsg;
import org.edec.rest.model.student.request.TeacherSubjectsMsg;
import org.edec.rest.model.student.response.Teacher;
import org.edec.rest.model.student.response.TeacherSemester;
import org.edec.rest.model.student.response.TeacherSubject;
import org.edec.rest.model.teacher.request.SubjectMarkMsg;
import org.edec.teacher.manager.EntityManagerRegister;
import org.edec.teacher.model.RetakeModel;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.teacher.model.register.RatingModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.teacher.service.CompletionCommissionService;
import org.edec.teacher.service.CompletionService;
import org.edec.teacher.service.impl.CompletionCommissionImpl;
import org.edec.teacher.service.impl.CompletionServiceImpl;
import org.edec.teacher.service.impl.RegisterServiceImpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.constants.RegisterType;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/teacher")
public class TeacherCtrl {
    private static final Logger log = Logger.getLogger(TeacherCtrl.class.getName());
    private EntityManagerRegister manager = new EntityManagerRegister();

    private CompletionCommissionService commissionService = new CompletionCommissionImpl();
    private CompletionService completionService = new CompletionServiceImpl();

    RegisterServiceImpl registerService = new RegisterServiceImpl();
    LoginRestDAO loginRestDAO = new LoginRestDAO();
    TeacherRestDAO teacherRestDAO = new TeacherRestDAO();


    /**
     * Получение информации по преподавателю по токену
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/get")
    @Consumes("application/json;charset=utf-8")
    public Response getTeacherByLogin(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Teacher teacher = teacherRestDAO.getTeacherInfoByLogin(login);
            if (teacher == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_FORBIDDEN);
            }
            JSONObject teacherJSON = new JSONObject(teacher);
            jsonObject.put("teacher", teacherJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }

    }

    /**
     * Получение доступных семестров для преподавателя
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/semesters")
    @Consumes("application/json;charset=utf-8")
    public Response getTeacherSemesters(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<TeacherSemester> semesters = teacherRestDAO.getTeacherSemesters(login);
            if (semesters == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_FORBIDDEN);
            }
            JSONArray semestersJSON = new JSONArray(semesters);
            jsonObject.put("semesters", semestersJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение дисциплин преподавателя по конкретному семестру
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/subjects")
    @Consumes("application/json;charset=utf-8")
    public Response getTeacherSubjectsInSemester(TeacherSubjectsMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<TeacherSubject> subjects = teacherRestDAO.getTeacherSubjectsBySemester(login, userMsg.getSemester().longValue());
            // Постобработка для исключительных ситуаций (например открытые пустые ведомости при наличии основной)
            List<TeacherSubject> subjectsFiltered = new ArrayList<>();
            for (TeacherSubject subject : subjects) {
                boolean find = false;
                int index = 0;
                for (TeacherSubject teacherSubject : subjectsFiltered) {
                    if (teacherSubject.getIdLGSS().equals(subject.getIdLGSS())
                            && teacherSubject.getIsCP().equals(subject.getIsCP())
                            && teacherSubject.getIsCW().equals(subject.getIsCW())
                            && teacherSubject.getIsExam().equals(subject.getIsExam())
                            && teacherSubject.getIsPass().equals(subject.getIsPass())
                    ) {
                        if (teacherSubject.getCertNumber() == null && subject != null) {
                            // Найдено - заменить
                            subjectsFiltered.set(index, subject);
                            find = true;
                        } else {
                            // Найдено - итак норм
                            find = true;
                        }
                    }
                    index++;
                }
                if (!find) {
                    subjectsFiltered.add(subject);
                }
            }

            // Повторная проходка по сгруппированным дисциплинам для сплита форм контроля
            // Сплитим по формам контроля, проверяем подписи и сертификаты - что относятся к нужной фк
            List<TeacherSubject> subjectsPostFiltered = new ArrayList<>();
            for (TeacherSubject subject : subjectsFiltered) {
                boolean find = false;
                int index = 0;
                for (TeacherSubject teacherSubject : subjectsPostFiltered) {
                    if (teacherSubject.getIdLGSS().equals(subject.getIdLGSS())
                            && (
                                    (teacherSubject.getIsCP().equals(subject.getIsCP()) && teacherSubject.getIsCP().equals(true))
                                    || (teacherSubject.getIsCW().equals(subject.getIsCW()) && teacherSubject.getIsCW().equals(true))
                                    || (teacherSubject.getIsExam().equals(subject.getIsExam()) && teacherSubject.getIsExam().equals(true))
                                    || (teacherSubject.getIsPass().equals(subject.getIsPass()) && teacherSubject.getIsPass().equals(true))
                    )
                    ) {
                        find = true;
                    }
                }
                if (!find) {
                    if (subject.getIsExam()) {
                        try {
                            TeacherSubject ex = subject.clone();
                            if (subject.getFoc() != null && subject.getFoc() != FormOfControlConst.EXAM.getValue()) {
                                ex.setCertNumber(null);
                                ex.setIsSign(-1);
                                ex.setSignDate(null);
                            }
                            ex.setIsExam(true);
                            ex.setFoc(FormOfControlConst.EXAM.getValue());
                            ex.setIsPass(false);
                            ex.setIsCP(false);
                            ex.setIsCW(false);
                            ex.setIsPractic(false);
                            subjectsPostFiltered.add(ex);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (subject.getIsPass()) {
                        try {
                            TeacherSubject ex = subject.clone();
                            if (subject.getFoc() != null && subject.getFoc() != FormOfControlConst.PASS.getValue()) {
                                ex.setCertNumber(null);
                                ex.setIsSign(-1);
                                ex.setSignDate(null);
                            }
                            ex.setIsExam(false);
                            ex.setIsPass(true);
                            ex.setFoc(FormOfControlConst.PASS.getValue());
                            ex.setIsCP(false);
                            ex.setIsCW(false);
                            ex.setIsPractic(false);
                            subjectsPostFiltered.add(ex);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (subject.getIsCP()) {
                        try {
                            TeacherSubject ex = subject.clone();
                            if (subject.getFoc() != null && subject.getFoc() != FormOfControlConst.CP.getValue()) {
                                ex.setCertNumber(null);
                                ex.setIsSign(-1);
                                ex.setSignDate(null);
                            }
                            ex.setIsExam(false);
                            ex.setIsPass(false);
                            ex.setIsCP(true);
                            ex.setFoc(FormOfControlConst.CP.getValue());
                            ex.setIsCW(false);
                            ex.setIsPractic(false);
                            subjectsPostFiltered.add(ex);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (subject.getIsCW()) {
                        try {
                            TeacherSubject ex = subject.clone();
                            if (subject.getFoc() != null && subject.getFoc() != FormOfControlConst.CW.getValue()) {
                                ex.setCertNumber(null);
                                ex.setIsSign(-1);
                                ex.setSignDate(null);
                            }
                            ex.setIsExam(false);
                            ex.setIsPass(false);
                            ex.setIsCP(false);
                            ex.setIsCW(true);
                            ex.setFoc(FormOfControlConst.CW.getValue());
                            ex.setIsPractic(false);
                            subjectsPostFiltered.add(ex);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    if (subject.getIsPractic()) {
                        try {
                            TeacherSubject ex = subject.clone();
                            if (subject.getFoc() != null && subject.getFoc() != FormOfControlConst.PRACTIC.getValue()) {
                                ex.setCertNumber(null);
                                ex.setIsSign(-1);
                                ex.setSignDate(null);
                            }
                            ex.setIsExam(false);
                            ex.setIsPass(false);
                            ex.setIsCP(false);
                            ex.setIsCW(false);
                            ex.setIsPractic(true);
                            ex.setFoc(FormOfControlConst.PRACTIC.getValue());
                            subjectsPostFiltered.add(ex);
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            if (subjectsPostFiltered == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_FORBIDDEN);
            }
            JSONArray subjectsJSON = new JSONArray(subjectsPostFiltered);
            jsonObject.put("subjects", subjectsJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение оценок за конкретную дисциплину
     *
     * @param subjectRatingsMsg
     * @return
     */
    @POST
    @Path("/ratings")
    @Consumes("application/json;charset=utf-8")
    public Response getRatingsByLGSS(SubjectRatingsMsg subjectRatingsMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(subjectRatingsMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Long lgss = subjectRatingsMsg.getIdLGSS();
            FormOfControlConst fc = FormOfControlConst.getName(subjectRatingsMsg.getFoc());
            RegisterType type = RegisterType.getRegisterTypeByRetakeCount(subjectRatingsMsg.getType());
            // TODO: добавить проверку доступа для преподавателя
            List<RatingModel> ratings = manager.getListRatingsBySubjectAndType(lgss, fc, type);
            RegisterModel registerModel = registerService.transformRatingModelsToRegisterModel(ratings, fc);

            JSONObject regJSON = new JSONObject(registerModel);
            jsonObject.put("register", regJSON);
            jsonObject.put("status", "success");
        }
        return prepareResponse(jsonObject.toString());
    }

    @POST
    @Path("/setrating")
    @Consumes("application/json;charset=utf-8")
    public Response getSetRatingByLGSS(SubjectMarkMsg subjectMarkMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(subjectMarkMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            // Сначала проверяем, а не подписана ли ведомость
            // (на случай ошибки на клиенте или умышленной попытке перезаписи)
            if (subjectMarkMsg.getIdSRH() != null && registerService.checkSRHSign(subjectMarkMsg.getIdSRH())) {
                jsonObject.put("msg", "Ведомость уже подписана");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            if (subjectMarkMsg.getIdSRH() == null && registerService.checkExistSRH(subjectMarkMsg.getIdSR(), subjectMarkMsg.getRetakeCount())) {
                jsonObject.put("msg", "Оценка с retake_count " + subjectMarkMsg.getRetakeCount() + " уже существует");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            long idSRH = 0;
            if (subjectMarkMsg.getIdSRH() != null) {
                idSRH = subjectMarkMsg.getIdSRH();
                registerService.updateSRHDateAndRating(idSRH, subjectMarkMsg.getNewRating());
            } else {
                idSRH = registerService.createSRH(
                        subjectMarkMsg.isExam(),
                        subjectMarkMsg.isPass(),
                        subjectMarkMsg.isCp(),
                        subjectMarkMsg.isCw(),
                        subjectMarkMsg.isPractic(),
                        subjectMarkMsg.getPassType(),
                        "0.0.0",
                        subjectMarkMsg.getNewRating(),
                        subjectMarkMsg.getIdSR(),
                        17L,
                        subjectMarkMsg.getRetakeCount() > 0 ? subjectMarkMsg.getRetakeCount() * -1 : subjectMarkMsg.getRetakeCount()
                );
            }

            jsonObject.put("idSRH", idSRH);
            jsonObject.put("status", "success");
        }
        return prepareResponse(jsonObject.toString());
    }


    /**
     * Получение всех пересдач преподавателя
     *
     * @param teacherRetakeMsg
     * @return
     */
    @POST
    @Path("/retakes")
    @Consumes("application/json;charset=utf-8")
    public Response getAllRetakes(TeacherRetakeMsg teacherRetakeMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(teacherRetakeMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Teacher teacher = teacherRestDAO.getTeacherInfoByLogin(login);
            Integer filter = teacherRetakeMsg.getFilter();
            if (filter == null) {
                filter = 0;
            }
            List<RetakeModel> fullRetakeList = completionService.getRetakesForHum(teacher.getIdHum(), filter);

            JSONArray subjectsJSON = new JSONArray(fullRetakeList);
            jsonObject.put("retakes", subjectsJSON);
            jsonObject.put("status", "success");
        }
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Получение всех комиссий преподавателя
     *
     * @param teacherRetakeMsg
     * @return
     */
    @POST
    @Path("/comissions")
    @Consumes("application/json;charset=utf-8")
    public Response getAllComissons(TeacherRetakeMsg teacherRetakeMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(teacherRetakeMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Teacher teacher = teacherRestDAO.getTeacherInfoByLogin(login);
            Integer filter = teacherRetakeMsg.getFilter();
            if (filter == null) {
                filter = 0;
            }
            List<CommissionModel> fullComissonsList = commissionService.getListCommByHum(teacher.getIdHum(), filter);

            JSONArray subjectsJSON = new JSONArray(fullComissonsList);
            jsonObject.put("comissions", subjectsJSON);
            jsonObject.put("status", "success");
        }
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Получение полной информации по комиссии
     *
     * @param teacherRetakeMsg
     * @return
     */
    @POST
    @Path("/comissions/ratings")
    @Consumes("application/json;charset=utf-8")
    public Response getComissonRatings(TeacherRetakeRatingMsg teacherRetakeMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(teacherRetakeMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Teacher teacher = teacherRestDAO.getTeacherInfoByLogin(login);

            CommissionModel commissionModel = commissionService.getCommissionByRegister(teacherRetakeMsg.getIdReg());

            JSONObject subjectsJSON = new JSONObject(commissionModel);
            jsonObject.put("comission", subjectsJSON);
            jsonObject.put("status", "success");
        }
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Секция костылей для CORS
     **/
    @OPTIONS
    @Path("/get")
    public Response getTeacherByLoginOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/semesters")
    public Response getTeacherSemestersOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/subjects")
    public Response getTeacherSubjectsInSemesterOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/ratings")
    public Response getRatingsByLGSSOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/retakes")
    public Response getAllRetakesOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/comissions")
    public Response getAllComissonsOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/comissions/ratings")
    public Response getComissonRatingsOpt() {
        return prepareResponse("");
    }

    /**
     * Конец секции костылей для CORS
     **/

    public Response prepareResponse(String res) {
        return prepareResponse(res, HttpStatus.SC_OK);
    }

    public Response prepareResponse(String res, int status) {
        Response.ResponseBuilder response = Response.status(status);
        response.entity(res);
        response.header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers",
                        "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        return response.build();
    }


}
