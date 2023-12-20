package org.edec.commission.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubjectDebtModel implements Comparable<SubjectDebtModel> {

    private boolean signed;

    private Date dateComission, dateofbegincomission, dateofendcomission;

    private Integer checkedcount, countstudent, semesternumber;

    private Long idChair, idSemester, idSubj, idRegComission;

    private String classroom, focStr, fulltitle, semesterStr, subjectname;
    private String groupNames, groupStudentFioNames;
    private String teachers;

    private List<StudentDebtModel> students = new ArrayList<>();

    public String getFulltitle() {
        return fulltitle == null ? "" : fulltitle;
    }

    @Override
    public int compareTo(SubjectDebtModel o) {
        if (this.getIdChair().compareTo(o.getIdChair()) == 0) {
            return o.getFocStr().compareTo(this.getFocStr());
        } else {
            return o.getIdChair().compareTo(this.getIdChair());
        }
    }
}
