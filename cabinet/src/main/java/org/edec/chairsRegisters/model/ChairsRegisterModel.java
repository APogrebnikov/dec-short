package org.edec.chairsRegisters.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ChairsRegisterModel {
    private String subjectname, registerNumber, groupname, foc, fioTeachers, certnumber, semesterStr;
    private Long idRegister, idDepartment, idComissionRegister, idInstitute, idSemester, idLgss;
    private int retakeCount, qualification, fos;
    private Date signdate, passdate, examdate;
    private Date beginDate, endDate, secondBeginDate, secondEndDate;
    // -- для комиссий
    private Date dateBeginComission, dateOfEndComission, comissionDate, dateOfSecondSignEnd;
    private String timeCom, classroom;
    private String signatorytutor;
}
