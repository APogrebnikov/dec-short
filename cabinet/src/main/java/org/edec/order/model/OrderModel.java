package org.edec.order.model;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class OrderModel {
    private Date datecreated;
    private Date datefinish;
    private Date datesign;

    private Long idOrder;
    private Long idOrderRule;
    private Long orderType;
    private Long idSemester;

    private Long countStudents;

    private String currenthumanface;
    private String description;
    private String idLotus;
    private String number;
    private String operation;
    private String semesterSeason;
    private String status;
    private String type;
    private String url;

    private List<SectionModel> sections;
    private List<GroupModel> groups;
}
