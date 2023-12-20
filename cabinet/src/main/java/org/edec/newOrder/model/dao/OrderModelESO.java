package org.edec.newOrder.model.dao;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by apple on 29.11.16.
 */
@Getter
@Setter
public class OrderModelESO {
    private String fio;
    private String foundation;
    private String foundationLos;
    private String groupname;
    private String recordbook;
    private String sectionname;
    private String additionalInfo;

    private Date firstDate;
    private Date secondDate;
    private Date thirdDate;

    private Date firstDateSection;
    private Date secondDateSection;

    private long idSection;
    private long idStudent;
    private long idOS;
    private Long idMine;

    public OrderModelESO () {
    }


}
