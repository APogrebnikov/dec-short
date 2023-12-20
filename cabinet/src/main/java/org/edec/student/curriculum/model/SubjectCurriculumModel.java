package org.edec.student.curriculum.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubjectCurriculumModel {
    private String subjectname, codeSubject, groupname, foc;
    private Integer semesterNumber;
    private Integer  ZE, hoursAll, hoursAud;
    private Integer  examRating, passRating, cwRating, cpRating, practicRting;
    private Integer  isExam, isPass, isCw, isCp, isPractic;
    private Long idSubject, otherIdSubject, idSem;
    private Boolean checkSubject;
    private Boolean learnedSubject = false;
    private List<String> focList = new ArrayList<>();
    private Date dateofendsemester, dateofbeginsemester;
}
