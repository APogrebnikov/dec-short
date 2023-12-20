package org.edec.commission.report.model.notion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter @Setter @NoArgsConstructor
public class NotionDto {

    //Студент
    private Integer course;
    private String family, name, patronymic;
    private String groupName;
    private String programCode, programTitle;
    private String specialityCode, specialityTitle;

    //Предмет
    private Integer semesterNumber;
    private String focStr, subjectName;

    private String getSpeciality() {
        if (StringUtils.isNoneEmpty(specialityCode, specialityTitle)) {
            return specialityCode + " - «" + specialityTitle + "»";
        }
        return "";
    }

    private String getProgram() {
        if (StringUtils.isNoneEmpty(programCode, programTitle)) {
            return programCode + " - «" + programTitle + "»";
        }
        return "";
    }

    public String getFio() {
        return family + " " + name + " " + patronymic;
    }

    public NotionStudentModel toNotionStudentModel() {

        NotionStudentModel student = new NotionStudentModel();

        student.setCourse(course);
        student.setFamily(family);
        student.setName(name);
        student.setPatronymic(patronymic);
        student.setGroupName(groupName);
        student.setProfile(getProgram());
        student.setSpeciality(getSpeciality());

        return student;
    }

    public NotionStudentDebtModel toNotionStudentDebtModel() {

        NotionStudentDebtModel debt = new NotionStudentDebtModel();

        debt.setFocStr(focStr);
        debt.setSemesterNumber(semesterNumber);
        debt.setSubjectName(subjectName);

        return debt;
    }
}
