package org.edec.efficiency.model;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;


@Getter
@Setter
public class ProblemSubjectGroup extends SubjectModel {
    private Boolean attendance;
    private Boolean eok;
    private Boolean performance;

    private Long idEok;
    private Long idLGSS;

    private String teacher;

    public ProblemSubjectGroup () {
    }

    @Override
    public String getFoc () {
        String result = "";
        if (getExam()) {
            result += "экзамен;";
        }
        if (getPass()) {
            result += "зачет;";
        }
        if (getCp()) {
            result += "КП;";
        }
        if (getCw()) {
            result += "КР;";
        }
        if (getPractic()) {
            result += "практика;";
        }
        return result;
    }
}
