package org.edec.synchroMine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.utility.component.model.StudentModel;

@Getter
@Setter
@NoArgsConstructor

public class StudentStatusModel extends StudentModel {
    private Integer isBudget;
    private Integer isTrustAgreement;
    private int formOfStudy, isTransferStudent, isTransfered;
    private Integer formOfControl;
    private Integer condOfEducation;

}
