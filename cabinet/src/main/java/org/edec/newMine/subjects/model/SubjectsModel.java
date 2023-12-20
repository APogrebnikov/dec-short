package org.edec.newMine.subjects.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class SubjectsModel extends org.edec.model.SubjectModel {
    private Boolean practicType = false, facultative = false;
    private Date datePracticeBegin, datePracticeEnd;
    private Double hoursLecture, hoursPractice, hoursLabaratory, hoursIndependent, hoursExam;
    private Long idChair, idChairMine, idRegisterMine, idCurriculum;
    private Long idLGSS, idSubjMine;
    private String practiceType, practiceTypeDative;
    private Integer practicStartWeek, practicWeekCount, typeOfPractice;
    private String year;
    private List<SubjectsTeacherModel> teachers = new ArrayList<>();

    private SubjectsModel otherSubject;

    private Set<Long> workLoads = new HashSet<>();
    private Set<Long> employees = new HashSet<>();

    private Integer sessionNumber;

    public Double getHoursAll() {
        if (hoursLecture != null &&  hoursPractice  != null && hoursLabaratory  != null && hoursIndependent  != null && hoursExam  != null ) {
            return hoursLecture + hoursPractice + hoursLabaratory + hoursIndependent + hoursExam;
        } else {
            return  0.0;
        }

    }

    public Double getHoursAud() {
        if (hoursLecture != null && hoursPractice != null && hoursLabaratory != null ) {
            return hoursLecture + hoursPractice + hoursLabaratory;
        } else {
            return  0.0;
        }
    }

    public Double getZE(){
        if (getHoursAud() > 0) {
            return getHoursAud()/36;
        } else {
            return 0.0;
        }
    }

    public String getFoc() {
        String foc = "";
        String difPass = "";
        String foc2 = "";
        if (getType() ==1 ) {
            difPass = "диф. ";
        }
        if (getExam()) {
            foc = "Экзамен";
        }
        if (getPass()) {
            foc = "Зачет";
        }
        if (getCp()) {
            foc2 = ", КП ";
        }
        if (getCw()) {
            foc2 = ", КР ";
        }if (getPractic()) {
            foc2 = ", Практика ";
        }
        return difPass + foc + foc2;
    }

    public String printTeachers(){
        String res = "";
        for (SubjectsTeacherModel teacher : this.getTeachers()) {
            res += teacher.getFio() + ";\n";
        }
        return res;
    }

}
