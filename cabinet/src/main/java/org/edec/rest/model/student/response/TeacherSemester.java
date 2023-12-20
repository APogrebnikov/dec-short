package org.edec.rest.model.student.response;

import lombok.Data;
import org.edec.utility.constants.FormOfStudy;

@Data
public class TeacherSemester {
    private Long idSemester;
    private String semesterName;
    private Long idInstitute;
    private String instituteName;
    private Integer formOfStudy;
    private String formOfStudyStr;

    public String getFormOfStudyStr() {
        return FormOfStudy.getFormOfStudyByType(this.formOfStudy).getName();
    }
}
