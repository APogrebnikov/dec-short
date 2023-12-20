package org.edec.secretaryChair.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
public class CommissionDayModel {
    /**
     * День комиссии
     */
    private Date day;

    private DayType type = DayType.NONE_IN_PERIOD;
    /**
     * Содержит время, когда проводится уже комиссия.
     * Если размер = 0, значит комисси на этот день не назначены
     */
    private List<Date> busyTimes;

    public CommissionDayModel(Date day) {
        this.day = day;
    }

    public List<Date> getBusyTimes () {
        if (busyTimes == null) {
            busyTimes = new ArrayList<>();
        }
        return busyTimes;
    }

    public enum DayType {
        NONE_IN_PERIOD, //Не входит в комиссионный период
        FREE_DAY, //Свободный день
        CLOSE_DAY, //Нет свободного времени
        FREE_TIME; //Есть свободное время
    }
}