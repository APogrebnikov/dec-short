package org.edec.schedule.service.xls;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.edec.schedule.manager.EntityManagerAttendance;
import org.edec.schedule.model.dao.DicDayLesson;
import org.edec.schedule.model.dao.DicTimeLesson;
import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.schedule.model.dao.GroupSubjectLessonEso;
import org.edec.schedule.model.xls.DayOfWeekClasses;
import org.edec.schedule.model.xls.TimeClasses;
import org.edec.schedule.model.xls.WeekClasses;
import org.edec.schedule.model.xls.XlsScheduleContainer;
import org.edec.schedule.service.ScheduleParser;
import org.edec.utility.constants.QualificationConst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class XlsScheduleParser implements ScheduleParser {

    private EntityManagerAttendance emAttendance = new EntityManagerAttendance();

    private XlsScheduleParserConfig config;

    private List<DicDayLesson> dicDayLessons;
    private List<DicTimeLesson> dicTimeLessons;

    public XlsScheduleParser(XlsScheduleParserConfig config) {
        this.config = config;
        dicDayLessons = emAttendance.getDays();
        dicTimeLessons = emAttendance.getTimes();
    }

    @Override
    public List<GroupSubjectLesson> parseScheduleByParams(String groupname, Integer course, QualificationConst qualification) {
        groupname = groupname.toLowerCase();

        XlsScheduleContainer xlsContainer = config.getSelectedHolder().schedules().stream()
                .filter(container -> container.qualification().equals(qualification)
                        && container.course().equals(course))
                .findFirst().orElse(null);

        if (xlsContainer == null) {
            throw new IllegalArgumentException("Контейнер не нашелся для курса " + course + " и квалификации " + qualification.getName());
        }

        Sheet sheet = xlsContainer.scheduleData().getSheet("Расписание");

        Cell selectedGroup = findCellBySelectedGroupName(sheet, groupname);

        if (selectedGroup == null) {
            throw new IllegalArgumentException("Группа в расписание не нашлась");
        }

        List<GroupSubjectLesson> groupSubjectLessons = new ArrayList<>();
        for (DayOfWeekClasses dayOfWeekClasses : getDayOfWeekClasses(sheet)) {

            String day = dayOfWeekClasses.getDayOfWeek().getStringCellValue().trim();

            for (TimeClasses timeClasses : dayOfWeekClasses.getTimeClasses()) {
                String time = timeClasses.getTimeClasses().getStringCellValue().trim();
                for (WeekClasses weekClasses : timeClasses.getWeekClasses()) {
                    String week = weekClasses.getWeek().getStringCellValue().trim();
                    String subject = sheet.getRow(weekClasses.getSubjectIndex())
                            .getCell(selectedGroup.getColumnIndex())
                            .getStringCellValue()
                            .trim();
                    String teacher = sheet.getRow(weekClasses.getTeacherIndex())
                            .getCell(selectedGroup.getColumnIndex())
                            .getStringCellValue()
                            .trim();
                    String room = sheet.getRow(weekClasses.getRoomIndex())
                            .getCell(selectedGroup.getColumnIndex())
                            .getStringCellValue()
                            .trim();
                    if (!subject.equals("")) {
                        GroupSubjectLesson groupSubjectLesson = GroupSubjectLesson.builder()
                                .dicDayLesson(getDicDayLessonByName(day, dicDayLessons))
                                .dicTimeLesson(getDicTimeLessonByName(time, dicTimeLessons))
                                .room(room.contains("/") ? room.substring(room.indexOf("/") + 1) : room)
                                .subjectName(subject)
                                .lesson(room.contains("лек"))
                                .teacher(teacher)
                                .week(Integer.valueOf(week))
                                .build();

                        groupSubjectLessons.add(groupSubjectLesson);
                    }
                }
            }
        }
        return groupSubjectLessons;
    }

    private Cell findCellBySelectedGroupName(Sheet sheet, String groupname) {

        //Ищем с 5 по 15 строку
        for (int i = 5; i <= 15; i++) {
            Row rowGroup = sheet.getRow(i);
            Iterator<Cell> iteratorCellGroup = rowGroup.cellIterator();
            while (iteratorCellGroup.hasNext()) {
                Cell cell = iteratorCellGroup.next();
                if (cell.getCellType() == CellType.STRING.getCode() && StringUtils.containsIgnoreCase(cell.getStringCellValue(), groupname)) {
                    return cell;
                }
            }
        }
        return null;
    }

    private DicDayLesson getDicDayLessonByName(String name, List<DicDayLesson> dicDayLessons) {
        return dicDayLessons.stream()
                .filter(dayLesson -> StringUtils.equalsIgnoreCase(dayLesson.getName(), name))
                .findFirst().orElse(null);
    }

    private DicTimeLesson getDicTimeLessonByName(String name, List<DicTimeLesson> dicTimeLessons) {
        return dicTimeLessons.stream()
                .filter(timeLesson -> StringUtils.equalsIgnoreCase(timeLesson.getTimeName(), name))
                .findFirst().orElse(null);
    }

    private List<DayOfWeekClasses> getDayOfWeekClasses(Sheet sheet) {
        Iterator<Row> rowIterator = sheet.rowIterator();

        DayOfWeekClasses mondayClasses = new DayOfWeekClasses(), tuesdayClasses = new DayOfWeekClasses(), wednesdayClasses = new DayOfWeekClasses(), thursdayClasses = new DayOfWeekClasses(), fridayClasses = new DayOfWeekClasses(), saturdayClasses = new DayOfWeekClasses();
        boolean monday = false, tuesday = false, wednesday = false, thursday = false, friday = false, saturday = false;

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cellDayOfWeek = row.getCell(0);

            if (cellDayOfWeek == null) {
                continue;
            }
            String weekDayValue = cellDayOfWeek.getStringCellValue().trim().toLowerCase();

            if (cellDayOfWeek == null) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(weekDayValue, "понедельник")) {
                monday = true;
                mondayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (StringUtils.equalsIgnoreCase(weekDayValue, "вторник")) {
                monday = false;
                tuesday = true;
                tuesdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (StringUtils.equalsIgnoreCase(weekDayValue, "среда")) {
                tuesday = false;
                wednesday = true;
                wednesdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (StringUtils.equalsIgnoreCase(weekDayValue, "четверг")) {
                wednesday = false;
                thursday = true;
                thursdayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (StringUtils.equalsIgnoreCase(weekDayValue, "пятница")) {
                thursday = false;
                friday = true;
                fridayClasses.setDayOfWeek(cellDayOfWeek);
            } else if (StringUtils.equalsIgnoreCase(weekDayValue, "суббота")) {
                friday = false;
                saturday = true;
                saturdayClasses.setDayOfWeek(cellDayOfWeek);
            }

            Cell cellTime = row.getCell(1);
            if (cellTime!=null) {
                if (monday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(mondayClasses, cellTime);
                } else if (tuesday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(tuesdayClasses, cellTime);
                } else if (wednesday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(wednesdayClasses, cellTime);
                } else if (thursday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(thursdayClasses, cellTime);
                } else if (friday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(fridayClasses, cellTime);
                } else if (saturday && !cellTime.getStringCellValue().equals("")) {
                    setTimeForDay(saturdayClasses, cellTime);
                }
            }

            Cell cellWeek = row.getCell(2);
            if (cellWeek!=null) {
                if (monday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(mondayClasses, cellWeek);
                } else if (tuesday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(tuesdayClasses, cellWeek);
                } else if (wednesday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(wednesdayClasses, cellWeek);
                } else if (thursday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(thursdayClasses, cellWeek);
                } else if (friday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(fridayClasses, cellWeek);
                } else if (saturday && !cellWeek.getStringCellValue().equals("")) {
                    setWeekForLastTimeByDay(saturdayClasses, cellWeek);
                }
            }
        }

        return Arrays.asList(
                mondayClasses, tuesdayClasses, wednesdayClasses,
                thursdayClasses, fridayClasses, saturdayClasses
        );
    }

    private void setTimeForDay(DayOfWeekClasses dayOfWeekClasses, Cell cell) {
        TimeClasses time = new TimeClasses();
        time.setTimeClasses(cell);
        dayOfWeekClasses.getTimeClasses().add(time);
    }

    private void setWeekForLastTimeByDay(DayOfWeekClasses dayOfWeekClasses, Cell cell) {
        TimeClasses timeClasses = dayOfWeekClasses.getTimeClasses().get(dayOfWeekClasses.getTimeClasses().size() - 1);
        WeekClasses weekClasses = new WeekClasses();
        weekClasses.setWeek(cell);
        weekClasses.setSubjectIndex(cell.getRowIndex());
        weekClasses.setTeacherIndex(cell.getRowIndex() + 1);
        weekClasses.setRoomIndex(cell.getRowIndex() + 2);
        timeClasses.getWeekClasses().add(weekClasses);
    }

    public void setConfig(XlsScheduleParserConfig config) {
        this.config = config;
    }
}
