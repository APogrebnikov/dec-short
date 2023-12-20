package org.edec.studentPassport.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.StudentModel;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class StudentStatusModel extends StudentModel {
    private Boolean academicLeave, deducted, foreigner, isDebtor;
    private Boolean governmentFinanced, trustAgreement;
    private Boolean educationcomplite;

    private Date birthday;

    private Integer course, sex, semesterNumber;

    private Long idDG, idInstitute;
    private Long idSemester;
    private Long currentGroupId;
    private Long mineId;

    private String groupname, prevGroupName, direction;
}
