package org.edec.register.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.constants.FormOfControlConst;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * @author Anton Skripachev
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class RegisterModel {
    private Long idRegister, idRegisterMine, idSubject, idSemester, idInstitute, idLgss;
    private String groupName, subjectName, semester, registerNumber, certNumber, registerUrl;
    private HashSet<String> teachers;
    private Integer foc, retakeCount, fos, qualification, synchStatus;
    private Date signDate, dateOfEnd, dateOfBegin, dateOfSecondSignBegin, dateOfSecondSignEnd;
    private Date examDate, passDate, cwDate, cpDate, practicDate, dateBeginMainRegister, dateEndMainRegister;
    private Date dateOfGroupFinish;
    private List<StudentModel> students = new ArrayList<>();

    public boolean isRegisterSigned() {
        return registerUrl != null && !registerUrl.equals("");
    }

    public Date getMainDate() {
        if (getFoc() == FormOfControlConst.EXAM.getValue()){
            return getExamDate();
        }
        if (getFoc() == FormOfControlConst.PASS.getValue()){
            return getPassDate();
        }
        if (getFoc() == FormOfControlConst.CP.getValue()){
            return getCpDate();
        }
        if (getFoc() == FormOfControlConst.CW.getValue()){
            return getCwDate();
        }
        if (getFoc() == FormOfControlConst.PRACTIC.getValue()){
            return getPracticDate();
        }

        return new Date();
    }
}
