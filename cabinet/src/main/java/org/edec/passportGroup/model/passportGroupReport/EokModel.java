package org.edec.passportGroup.model.passportGroupReport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EokModel {
    private Boolean exam, pass, cp, cw, practic;

    private Integer countStudent;

    private Long idEsoCourse;

    private String courseName, eokName, formOfControl, groupname, speciallity, subjectname, teacher;

    public String getFormOfControl () {
        String result = "";

        if (exam) {
            result += "экзамен;";
        }
        if (pass) {
            result += "зачет;";
        }
        if (cp) {
            result += "КП;";
        }
        if (cw) {
            result += "КР;";
        }
        if (practic) {
            result += "практика;";
        }

        return result.equals("") ? "-" : result.substring(0, result.length() - 1);
    }
}
