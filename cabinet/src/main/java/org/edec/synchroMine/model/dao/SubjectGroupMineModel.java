package org.edec.synchroMine.model.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.edec.model.SubjectModel;


@Getter
@Setter
@ToString
public class SubjectGroupMineModel extends SubjectModel {
    private Boolean practicType, facultative;
    private Double hoursLecture, hoursPractice, hoursLabaratory, hoursIndependent, hoursExam;
    private Integer practicStartWeek, practicWeekCount, typeOfPractice;
    private Long idChairMine, idSubjMine, idWorkload, idRegisterMine;
    private String family, name, patronymic;
}
