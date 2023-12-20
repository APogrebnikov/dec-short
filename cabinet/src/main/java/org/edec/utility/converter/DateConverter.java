package org.edec.utility.converter;

import org.edec.utility.constants.MonthConst;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by dmmax
 */
public class DateConverter {
    public static String convertTimestampToSQLStringFormate(Date date) {
        return convertDateToStringByFormat(date, "yyyy-MM-dd HH:mm");
    }

    public static String convertDateToSQLStringFormat(Date date) {
        return convertDateToStringByFormat(date, "yyyyMMdd");
    }

    public static String convertTimeToString(Date date) {
        return convertDateToStringByFormat(date, "HH:mm");
    }

    public static String convertDateToString(Date date) {
        if (date == null) {
            return "";
        }

        return convertDateToStringByFormat(date, "dd.MM.yyyy");
    }

    public static String convertTimestampToString(Date date) {
        return convertDateToStringByFormat(date, "dd.MM.yyyy HH:mm");
    }

    public static String convertDateToDayString(Date date) {
        return convertDateToStringByFormat(date, "dd");
    }

    public static String convertDateToYearString(Date date) {
        return convertDateToStringByFormat(date, "yyyy");
    }

    public static String convertDateToString(Date date, String emptyMessage) {
        return convertDateToStringByFormatWithEmptyMessage(date, "dd.MM.yyyy", emptyMessage);
    }

    public static String convertDateToStringByFormat(Date date, String format) {
        return convertDateToStringByFormatWithEmptyMessage(date, format, "");
    }

