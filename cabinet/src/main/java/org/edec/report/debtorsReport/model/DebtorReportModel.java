package org.edec.report.debtorsReport.model;

import lombok.Data;

@Data
public class DebtorReportModel implements Comparable<DebtorReportModel>{
    private String fio, groupname, subjectname, foc, semester, subjectDepartment, groupDepartment, notBudget, teachers, countDebts, ZE;
    private String phone, email;
    private Boolean hasNegativeComission;
    private Integer is_exam, is_pass, is_cp, is_cw, is_practice;
    private Boolean examComm, passComm, cpComm, cwComm, practicComm, openComm;
    private Long idSemester, idStudentMine;
    private Integer examrating, passrating, courseworkrating, courseprojectrating, practicrating;


    @Override
    public int compareTo(DebtorReportModel o) {
        return 0;
    }
}
