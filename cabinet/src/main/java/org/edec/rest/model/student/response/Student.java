package org.edec.rest.model.student.response;

import lombok.Data;

import java.util.Date;

@Data
public class Student {
    private Long idStudent;
    private Long idHumanface;
    private Long idGroup;
    private String surname;
    private String name;
    private String patronymic;
    private String institute;
    private String groupname;
    private String recordbook;
    private Boolean notification;
    private String email;
    private Date birthday;
    private Boolean groupLeader;
    private String chairFulltitle;
    private Date refDateFinish;

    private String formofstudy;
    private String directionName;
    private String directionCode;
    private Integer course;
    private String startPage;
    private Long otherEsoid;
}