    public static String getShortDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "ПН";
            case Calendar.TUESDAY:
                return "ВТ";
            case Calendar.WEDNESDAY:
                return "СР";
            case Calendar.THURSDAY:
                return "ЧТ";
            case Calendar.FRIDAY:
                return "ПТ";
            case Calendar.SATURDAY:
                return "СБ";
            case Calendar.SUNDAY:
                return "ВС";
            default:
                return "";
        }
    }

    public static String convertDateToStringByFormatWithEmptyMessage(Date date, String format, String emptyMessage) {
        if (date == null || format == null || format.equals("")) {
            return emptyMessage;
        } else {
            return new SimpleDateFormat(format).format(date);
        }
    }

    public static String convert2dateToString(Date date1, Date date2) {
        Calendar calendarBegin = Calendar.getInstance();
        calendarBegin.setTime(date1);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(date2);
        return calendarBegin.get(Calendar.YEAR) + " - " + calendarEnd.get(Calendar.YEAR);
    }

    public static Date convertStringToDate(String date, String format) {
        try {
            return (date == null || date.equals("")) ? null : new SimpleDateFormat(format).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getFirstDateOfMonthByCalendar(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.get(Calendar.DAY_OF_WEEK);
        return calendar.getTime();
    }

    public static Date getLastDateOfMonthByCalendar(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, 1);
        calendar.get(Calendar.DATE);
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextMonthByCalendar(Date currentDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.get(Calendar.MONTH);
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextYear(Date currentDate) {
        if (currentDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
        calendar.get(Calendar.YEAR);
        calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
        calendar.get(Calendar.MONTH);
        return calendar.getTime();
    }

    public static Date getLastDayOfMonth(Date currentDate) {
        if (currentDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.get(Calendar.MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.get(Calendar.DAY_OF_MONTH);
        return calendar.getTime();
    }

    public static Date getFirstDayOfMonth(Date currentDate) {
        if (currentDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.get(Calendar.MONTH);
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextMonth(Date currentDate) {
        if (currentDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, 1);
        calendar.get(Calendar.MONTH);
        return calendar.getTime();
    }

    public static Date getNextDay(Date currentDate) {
        if (currentDate == null) {
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 1);
        calendar.get(Calendar.DATE);
        return calendar.getTime();
    }

    public static Date addMonthsToDate(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        calendar.get(Calendar.MONTH);
        return calendar.getTime();
    }

    public static Date addYearsToDate(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, years);
        calendar.get(Calendar.YEAR);
        return calendar.getTime();
    }

    public static Date getEndOfStudyForStudentByPeriodOfSudy(Date dateOfStartSchoolyear, Double periodOfStudy, int semester) {
        if (dateOfStartSchoolyear == null || periodOfStudy == null) {
            return null;
        }

        if (periodOfStudy <= 0 || semester <= 0) {
            return null;
        }

        int semestersOfStudy;
        boolean isFloatPeriod = false;
        // если период обучения дробный
        if ((double) periodOfStudy.intValue() != periodOfStudy) {
            // округляем в большую сторону
            semestersOfStudy = periodOfStudy.intValue() * 2 + 2;
            isFloatPeriod = true;
        } else {
            semestersOfStudy = periodOfStudy.intValue() * 2;
        }

        if (isFloatPeriod) {
            // не будет работать если у КБ поменяют дату окончания обучения(сейчас это 31.01.......)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateOfStartSchoolyear);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + (semestersOfStudy + 2 - semester) / 2);
            calendar.get(Calendar.YEAR);
            calendar.set(Calendar.MONTH, calendar.getActualMinimum(Calendar.MONTH));
            calendar.get(Calendar.MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.get(Calendar.DAY_OF_MONTH);
            return calendar.getTime();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateOfStartSchoolyear);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + (semestersOfStudy + 2 - semester) / 2);
            calendar.get(Calendar.YEAR);
            calendar.set(Calendar.MONTH, 7);
            calendar.get(Calendar.MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            calendar.get(Calendar.DAY_OF_MONTH);
            return calendar.getTime();
        }
    }

    public static String getMonthByDate(Date date) {
        switch (date.getMonth()) {
            case 0:
                return "Январь";
            case 1:
                return "Февраль";
            case 2:
                return "Март";
            case 3:
                return "Апрель";
            case 4:
                return "Май";
            case 5:
                return "Июнь";
            case 6:
                return "Июль";
            case 7:
                return "Август";
            case 8:
                return "Сентябрь";
            case 9:
                return "Октябрь";
            case 10:
                return "Ноябрь";
            case 11:
                return "Декабрь";
            default:
                return "Не удалось определить месяц";
        }
    }

    public static Date changeDateByMonth(Date date, int fieldOfDate, int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(fieldOfDate, amount);
        return calendar.getTime();
    }

    /**
     * @param dateBegin - дата начала
     * @param dateEnd - дата окончания
     *
     * @return все даты, удовлетворяющие интервалку [Дата начала; Дата окончания]
     */
    public static List<Date> getDateRangeByTwoDates(Date dateBegin, Date dateEnd) {
        return getDateRangeByTwoDates(dateBegin, dateEnd, true);
    }

    public static Date getMondayOfWeekByDate(Date date) {
        return getDayOfWeekByAnotherDate(date, Calendar.MONDAY);
    }

    public static Date getSundayOfWeekByDate(Date date) {
        return getDayOfWeekByAnotherDate(date, Calendar.SUNDAY);
    }

    public static Date getDayOfWeekByAnotherDate(Date date, int dayOfWeek) {
        if (date == null) {
            throw new IllegalArgumentException("Дата не может быть пустой");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return calendar.getTime();
    }

    /**
     * @param dateBegin - дата начала
     * @param dateEnd - дата окончания
     * @param includes - включать первый и последний день
     *
     * @return все даты, удовлетворяющие интервалку [Дата начала; Дата окончания]
     * Если inclusive = false, то не будут включаться первый и последний день (Дата начала; Дата окончания)
     */
    public static List<Date> getDateRangeByTwoDates(Date dateBegin, Date dateEnd, boolean includes) {
        if (dateBegin == null || dateEnd == null || dateBegin.after(dateEnd)) {
            throw new IllegalArgumentException("Даты не могут быть пустыми и дата начала должна быть меньше даты окончания");
        }
        List<Date> dateRanges = new ArrayList<>();

        //Создаем календари без учета часов и минут. Только дата
        Calendar calendarBegin = Calendar.getInstance();
        calendarBegin.setTime(dateBegin);
        calendarBegin.set(Calendar.HOUR_OF_DAY, 0);
        calendarBegin.set(Calendar.MINUTE, 0);
        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(dateEnd);
        calendarEnd.set(Calendar.HOUR_OF_DAY, 0);
        calendarEnd.set(Calendar.MINUTE, 0);

        while (calendarBegin.before(calendarEnd)) {
            Date date = calendarBegin.getTime();
            dateRanges.add(date);
            calendarBegin.add(Calendar.DATE, 1);
        }
        //Так как последнее число не войдет в этот список, то добавляем его вручную
        dateRanges.add(dateEnd);

        if (!includes && dateRanges.size() > 2) {
            dateRanges.remove(0);
            dateRanges.remove(dateRanges.size() - 1);
        }

        return dateRanges;
    }

    /**
     * Получение минут в дне, считая от 00:00. Часы также преобразовываются в минуты
     *
     * @param day - день со временем
     *
     * @return - минуты
     */
    public static int getMinuteOfDay(Date day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(day);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour * 60 + calendar.get(Calendar.MINUTE);
    }

    /**
     * Получаем раделитель в виде даты для группировки сообщений
     *
     * @param previousDate предыдущая дата
     * @param date текущая дата
     *
     * @return текст для разделителя
     */
    public static String getDateForMessageGroup(Date previousDate, Date date) {

        String result;

        Calendar calPrev = Calendar.getInstance();
        Calendar calCurrent = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();

        if (previousDate!=null) calPrev.setTime(previousDate);

        calCurrent.setTime(date);
        calNow.setTime(new Date());

        //Сначала проверяем совпадают ли обе даты, если да то разделитель не ставится
        if (previousDate != null &&
            (calPrev.get(Calendar.YEAR) == calCurrent.get(Calendar.YEAR)
            && calPrev.get(Calendar.MONTH) == calCurrent.get(Calendar.MONTH)
            && calPrev.get(Calendar.DAY_OF_MONTH) == calCurrent.get(Calendar.DAY_OF_MONTH))) {
            result = "";
        } else {
            //Если две даты не совпадают, либо предыдущей даты нету, то выясняем какой разделитель ставить в зависимотсти от даты текущего дня
            if (calNow.get(Calendar.YEAR) == calCurrent.get(Calendar.YEAR)) {
                if (calNow.get(Calendar.MONTH) == calCurrent.get(Calendar.MONTH)) {
                    if (calNow.get(Calendar.DAY_OF_MONTH) == calCurrent.get(Calendar.DAY_OF_MONTH)) {
                        result = "Сегодня";
                    } else if (calNow.get(Calendar.DAY_OF_MONTH) - calCurrent.get(Calendar.DAY_OF_MONTH) == 1) {
                        result = "Вчера";
                    } else {
                        //Ставим дату формата день месяц
                        result = Integer.toString(calCurrent.get(Calendar.DAY_OF_MONTH)) + " " +
                                 MonthConst.getNameByValue(calCurrent.get(Calendar.MONTH));
                    }
                } else {
                    //Ставим дату формата день месяц
                    result = Integer.toString(calCurrent.get(Calendar.DAY_OF_MONTH)) + " " +
                             MonthConst.getNameByValue(calCurrent.get(Calendar.MONTH));
                }
            } else {
                //Ставим дату формата день месяц год
                result = Integer.toString(calCurrent.get(Calendar.DAY_OF_MONTH)) + " " + MonthConst.getNameByValue(calCurrent.get(Calendar.MONTH)) + " " +
                         Integer.toString(calCurrent.get(Calendar.YEAR));
            }
        }

        return result;
    }

    public static Date getFirstDayOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    public static Date getLastDayOfCurrentMonth(){
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, - 1);

        return calendar.getTime();
    }

    public static Date getFirstDayOfPreviousMonth(){
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MONTH, -1);

        return calendar.getTime();
    }


    public static Date getLastDayOfPreviousMonth(){
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(new Date());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, - 1);

        return calendar.getTime();
    }
}
