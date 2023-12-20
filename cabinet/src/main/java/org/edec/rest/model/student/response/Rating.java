package org.edec.rest.model.student.response;

import lombok.Data;

import java.util.Date;

@Data
public class Rating {
    private Long idLGS;
    private Long idGroup;
    private Date dateOfPass;
    private Date consultationDate;
    private Boolean isExam;
    private Boolean isPass;
    private Boolean isCP;
    private Boolean isCW;
    private Boolean isPractice;
    private Integer examrating;
    private Integer passrating;
    private Integer cprating;
    private Integer cwrating;
    private Integer practicerating;
    private String examTutor;
    private String passTutor;
    private String cpTutor;
    private String cwTutor;
    private String practiceTutor;
    private Integer semester;
    private Long hoursCount;
    private String subjectName;
    private Integer visitcount,skipcount;
    private Long esogradecurrent, esogrademax;
    private Long idLGSS;
    private String login;
}
