package org.edec.rest.model.student.response;

import lombok.Data;

import java.util.Date;

@Data
public class StudentSemester {
    private Long idLGS;
    private Integer semesterNumber;
    private Long idSSS;
    private String groupname;
    private Long idGroup;
    private String semesterName;
    private Date dateofbegin;
    private Date dateofend;
    private Date dateofbeginsession;
    private Date dateofendsession;
    private Date dateofbeginpassweek;
    private Date dateofendpassweek;
}
