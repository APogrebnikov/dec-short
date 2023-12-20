package org.edec.successful.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ReportStatModel {
    private Set<StudentReportModel> allStudents, successfulStudents, studentsWithOnlyFiveMarks,
                studentsWithFiveAndFourMarks, studentsWithOnlyFourMarks,
                studentsWithFiveFourThreeMarks, studentsWithOnlyThreeMarks,
                studentsWithTwoThreeMarks, studentsWithDebts, studentsWithOneDebt,
                studentsWithTwoDebts, studentsWithThreeOrFourDebts, studentsWithMoreThanFiveDebts,
                studentsWithAllDebts;

    public ReportStatModel() {
        this.allStudents = new HashSet<>();
        this.successfulStudents = new HashSet<>();
        this.studentsWithOnlyFiveMarks = new HashSet<>();
        this.studentsWithFiveAndFourMarks = new HashSet<>();
        this.studentsWithOnlyFourMarks = new HashSet<>();
        this.studentsWithFiveFourThreeMarks = new HashSet<>();
        this.studentsWithOnlyThreeMarks = new HashSet<>();
        this.studentsWithTwoThreeMarks = new HashSet<>();
        this.studentsWithDebts = new HashSet<>();
        this.studentsWithOneDebt = new HashSet<>();
        this.studentsWithTwoDebts = new HashSet<>();
        this.studentsWithThreeOrFourDebts = new HashSet<>();
        this.studentsWithMoreThanFiveDebts = new HashSet<>();
        this.studentsWithAllDebts = new HashSet<>();
    }

    public void mergeInto(ReportStatModel mainStat) {
        mainStat.allStudents.addAll(allStudents);
        mainStat.successfulStudents.addAll(successfulStudents);
        mainStat.studentsWithOnlyFiveMarks.addAll(studentsWithOnlyFiveMarks);
        mainStat.studentsWithFiveAndFourMarks.addAll(studentsWithFiveAndFourMarks);
        mainStat.studentsWithOnlyFourMarks.addAll(studentsWithOnlyFourMarks);
        mainStat.studentsWithFiveFourThreeMarks.addAll(studentsWithFiveFourThreeMarks);
        mainStat.studentsWithOnlyThreeMarks.addAll(studentsWithOnlyThreeMarks);
        mainStat.studentsWithTwoThreeMarks.addAll(studentsWithTwoThreeMarks);
        mainStat.studentsWithDebts.addAll(studentsWithDebts);
        mainStat.studentsWithOneDebt.addAll(studentsWithOneDebt);
        mainStat.studentsWithTwoDebts.addAll(studentsWithTwoDebts);
        mainStat.studentsWithThreeOrFourDebts.addAll(studentsWithThreeOrFourDebts);
        mainStat.studentsWithMoreThanFiveDebts.addAll(studentsWithMoreThanFiveDebts);
        mainStat.studentsWithAllDebts.addAll(studentsWithAllDebts);
    }
}
