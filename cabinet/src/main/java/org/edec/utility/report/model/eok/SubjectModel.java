package org.edec.utility.report.model.eok;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class SubjectModel {
    private Boolean exam, pass, cp, cw, practic;

    private Integer countStudent;

    private Long idEsoCourse;

    private String courseName;
    private String department;
    private String eokName;
    private String formOfControl;
    private String groupname;
    private String speciallity;
    private String subjectname;
    private String teacher;
    private Date synchProgressDate;
    private Double progress;

    public SubjectModel () {
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

        return result.equals("") ? "-" : result.substring(0, result.length() - 1);
    }
}
