package org.edec.commission.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.SubjectModel;


@Getter
@Setter
@NoArgsConstructor
public class StudentDebtModel extends SubjectModel implements Comparable<StudentDebtModel> {
    private boolean checkKuts;

    private Boolean examComm, passComm, cpComm, cwComm, practicComm;
    private Boolean  isGovernmentFinanced;

    private Integer examrating, passrating, cprating, cwrating, practicrating;
    private Integer esoExamRating, esoPassRating, esoCpRating, esoCwRating;

    private Long idChair;
    private Long idSc;
    private Long idSSS;
    private Long idSemester;
    private Long idSr;
    private Long idSrh;

    private String fio;
    private String fulltitle;
    private String groupname;
    private String semesterStr;
    private String teacherStr;

    /**
     * Не учавствуют в запросе
     **/
    private boolean openComm;
    private Integer rating;
    private String focStr;

    @Override
    public int compareTo (StudentDebtModel o) {
        if (o.getSemesterStr().compareTo(this.getSemesterStr()) == 0) {
            if (o.getSemesternumber().compareTo(this.getSemesternumber()) == 0) {
                if (o.getSubjectname().compareTo(this.getSubjectname()) == 0) {
                    return o.getFocStr().compareTo(this.getFocStr());
                } else {
                    return o.getSubjectname().compareTo(this.getSubjectname());
                }
            } else {
                return o.getSemesternumber().compareTo(this.getSemesternumber());
            }
        } else {
            return o.getSemesterStr().compareTo(this.getSemesterStr());
        }
    }
}
