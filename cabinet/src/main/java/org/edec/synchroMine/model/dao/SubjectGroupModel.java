package org.edec.synchroMine.model.dao;

import lombok.Getter;
import lombok.Setter;
import org.edec.model.SubjectModel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
public class SubjectGroupModel extends SubjectModel {
    private Boolean practicType = false, facultative = false;
    private Date datePracticeBegin, datePracticeEnd;
    private Double hoursLecture, hoursPractice, hoursLabaratory, hoursIndependent, hoursExam;
    private Long idChair, idChairMine;
    private Long idLGSS, idSubjMine;
    private String practiceType, practiceTypeDative;

    private SubjectGroupModel otherSubject;

    private Set<Long> workLoads = new HashSet<>();
    private Set<Long> employees = new HashSet<>();
}
