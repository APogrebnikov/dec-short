package org.edec.secretaryChair.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.commons.model.StudentGroupModel;
import org.edec.utility.constants.FormOfControlConst;

import java.util.Date;

/**
 * Модель предназначена для показа студентов и комиссии, в которых они есть
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class StudentCommissionModel extends StudentGroupModel {
    private boolean isPhysicalCulture;
    private Date dateCommission;

    private Integer foc;
    private Long idSubject;

    private String subjectname;

    public String getFocStr () {
        return FormOfControlConst.getName(this.foc).getName();
    }
}
