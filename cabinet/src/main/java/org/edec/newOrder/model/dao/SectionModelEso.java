package org.edec.newOrder.model.dao;

import java.util.Date;

/**
 * Created by antonskripacev on 03.01.17.
 */
public class SectionModelEso {
    String name;

    Long idLOS;

    Date firstDate;
    Date secondDate;

    public Date getFirstDate () {
        return firstDate;
    }

    public void setFirstDate (Date firstDate) {
        this.firstDate = firstDate;
    }

    public Date getSecondDate () {
        return secondDate;
    }

    public void setSecondDate (Date secondDate) {
        this.secondDate = secondDate;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Long getIdLOS () {
        return idLOS;
    }

    public void setIdLOS (Long idLOS) {
        this.idLOS = idLOS;
    }
}
