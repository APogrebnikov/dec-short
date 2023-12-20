package org.edec.newMine.subjects.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.edec.model.SubjectModel;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubjectMineModel extends SubjectModel {
    private Boolean practicType, facultative;
    private Double hoursLecture, hoursPractice, hoursLabaratory, hoursIndependent, hoursExam;
    private Integer practicStartWeek, practicWeekCount, typeOfPractice;
    private Long idChairMine, idSubjMine,  idRegisterMine,  idCurriculum;
    private String year;
    private List<SubjectsTeacherModel> teachers = new ArrayList<>();

    public Double getHoursAll() {
        return hoursLecture + hoursPractice + hoursLabaratory + hoursIndependent + hoursExam;
    }

    public Double getHoursAud() {
        return hoursLecture + hoursPractice + hoursLabaratory;
    }

    public Double getZE(){
        return getHoursAud()/36;
    }

}
