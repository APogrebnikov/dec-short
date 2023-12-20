package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by antonskripacev on 09.04.17.
 */
@Getter
@Setter
public class StudentModel {
    private Long idSSS, idDicGroup, idCurrentDicGroup, idSemester, idSR, idSc;
    private Integer type;
    private String fullName;
    private Boolean deducted, academicLeave, listener, governmentFinanced, selected;
    private List<RatingModel> ratings;
    private List<ScholarshipInfo> listScholarshipInfo;
}
