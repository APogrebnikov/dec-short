package org.edec.newOrder.model.dao;

import java.util.Date;

/**
 * Created by antonskripacev on 19.01.17.
 */
public class ReferenceModelESO {
    private Long id;
    private Date dateGet;
    private Date dateStart;
    private Date dateFinish;

    public Long getId () {
        return id;
    }

    public void setId (Long id) {
        this.id = id;
    }

    public Date getDateGet () {
        return dateGet;
    }

    public void setDateGet (Date dateGet) {
        this.dateGet = dateGet;
    }

    public Date getDateFinish () {
        return dateFinish;
    }

    public void setDateFinish (Date dateFinish) {
        this.dateFinish = dateFinish;
    }

    public Date getDateStart () {
        return dateStart;
    }

    public void setDateStart (Date dateStart) {
        this.dateStart = dateStart;
    }
}
