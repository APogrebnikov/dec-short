package org.edec.subjectsAnalysis.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubjectsAnalysisModel {
    private String subjectname;
    private long  partFive, partFour, partThree, partTwo, partPass, partNoPass;
    private Double fiveCount, fourCount, threeCount, twoCount, absenceCount,avgRating;
    private Integer moda, totalRiskScore, idChair;
    private Double oneRetake, moreThenOneRetake;
    private Double notPassCount, passCount, passCountFive, passCountFour, passCountThree;


}
