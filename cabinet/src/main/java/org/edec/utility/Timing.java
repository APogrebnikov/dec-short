package org.edec.utility;

/**
 * Класс предназначен для измерения выполнения каких-либо действий
 */
public class Timing {
    private long startTime;

    public Timing () {
        this.startTime = System.currentTimeMillis();
    }

    public static Timing start () {
        return new Timing();
    }

    public void reset () {
        startTime = System.currentTimeMillis();
    }

    public long getPastTime () {
        return System.currentTimeMillis() - startTime;
    }

    public long getPastTimeAndReset () {
        long pastTime = getPastTime();
        startTime = System.currentTimeMillis();
        return pastTime;
    }

    public static String getRusTimestampByMinutes (long minutes) {
        if (minutes == 0) {
            return "< 1 минуты";
        }
        String result = "";
        long modulo = minutes;
        if (60 * 24 < modulo) {
            result = getDay(modulo / (60 * 24));
            modulo = modulo % (60 * 24);
        }
        if (60 < modulo) {
            result = result + getHour(modulo / 60);
            modulo = modulo % 60;
        }
        result = result + getMinutes(modulo);
        return result;
    }

    public static String getDay (long day) {
        if (day == 0) {
            return "";
        } else if (day == 1) {
            return "1 день ";
        } else if (day == 2 || day == 3 || day == 4) {
            return day + " дня ";
        } else {
            return day + " дней ";
        }
    }

    private static String getHour (long hours) {
        if (hours == 0) {
            return "";
        } else if (hours == 1) {
            return "1 час ";
        } else if (hours == 2 || hours == 3 || hours == 4) {
            return hours + " часа ";
        } else {
            return hours + " часов ";
        }
    }

    private static String getMinutes (long minutes) {
        if (minutes == 0) {
            return "";
        } else if (minutes == 1) {
            return "1 минуту";
        } else if (minutes == 2 || minutes == 3 || minutes == 4) {
            return minutes + " минуты";
        } else {
            return minutes + " минут";
        }
    }
}
