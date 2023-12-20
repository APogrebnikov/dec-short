package org.edec.utility;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.time.DateUtils;
import org.edec.utility.converter.DateConverter;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;

@UtilityClass
public class DateUtility {

    public static Date getNextDate(@NonNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, 1);

        return calendar.getTime();
    }

    public static Date getNextDateExcludeWeekend(@NonNull Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getNextDate(date));

        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendar.getTime();
    }

    public static boolean isAfterDate(@NonNull Date currentDate, @NonNull Date afterDate) {

        Calendar current = DateUtils.toCalendar(currentDate);
        Calendar after = DateUtils.toCalendar(afterDate);

        return current.get(Calendar.YEAR) > after.get(Calendar.YEAR) || (current.get(Calendar.YEAR) == after.get(Calendar.YEAR) &&
                                                                         current.get(Calendar.DAY_OF_YEAR) >
                                                                         after.get(Calendar.DAY_OF_YEAR));
    }

    public static boolean isSameDay(@NonNull Date date1, @NonNull Date date2) {

        return DateUtils.isSameDay(date1, date2);
    }

    public static boolean isSameDayOrAfter(@NonNull Date date1, @NonNull Date date2) {

        return isSameDay(date1, date2) || isAfterDate(date1, date2);
    }

    public static boolean isBeforeDate(@NonNull Date currentDate, @NonNull Date beforeDate) {

        return !isSameDayOrAfter(currentDate, beforeDate);
    }

    public static boolean isSameDayOrBefore(@NonNull Date currentDate, @NonNull Date beforeDate) {

        return isSameDay(currentDate, beforeDate) || isBeforeDate(currentDate, beforeDate);
    }

    public static boolean isBetweenDate(@NonNull Date currentDate, @NonNull Date beginPeriod, @NonNull Date endPeriod) {

        return isSameDayOrAfter(currentDate, beginPeriod) && isSameDayOrBefore(currentDate, endPeriod);
    }

    public static boolean isNotBetweenDate(@NonNull Date currentDate, @NonNull Date beginPeriod, @NonNull Date endPeriod) {

        return !isBetweenDate(currentDate, beginPeriod, endPeriod);
    }

    /**
     * Выполняется проверка если дата совпадает с текущей датой или на день позже с учетом выходных
     *
     * @param currentDate текущая дата
     * @param checkedDate проверяемая дата
     */
    public static boolean isSameDayOrNextWithWeekends(@NonNull Date currentDate, @NonNull Date checkedDate) {
        return isSameDay(currentDate, checkedDate) || isSameDay(currentDate, getNextDateExcludeWeekend(checkedDate));
    }

    /**
     * Проверка принадлежит ли текущая дата к указанному в днях периоду (на данный момент день + 3 дня в запасе)
     * @param currentDate текущая дата
     * @param checkedDate дата начала перода
     * @param periodSize длина периода (в днях)
     */
    public static boolean isDayBelongsToPeriod(@NonNull Date currentDate, @NonNull Date checkedDate, int periodSize) {
        Calendar date = Calendar.getInstance();
        date.setTime(checkedDate);

        while (periodSize != 0) {
            // Учет самого воскресенья
            if ((date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) && isSameDay(currentDate, date.getTime())) {
                return true;
            }
            if (date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            || isHoliday(date)) {
                date.add(Calendar.DAY_OF_YEAR, 1);
            }
            if (isSameDay(currentDate, date.getTime())) {
                return true;
            }

            date.add(Calendar.DAY_OF_YEAR, 1);
            periodSize--;
        }

        return false;
    }

    private static boolean isHoliday(Calendar date){
        return (date.get(Calendar.MONTH) == Calendar.FEBRUARY) && (date.get(Calendar.DAY_OF_MONTH) == 23)
            || (date.get(Calendar.MONTH) == Calendar.MARCH) && (date.get(Calendar.DAY_OF_MONTH) == 8)
            || (date.get(Calendar.MONTH) == Calendar.MAY) && (date.get(Calendar.DAY_OF_MONTH) == 1)
            || (date.get(Calendar.MONTH) == Calendar.MAY) && (date.get(Calendar.DAY_OF_MONTH) == 9)
            || (date.get(Calendar.MONTH) == Calendar.JUNE) && (date.get(Calendar.DAY_OF_MONTH) == 12)
            //|| (date.get(Calendar.MONTH) == Calendar.JUNE) && (date.get(Calendar.DAY_OF_MONTH) == 24)
            || (date.get(Calendar.MONTH) == Calendar.JULY) && (date.get(Calendar.DAY_OF_MONTH) == 1);
    }

    public static boolean isSameDayOrNext(Date currentDate, Date checkedDate) {
        Calendar nextDayDate = Calendar.getInstance();
        nextDayDate.setTime(checkedDate);
        nextDayDate.add(Calendar.DAY_OF_MONTH, 1);

        return isSameDay(currentDate, checkedDate) || isSameDay(currentDate, nextDayDate.getTime());
    }

    public static int getCurrentYear(){
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getPreviousYear(){
        return getCurrentYear() - 1;
    }
}
