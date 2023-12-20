package org.edec.eok.model.dao;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SubjectEsoModel {
    private Boolean exam;
    private Boolean pass;
    private Boolean cp;
    private Boolean cw;
    private Boolean practic;

    private Double hourscount;

    private Long idEsoCourse;
    private Long idLGSS;

    private String department;
    private String fio;
    private String formOfControl;
    private String groupname;
    private String subjectname;

    public SubjectEsoModel () {
    }

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
        return result;
    }
}
