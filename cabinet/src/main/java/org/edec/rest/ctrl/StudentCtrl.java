package org.edec.rest.ctrl;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.edec.model.GroupSemesterModel;
import org.edec.rest.manager.FeedBackDAO;
import org.edec.rest.manager.LoginRestDAO;
import org.edec.rest.manager.StudentRestDAO;
import org.edec.rest.model.UserMsg;
import org.edec.rest.model.student.Efficiency;
import org.edec.rest.model.student.Progress;
import org.edec.rest.model.student.ProgressDetail;
import org.edec.rest.model.student.ProgressStat;
import org.edec.rest.model.student.request.*;
import org.edec.rest.model.student.response.*;
import org.edec.student.journalOfAttendance.service.JournalOfAttendanceService;
import org.edec.student.journalOfAttendance.service.impl.JournalOfAttendanceServiceImpl;
import org.edec.student.questionnaire.model.QuestionnaireModel;
import org.edec.student.questionnaire.service.QuestionnaireEnsembleService;
import org.edec.student.questionnaire.service.impl.QuestionnaireEnsembleImpl;
import org.edec.student.schedule.manager.ScheduleStudentManager;
import org.edec.utility.httpclient.manager.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Path("/student")
public class StudentCtrl {
    private static final Logger log = Logger.getLogger(StudentCtrl.class.getName());
    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();
    LoginRestDAO loginRestDAO = new LoginRestDAO();
    FeedBackDAO feedBackDAO = new FeedBackDAO();
    StudentRestDAO studentRestDAO = new StudentRestDAO();
    @javax.ws.rs.core.Context
    ServletContext context;

