package org.edec.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SubjectModel {
    private Boolean exam = false, pass = false, cp = false, cw = false, practic = false;

    private Double hoursCount, hoursAudCount;

    private Integer focInt;
    private Integer semesternumber;
    private Integer type;

    private Long idSubj;
    private Long idDicSubj;

    private String foc;
    private String subjectcode;
    private String subjectname;

    public String printAllFocForSubject () {
        String result = "";
        if (exam) {
            result += "экзамен; ";
        }
        if (pass) {
            result += "зачет; ";
        }
        if (cp) {
            result += "КП; ";
        }
        if (cw) {
            result += "КР; ";
        }
        if (practic) {
            result += "практика; ";
        }
        return result;
    }
}
