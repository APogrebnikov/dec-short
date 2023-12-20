package org.edec.register.model.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterModelEso {
    private Long idRegister, idRegisterMine, idSubject, idSemester, idSSS, idSRH, idInstitute, idLgss;
    private String groupName, subjectName, semester, registerNumber, certNumber, registerUrl, family, name, patronymic;
    private String familyTeacher, nameTeacher, patronymicTeacher;
    private Integer foc, fos, retakeCount, rating, type, qualification, synchStatus;
    private Date signDate, dateOfEnd, dateOfBegin, changeDateTime, dateOfSecondSignBegin, dateOfSecondSignEnd;
    private Date examDate, passDate, cwDate, cpDate, practicDate, endDateMainRegister, beginDateMainRegister;
    private Date dateOfEndComission;
    private Date dateOfGroupFinish;
}