    /**
     * Получение базовой информации по пользователю
     *
     * @param userMsg
     * @return {msg:"", status:"", usertoken:""}
     */
    @POST
    @Path("/get")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentByLogin(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            if (student == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONObject studentJSON = new JSONObject(student);
            jsonObject.put("student", studentJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }

    }

    @GET
    @Path("/get")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentByLogin(@RequestHeader HttpHeaders headers) {
        JSONObject jsonObject = new JSONObject();
        String token = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0);
        String login = loginRestDAO.checkUserToken(token);
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            if (student == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONObject studentJSON = new JSONObject(student);
            jsonObject.put("student", studentJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }

    }

    /**
     * Получение одногруппников для текущего пользователя
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/classmates")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentClassmatesByLogin(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Student> students = studentRestDAO.getStudentClassmates(login);
            if (students == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray studentJSON = new JSONArray(students);
            jsonObject.put("students", studentJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение учебного плана для текущего пользователя
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/curriculum")
    @Consumes("application/json;charset=utf-8")
    public Response getCurriculum(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();
        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            try {
                String url = "http://localhost:8081/sync/curriculum";
                JSONObject jsonOtherIdStudentcard = new JSONObject();
                jsonOtherIdStudentcard.put("otherIdStudentcard", studentRestDAO.getOtherIdStudentcardByToken(userMsg.getUserToken()));

                String curriculum = HttpClient.makeHttpRequest(url, HttpClient.POST, new ArrayList<>(), jsonOtherIdStudentcard.toString());

                jsonObject.put("curriculum", new JSONObject(curriculum));
                jsonObject.put("status", "success");
            } catch (Exception e) {
                jsonObject.put("status", "error");
            }
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение оценок за конкретный семестр для текущего пользователя
     *
     * @param ratingMsg
     * @return
     */
    @POST
    @Path("/rating")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentProgressBySemester(RatingMsg ratingMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(ratingMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Rating> ratings = studentRestDAO.getProgressBySemester(login, ratingMsg.getSemester().longValue());
            if (ratings == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray ratingsJSON = new JSONArray(ratings);
            jsonObject.put("ratings", ratingsJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение min max avg в рамках группы
     *
     * @param ratingMsg
     * @return
     */
    @POST
    @Path("/progress")
    @Consumes("application/json;charset=utf-8")
    public Response getGroupProgressBySemester(RatingMsg ratingMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(ratingMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Progress> progressList = studentRestDAO.getGroupProgress(ratingMsg.getSemester().longValue());
            if (progressList == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            List<ProgressStat> progressStatList = new ArrayList<>();
            for (Progress progress : progressList) {
                boolean find = false;
                for (ProgressStat progressStat : progressStatList) {
                    if (progress.getIdLGSS().equals(progressStat.getIdLGSS())) {
                        find = true;
                        if (progress.getEsogradecurrent() > progressStat.getMax()) {
                            progressStat.setMax(progress.getEsogradecurrent());
                        }
                        if (progress.getEsogradecurrent() < progressStat.getMin()) {
                            progressStat.setMin(progress.getEsogradecurrent());
                        }
                        progressStat.getList().add(progress);
                    }
                }
                if (!find) {
                    ProgressStat progressStat = new ProgressStat();
                    progressStat.setIdLGSS(progress.getIdLGSS());
                    progressStat.setSubjectName(progress.getSubjectName());
                    progressStat.setMax(progress.getEsogradecurrent());
                    progressStat.setMin(progress.getEsogradecurrent());
                    progressStat.getList().add(progress);
                    progressStatList.add(progressStat);
                }
            }
            // Расчет среднего прогресса
            for (ProgressStat progressStat : progressStatList) {
                Long total = 0L;
                for (Progress progress : progressStat.getList()) {
                    if (progress.getLogin().equalsIgnoreCase(login)) {
                        progressStat.setCurrent(progress.getEsogradecurrent());
                    }
                    total += progress.getEsogradecurrent();
                }
                progressStat.setAvg(total / progressStat.getList().size());
            }

            if (progressStatList == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray progressJSON = new JSONArray(progressStatList);
            jsonObject.put("progress", progressJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    @POST
    @Path("/progressdetails")
    @Consumes("application/json;charset=utf-8")
    public Response getGroupProgressDetailsBySemester(RatingMsg ratingMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(ratingMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<ProgressDetail> progressList = studentRestDAO.getGroupProgressDetails(ratingMsg.getSemester().longValue());
            if (progressList == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }

            List<JSONObject> progressSub = new ArrayList<>();
            for (ProgressDetail progressDetail : progressList) {
                boolean find = false;
                for (JSONObject sub : progressSub) {
                    if (progressDetail.getSubjectName().equals(sub.get("subjectName").toString())) {
                        JSONArray weeks = sub.getJSONArray("weeks");
                        JSONObject detail = new JSONObject();
                        detail.put("min", progressDetail.getMin());
                        detail.put("avg", progressDetail.getAvg());
                        detail.put("max", progressDetail.getMax());
                        detail.put("week", progressDetail.getWeek());
                        detail.put("sync", progressDetail.getDateSync());
                        weeks.put(detail);
                        sub.put("weeks", weeks);
                        find = true;
                    }
                }
                if (!find) {
                    JSONObject sub = new JSONObject();
                    sub.put("subjectName", progressDetail.getSubjectName());
                    sub.put("idLGSS", progressDetail.getIdLGSS());
                    JSONArray weeks = new JSONArray();
                    JSONObject detail = new JSONObject();
                    detail.put("min", progressDetail.getMin());
                    detail.put("avg", progressDetail.getAvg());
                    detail.put("max", progressDetail.getMax());
                    detail.put("week", progressDetail.getWeek());
                    detail.put("sync", progressDetail.getDateSync());
                    weeks.put(detail);
                    sub.put("weeks", weeks);
                    progressSub.add(sub);
                }
            }

            JSONArray progressJSON = new JSONArray(progressSub);
            jsonObject.put("progress", progressJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение среднего балла за сессию с учетом даты
     *
     * @param ratingMsg
     * @return
     */
    @POST
    @Path("/avgrating")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentAVGProgressOnSemester(RatingMsg ratingMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(ratingMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Rating> ratings = studentRestDAO.getAVGProgressOnSemester(login, ratingMsg.getSemester().longValue());
            if (ratings == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }

            // Формируем студента для отдельного расчета
            List<List<Rating>> students = new ArrayList<>();
            for (Rating rating : ratings) {
                boolean find = false;
                for (List<Rating> student : students) {
                    if (student.size() > 0 && student.get(0).getLogin().equals(rating.getLogin())) {
                        student.add(rating);
                        find = true;
                    }
                }
                if (!find) {
                    List<Rating> newstudent = new ArrayList<>();
                    newstudent.add(rating);
                    students.add(newstudent);
                }
            }

            Map<String, Double> map = new HashMap<>();
            for (List<Rating> student : students) {
                // Подсчет среднего балла для каждого набора оценок
                map.put(student.get(0).getLogin(), studentRestDAO.calcAVGRating(student));
            }

            //double res = studentRestDAO.calcAVGRating(ratings);
            // Сортировка результатов для построения рейтинга
            List<Map.Entry<String, Double>> sortedRatings = new ArrayList(map.entrySet());
            Collections.sort(sortedRatings, Comparator.comparingDouble((Map.Entry<String, Double> a) -> a.getValue()).reversed());

            // каунтер
            int i = 0;
            // позиция рейтинга
            int j = 0;
            double prev = 0;
            for (Map.Entry<String, Double> sortedRating : sortedRatings) {
                i++;
                if (!sortedRating.getValue().equals(prev)) {
                    j = i;
                    prev = sortedRating.getValue();
                }
                // System.out.println(j+" - "+sortedRating.getValue()+" "+sortedRating.getKey());
                if (sortedRating.getKey().equalsIgnoreCase(login)) {
                    jsonObject.put("result", String.format("%.2f", sortedRating.getValue()));
                    jsonObject.put("pos", j);
                    jsonObject.put("total", sortedRatings.size());
                }
            }
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение доступных семестров для текущего пользователя
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/semesters")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentSemesters(UserMsg userMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<StudentSemester> studentSemesters = studentRestDAO.getStudentSemesters(login);
            if (studentSemesters == null) {
                jsonObject.append("msg", "User not from IKIT");
                jsonObject.append("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray semestersJSON = new JSONArray(studentSemesters);
            jsonObject.put("studentSemesters", semestersJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение расписания для группы студента на конкретный день
     *
     * @param sheduleMsg
     * @return
     */
    @POST
    @Path("/shedule")
    @Consumes("application/json;charset=utf-8")
    public Response getSheduleForGroup(SheduleMsg sheduleMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(sheduleMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            ScheduleStudentManager smg = new ScheduleStudentManager();
            Integer week = smg.getCurrentWeekAlt(sheduleMsg.getDate());
            //Integer week = smg.getCurrentWeekByUser(login, sheduleMsg.getDate());
            List<Shedule> listOfSubject = studentRestDAO.getSheduleForGroupByDate(login, week, sheduleMsg.getDate());
            if (listOfSubject == null) {
                jsonObject.append("msg", "User not from IKIT");
                jsonObject.append("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray subjectJSON = new JSONArray(listOfSubject);
            jsonObject.put("subjects", subjectJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение текущего номера недели
     *
     * @param dateMsg
     * @return
     */
    @POST
    @Path("/week")
    @Consumes("application/json;charset=utf-8")
    public Response getCurrentWeek(DateMsg dateMsg) {
        JSONObject jsonObject = new JSONObject();
        ScheduleStudentManager smg = new ScheduleStudentManager();
        int fos = 1;
        if (dateMsg.getFos() != null) {
            fos = dateMsg.getFos();
        }
        Integer week = smg.getCurrentWeekAlt(dateMsg.getDate());
        Integer number = smg.getCurrentWeekNumberFos(fos, dateMsg.getDate());
        if (number.equals(-1)) {
            number = 18;
        }
        jsonObject.put("week", week);
        jsonObject.put("number", number);
        jsonObject.put("status", "success");
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Исправление посещаемости по конкретному занятию
     *
     * @param attendanceMsg
     * @return
     */
    @POST
    @Path("/attendance/edit")
    @Consumes("application/json;charset=utf-8")
    public Response editAttendanceForSubject(AttendanceResultMsg attendanceMsg) {
        JSONObject jsonObject = new JSONObject();
        boolean success = false;

        String login = loginRestDAO.checkUserToken(attendanceMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            for (Attendance student : attendanceMsg.getStudents()) {
                // Проверяем принудительно, нет ли еще записей за текущую дату
                Long idAttend = student.getIdAttendance();
                if (idAttend == null) {
                    idAttend = studentRestDAO.getDayAttendanceId(attendanceMsg, student);
                }

                if (idAttend != null) {
                    //UPDATE
                    student.setIdAttendance(idAttend);
                    success = studentRestDAO.updateAttendance(attendanceMsg, student);
                } else {
                    //INSERT
                    success = studentRestDAO.insertAttendance(attendanceMsg, student);
                }
            }
        }
        if (success) {
            jsonObject.put("status", "success");
        } else {
            jsonObject.put("msg", "Not update or insert");
            jsonObject.put("status", "error");
        }
        return prepareResponse(jsonObject.toString());
    }

    /**
     * Получение расписания по конкретному занятию
     *
     * @param attendanceMsgMsg
     * @return
     */
    @POST
    @Path("/attendance/subject")
    @Consumes("application/json;charset=utf-8")
    public Response getAttendanceForSubject(AttendanceMsg attendanceMsgMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(attendanceMsgMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Attendance> listOfAttendance = studentRestDAO.getAttendForGroupByDate(login, attendanceMsgMsg.getIdLGSS(), attendanceMsgMsg.getPos(), attendanceMsgMsg.getDate());
            if (listOfAttendance == null) {
                jsonObject.append("msg", "User not from IKIT");
                jsonObject.append("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray attendanceJSON = new JSONArray(listOfAttendance);
            jsonObject.put("attendance", attendanceJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получения полного объекта расписания за день
     *
     * @param sheduleMsg
     * @return
     */
    @POST
    @Path("/attendance/day")
    @Consumes("application/json;charset=utf-8")
    public Response getAttendanceForDay(SheduleMsg sheduleMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(sheduleMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            ScheduleStudentManager smg = new ScheduleStudentManager();
            Integer week = smg.getCurrentWeekAlt(sheduleMsg.getDate());
            List<Shedule> listOfSubject = studentRestDAO.getSheduleForGroupByDate(login, week, sheduleMsg.getDate());
            JSONArray subjectsJSON = new JSONArray();
            if (listOfSubject == null) {
                jsonObject.append("msg", "User not from IKIT");
                jsonObject.append("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            for (Shedule shedule : listOfSubject) {
                JSONObject shObj = new JSONObject();
                shObj.put("subjectname", shedule.getSubjectName());
                shObj.put("idLGSS", shedule.getIdLGSS());
                shObj.put("pos", shedule.getPos());
                shObj.put("lesson", shedule.getLesson());
                List<Attendance> listOfAttendance = studentRestDAO.getAttendForGroupByDate(login, shedule.getIdLGSS(), shedule.getPos(), sheduleMsg.getDate());
                JSONArray attendanceJSON = new JSONArray(listOfAttendance);
                shObj.put("students", attendanceJSON);
                subjectsJSON.put(shObj);
            }
            jsonObject.put("subjects", subjectsJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Получение информации по опросникам у группы
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/questionary")
    @Consumes("application/json;charset=utf-8")
    public Response getQuestionaryInfo(UserMsg userMsg) {
        QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl(context);

        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Student> students = studentRestDAO.getStudentClassmates(login);
            if (students == null) {
                jsonObject.put("msg", "User not from IKIT");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            JSONArray studentsJSON = new JSONArray();
            for (Student student : students) {
                JSONObject stObj = new JSONObject();
                stObj.put("surname", student.getSurname());
                stObj.put("name", student.getName());
                stObj.put("patronymic", student.getPatronymic());
                stObj.put("idHumanface", student.getIdHumanface());
                List<QuestionnaireModel> questionnaireModels = questionnaireEnsembleService.getAllQuestionnairesByIdHum(student.getIdHumanface());
                JSONArray questionnaireJSON = new JSONArray(questionnaireModels);
                stObj.put("questionnaire", questionnaireJSON);
                studentsJSON.put(stObj);
            }
            jsonObject.put("students", studentsJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }

    }

    /**
     * Список опросников для пользователя
     *
     * @param userMsg
     * @return
     */
    @POST
    @Path("/questionnaire/list")
    @Consumes("application/json;charset=utf-8")
    public Response getQuestionaryList(UserMsg userMsg) {
        QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl(context);

        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(userMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            JSONObject stObj = new JSONObject();
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            stObj.put("idHumanface", student.getIdHumanface());
            List<QuestionnaireModel> questionnaireModels = questionnaireEnsembleService.getAllQuestionnairesByIdHum(student.getIdHumanface());
            JSONArray questionnaireJSON = new JSONArray(questionnaireModels);
            stObj.put("questionnaire", questionnaireJSON);
            stObj.put("status", "success");
            return prepareResponse(stObj.toString());
        }
    }

    /**
     * Получение конкретного опросника
     *
     * @return
     */
    @POST
    @Path("/questionnaire/one")
    @Consumes("application/json;charset=utf-8")
    public Response getQuestionaryById(QuestionnaireMsg questionnaireMsg) {
        QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl(context);

        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(questionnaireMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            JSONObject stObj = new JSONObject();
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            stObj.put("idHumanface", student.getIdHumanface());
            QuestionnaireModel questionnaireModel = questionnaireEnsembleService.getQuestionnaire(questionnaireMsg.getId());
            stObj.put("questionnaire", new JSONObject(questionnaireModel));
            stObj.put("status", "success");
            return prepareResponse(stObj.toString());
        }
    }

    /**
     * Отправка всех ответов по конкретному опросу
     *
     * @return
     */
    @POST
    @Path("/questionnaire/sendresult")
    @Consumes("application/json;charset=utf-8")
    public Response sendAllQuestionaryResults(FinishQuestionnaireMsg finishQuestionnaireMsg) {
        QuestionnaireEnsembleService questionnaireEnsembleService = new QuestionnaireEnsembleImpl(context);

        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(finishQuestionnaireMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            JSONObject stObj = new JSONObject();
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            Boolean sendFlag = questionnaireEnsembleService.sendHumAnswer(student.getIdHumanface(), finishQuestionnaireMsg.getId(), finishQuestionnaireMsg.getAnswers());
            if (sendFlag) {
                stObj.put("msg", "answers sended");
                stObj.put("status", "success");
                return prepareResponse(stObj.toString());
            } else {
                jsonObject.put("msg", "Error on send");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
        }
    }

    /**
     * Тестовый прямой запрос к электронным курсам
     *
     * @param eokMsg
     * @return
     */
    @POST
    @Path("/eokrating")
    @Consumes("application/json;charset=utf-8")
    public Response getStudentEokRating(EokMsg eokMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(eokMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            Student student = studentRestDAO.getStudentInfoByLogin(login);
            Long idEokCourse = studentRestDAO.getEokIdSubject(eokMsg.getIdLGSS());

            if (student.getOtherEsoid() == null || idEokCourse == null) {
                jsonObject.put("msg", "missing eso id's");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
            EsoService esoService = new EsoService();
            esoService.generateToken();
            JSONArray gradesJSON = esoService.getJSONGradesForStudent(student.getOtherEsoid(), idEokCourse);
            jsonObject.put("grades", gradesJSON);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * Тестовый запрос для исследования
     */
    @POST
    @Path("/research/efficiency")
    @Consumes("application/json;charset=utf-8")
    public Response getHistoryEfficiency(EfficiencyMsg efficiencyMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(efficiencyMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            List<Efficiency> efficiencies = studentRestDAO.getHistoryEfficiency(efficiencyMsg.getIdLGSS(), efficiencyMsg.getWeeks());
            List<Efficiency> selfEfficiencies = studentRestDAO.getCurrentEfficiency(login, efficiencyMsg.getIdLGSS(), efficiencyMsg.getWeeks());
            if (selfEfficiencies == null) {
                jsonObject.put("msg", "Missing data");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }

            JSONObject result = new JSONObject();
            // Прошлый год
            List<Long> X = null;
            int weekn = 0;
            boolean finishY = false;
            List<Integer> weeksX = new ArrayList<>();
            // Результат прошлого года
            List<Double> Y = new ArrayList<>();
            List<Efficiency> objY = new ArrayList<>();

            // Текущие оценки
            List<Long> Z = null;
            int weeknZ = 0;
            List<Integer> weeksZ = new ArrayList<>();

            if (efficiencies != null) {
                // Сначала организуем подсчет Y, для корректной размерности - ориентируемся на первую неделю
                for (Efficiency efficiency : efficiencies) {
                    result.put("isExam", efficiency.getExam());
                    if (efficiency.getWeek() != weekn) {
                        if (X != null) {
                            result.put("X" + weekn, X);
                            weeksX.add(weekn);
                        }
                        if (weekn != 0) {
                            finishY = true;
                        }
                        weekn = efficiency.getWeek();
                        X = new ArrayList<>();
                        if (finishY) {
                            // Предзаполнение нужного размера
                            for (int i = 0; i < objY.size(); i++) {
                                X.add(0L);
                                objY.get(i).isFind = false;
                            }
                        /*
                        for (Efficiency obj : objY) {
                            obj.isFind = false;
                        }
                        */
                        }
                    }
                    if (!finishY) {
                        X.add(efficiency.getProgress());
                    } else {
                        // Здесь добавляется дополнительная проверка, чтобы свериться с порядком Y
                        for (int i = 0; i < objY.size(); i++) {
                            if (efficiency.getIdSSS().equals(objY.get(i).getIdSSS()) && !objY.get(i).isFind) {
                                X.set(i, efficiency.getProgress());
                                objY.get(i).isFind = true;
                                break;
                            }
                        }
                    /*
                    for (Efficiency obj : objY) {
                        if (efficiency.getIdSSS().equals(obj.getIdSSS()) && !obj.isFind) {
                            X.add(efficiency.getProgress());
                            obj.isFind = true;
                            break;
                        }
                    }
                    */
                    }

                    if (!finishY) {
                        // Для зачета
                        // y = 2 * зачет + (1 - пересдача / 2)
                        // Для экзамена
                        // оценка + (1- кол-во пересдач / 2)
                        Double y;
                        int rating = 0;
                        if (efficiency.getRating() > 0) {
                            rating = efficiency.getRating();
                        }
                        if (efficiency.getPass() && (efficiency.getRating() < 2)) {
                            y = 2.0 * rating + (1 - (efficiency.getRetake() - 1) / 2);
                        } else {
                            y = rating * 1.0 + (1 - (efficiency.getRetake() - 1) / 2);
                        }
                        objY.add(efficiency);
                        Y.add(y);
                    }
                }
                result.put("X" + weekn + "", X);
                weeksX.add(weekn);
                result.put("Y", Y);
            } else {
                int weeknX = 0;
                for (Efficiency selfEff : selfEfficiencies) {
                    result.put("isExam", selfEff.getExam());
                    X = new ArrayList<>();
                    X.add(0L);
                    weeknX = selfEff.getWeek();
                    result.put("X" + weeknX, X);
                    weeksX.add(weeknX);
                }
                Y.add(Double.valueOf(0));
                result.put("Y", Y);
            }

            for (Efficiency selfEff : selfEfficiencies) {
                Z = new ArrayList<>();
                Z.add(selfEff.getProgress());
                weeknZ = selfEff.getWeek();
                result.put("Z" + weeknZ, Z);
                weeksZ.add(weeknZ);
            }

            // Сравнение размеров Z и X - вычеркиваем битые
            result.put("missing", false);
            boolean find;
            for (Integer x : weeksX) {
                find = false;
                for (Integer z : weeksZ) {
                    if (x.equals(z)) {
                        find = true;
                        continue;
                    }
                }
                if (!find) {
                    // Удаляем если есть в X, но нет в Z
                    result.remove("X" + x);
                    result.put("missing", true);
                }
            }

            for (Integer z : weeksZ) {
                find = false;
                for (Integer x : weeksX) {
                    if (z.equals(x)) {
                        find = true;
                        continue;
                    }
                }
                if (!find) {
                    // Удаляем если есть в Z, но нет в X
                    result.remove("Z" + z);
                    result.put("missing", true);
                }
            }

            // Вызов питон скрипта передав набор x, z и y
            try {
                JSONObject jsonPyResult = new JSONObject(HttpClient.makeHttpRequest("http://localhost:8087", HttpClient.POST,
                        new ArrayList<>(), result.toString()
                ));
                //System.out.println(jsonPyResult);
                result = jsonPyResult;
            } catch (Exception ex) {
                System.out.println("Missing Python connection");
            }
/*result
            try {
                String json = result.toString();
                ProcessBuilder pb = new ProcessBuilder("python", "C://temp//python//calcv2.py", "" + json + "");
                Process p = pb.start();

                BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                System.out.println(".........start   process.........");
                String line = "";
                while ((line = bfr.readLine()) != null) {
                    System.out.println("Python Output: " + line);
                }
                String errline = "";
                while ((errline = stdError.readLine()) != null) {
                    System.out.println(errline);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
 */

            /*
            JSONArray efficienciesJSON = new JSONArray(efficiencies);
            jsonObject.put("efficiencies", efficienciesJSON);
            jsonObject.put("status", "success");
             */
            jsonObject.put("result", result);
            jsonObject.put("status", "success");
            return prepareResponse(jsonObject.toString());
        }
    }

    /**
     * {
     * "text": "Text text",
     * "userId": 216013,
     * "os": "ios",
     * "osVersion": "16.4",
     * "appVersion": "1.0.0 (1)",
     * "data": null,
     * "userToken": "demouser3"
     * }
     *
     * @return
     */
    @POST
    @Path("/appReport")
    @Consumes("application/json;charset=utf-8")
    public Response appReport(ReportMsg reportMsg) {
        JSONObject jsonObject = new JSONObject();

        String login = loginRestDAO.checkUserToken(reportMsg.getUserToken());
        if (login == "" || login == null) {
            jsonObject.put("msg", "Wrong token");
            jsonObject.put("status", "error");
            return prepareResponse(jsonObject.toString(), HttpStatus.SC_UNAUTHORIZED);
        } else {
            if (feedBackDAO.insertFeedback(reportMsg, login)) {
                jsonObject.put("result", "OK");
                jsonObject.put("status", "success");
                return prepareResponse(jsonObject.toString());
            } else {
                jsonObject.put("msg", "Can't insert");
                jsonObject.put("status", "error");
                return prepareResponse(jsonObject.toString());
            }
        }
    }


    /**
     * Секция костылей для CORS
     **/
    @OPTIONS
    @Path("/get")
    public Response getOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/classmates")
    public Response classmatesOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/rating")
    public Response ratingOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/semesters")
    public Response semestersOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/avgrating")
    public Response avgratingOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/shedule")
    public Response sheduleOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/attendance/day")
    public Response attendanceDayOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/attendance/subject")
    public Response attendanceSubjectOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/questionary")
    public Response QuestionaryInfoOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/questionnaire/list")
    public Response getQuestionaryListOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/questionnaire/one")
    public Response getQuestionaryByIdOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/questionnaire/sendresult")
    public Response sendAllQuestionaryResultsOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/eokrating")
    public Response getStudentEokRating() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/research/efficiency")
    public Response getHistoryEfficiencyOpt() {
        return prepareResponse("");
    }

    @OPTIONS
    @Path("/appReport")
    public Response appReportOpt() {
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
        response.header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization").header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        return response.build();
    }
}
