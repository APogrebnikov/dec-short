package org.edec.schedule.service.impl;

import bsh.StringUtil;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.edec.schedule.manager.EntityManagerAttendance;
import org.edec.schedule.model.dao.DicDayLesson;
import org.edec.schedule.model.dao.DicTimeLesson;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.schedule.model.dao.GroupSubjectLessonEso;
import org.edec.schedule.model.xls.DayOfWeekClasses;
import org.edec.schedule.model.xls.TimeClasses;
import org.edec.schedule.model.xls.WeekClasses;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.ScheduleParser;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.httpclient.manager.HttpClient;

import java.io.*;
import java.util.ArrayList;;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Log4j
public class AttendanceImpl implements AttendanceService {

    private EntityManagerAttendance emAttendance = new EntityManagerAttendance();

    private ScheduleParser parser;
    private List<DicDayLesson> dicDayLessons;
    private List<DicTimeLesson> dicTimeLessons;

    public AttendanceImpl() {
        dicDayLessons = emAttendance.getDays();
        dicTimeLessons = emAttendance.getTimes();
    }

    public AttendanceImpl(ScheduleParser parser) {
        this();
        this.parser = parser;
    }

    @Override
    public List<GroupSubjectLesson> getLessonsFromDb(String groupname) {
        List<GroupSubjectLessonEso> models = emAttendance.getModelLesson(groupname);
        return convertToGroupSubjectLesson(models);
    }

    @Override
    public List<GroupSubjectLesson> getLessonsFromDb(String groupname, Long idSem) {
        List<GroupSubjectLessonEso> models = emAttendance.getModelLesson(groupname, idSem);
        return convertToGroupSubjectLesson(models);
    }

    @Override
    public List<GroupModel> getGroupByInstAndFormOfStudy(Long idInst, Integer formOfStudy) {
        return emAttendance.getGroupsByInstAndFormOfStudy(idInst, formOfStudy);
    }

    @Override
    public List<SubjectModel> getSubjectsByGroupname(String groupname) {
        return emAttendance.getSubjectsByGroupname(groupname);
    }

    @Override
    public boolean createSchedule(Long idLGSS, Integer week, String room, String teacher, Long idDicDayLesson, Long idDicTimeLesson,
                                  Boolean lesson) {
        return emAttendance.addAttend(idLGSS, week, room, teacher, idDicDayLesson, idDicTimeLesson, lesson);
    }

    @Override
    public boolean deleteScheduleByGroup(String groupname) {
        return emAttendance.removeAllScheduleByGroupname(groupname);
    }

    @Override
    public boolean deleteScheduleById(Long idLSCH) {
        return emAttendance.removeScheduleById(idLSCH);
    }

    @Override
    public void generateForAllGroups(Long idInst, Integer formOfStudy) {
        if (parser == null) {
            throw new IllegalArgumentException("Парсер не был подключен");
        }
        List<GroupModel> groups = getGroupByInstAndFormOfStudy(idInst, formOfStudy);

        for (GroupModel group : groups) {
            deleteScheduleByGroup(group.getGroupname());
            List<SubjectModel> subjects = getSubjectsByGroupname(group.getGroupname());
            List<GroupSubjectLesson> lessonsTimetable = parser.parseScheduleByParams(
                    group.getGroupname(),
                    group.getCourse(),
                    group.getQualification() == 3 ? QualificationConst.MASTER : QualificationConst.BACHELOR);
            if (lessonsTimetable == null) {
                System.out.println(group.getGroupname() + " расписание не нашлось");
                continue;
            }
            for (GroupSubjectLesson lesson : lessonsTimetable) {
                for (SubjectModel subject : subjects) {
                    if (subject.getSubjectname().equals(lesson.getSubjectName())) {
                        lesson.setIdLGSS(subject.getIdLGSS());
                        break;
                    }
                }
                if (lesson.getIdLGSS() != null) {
                    if (!createSchedule(
                            lesson.getIdLGSS(), lesson.getWeek(), lesson.getRoom(), lesson.getTeacher(),
                            lesson.getDicDayLesson().getIdDicDayLesson(), lesson.getDicTimeLesson().getIdDicTimeLesson(), lesson.getLesson()
                    )) {
                        System.out.println("Ну удалось создать расписание: " + group.getGroupname() + ", " + lesson.getSubjectName());
                    }
                }
            }
        }
    }

    private List<GroupSubjectLesson> convertToGroupSubjectLesson(List<GroupSubjectLessonEso> models) {
        return models.stream()
                .map(model -> GroupSubjectLesson.builder()
                        .idLGSS(model.getIdLGSS())
                        .week(model.getWeek())
                        .lesson(model.getLesson())
                        .teacher(model.getTeacher())
                        .room(model.getRoom())
                        .subjectName(model.getSubjectName())
                        .idLSCH(model.getIdLSCH())
                        .dicDayLesson(dicDayLessons.stream()
                                .filter(dayLesson -> StringUtils.equalsIgnoreCase(dayLesson.getName(), model.getDayName()))
                                .findFirst().orElse(null))
                        .dicTimeLesson(dicTimeLessons.stream()
                                .filter(timeLesson -> StringUtils.equalsIgnoreCase(timeLesson.getTimeName(), model.getTimeName()))
                                .findFirst().orElse(null))
                        .build()).collect(Collectors.toList());
    }
}