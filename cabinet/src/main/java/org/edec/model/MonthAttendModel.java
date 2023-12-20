package org.edec.model;

import java.util.Date;


public class MonthAttendModel {
    private Date day;
    private Long count;

    public MonthAttendModel () {
    }

    public Date getDay () {
        return day;
    }

    public void setDay (Date day) {
        this.day = day;
    }

    public Long getCount () {
        return count;
    }

    public void setCount (Long count) {
        this.count = count;
    }
}
