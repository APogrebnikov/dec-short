package org.edec.subjectsAnalysis.model;

import java.util.Comparator;

public class SubjectsAnalysisComparator implements Comparator<SubjectsAnalysisModel> {

    public enum CompareMethods {
        BY_SUBJECTNAME, BY_AVG_RATING, BY_ONE_RETAKE, BY_MORE_THAN_ONE_RETAKE, BY_THREE, BY_FOUR,
        BY_FIVE, BY_NOT_ATTEST, BY_PASS, BY_NOT_PASS, BY_MODA, BY_TOTAL_RISK,
        BY_SUBJECTNAME_REV, BY_AVG_RATING_REV, BY_ONE_RETAKE_REV, BY_MORE_THAN_ONE_RETAKE_REV, BY_THREE_REV, BY_FOUR_REV,
        BY_FIVE_REV, BY_NOT_ATTEST_REV, BY_PASS_REV, BY_NOT_PASS_REV, BY_MODA_REV, BY_TOTAL_RISK_REV
    }

    private CompareMethods compareMethods;

    public SubjectsAnalysisComparator (CompareMethods compareMethods) {
        this.compareMethods = compareMethods;
    }
    @Override
    public int compare(SubjectsAnalysisModel o1, SubjectsAnalysisModel o2) {
        if (o1 != null && o2 != null) {
            switch (compareMethods) {
                case BY_SUBJECTNAME:
                    return compWithNull(o1.getSubjectname(), o2.getSubjectname());
                case BY_AVG_RATING:
                    return compWithNull(o1.getAvgRating(), o2.getAvgRating());
                case BY_ONE_RETAKE:
                    return compWithNull(o1.getOneRetake(), o2.getOneRetake());
                case BY_MORE_THAN_ONE_RETAKE:
                    return compWithNull(o1.getMoreThenOneRetake(), o2.getMoreThenOneRetake());
                case BY_THREE:
                    return compWithNull(o1.getPartThree(), o2.getPartThree());
                case BY_FOUR:
                    return compWithNull(o1.getPartFour(), o2.getPartFour());
                case BY_FIVE:
                    return compWithNull(o1.getPartFive(), o2.getPartFive());
                case BY_NOT_ATTEST:
                    return compWithNull(o1.getPartTwo(), o2.getPartTwo());
                case BY_PASS:
                    return compWithNull(o1.getPartPass(), o2.getPartPass());
                case BY_NOT_PASS:
                    return compWithNull(o1.getPartNoPass(), o2.getPartNoPass());
                case BY_MODA:
                    return compWithNull(o1.getModa(), o2.getModa());
                case BY_TOTAL_RISK:
                    return compWithNull(o1.getTotalRiskScore(), o2.getTotalRiskScore());
                case BY_SUBJECTNAME_REV:
                    return compWithNull(o2.getSubjectname(), o1.getSubjectname());
                case BY_AVG_RATING_REV:
                    return compWithNull(o2.getAvgRating(), o1.getAvgRating());
                case BY_ONE_RETAKE_REV:
                    return compWithNull(o2.getOneRetake(), o1.getOneRetake());
                case BY_MORE_THAN_ONE_RETAKE_REV:
                    return compWithNull(o2.getMoreThenOneRetake(), o1.getMoreThenOneRetake());
                case BY_THREE_REV:
                    return compWithNull(o2.getPartThree(), o1.getPartThree());
                case BY_FOUR_REV:
                    return compWithNull(o2.getPartFour(), o1.getPartFour());
                case BY_FIVE_REV:
                    return compWithNull(o2.getPartFive(), o1.getPartFive());
                case BY_NOT_ATTEST_REV:
                    return compWithNull(o2.getPartTwo(), o1.getPartTwo());
                case BY_PASS_REV:
                    return compWithNull(o2.getPartPass(), o1.getPartPass());
                case BY_NOT_PASS_REV:
                    return compWithNull(o2.getPartNoPass(), o1.getPartNoPass());
                case BY_MODA_REV:
                    return compWithNull(o2.getModa(), o1.getModa());
                case BY_TOTAL_RISK_REV:
                    return compWithNull(o2.getTotalRiskScore(), o1.getTotalRiskScore());
                default:
                    return 0;
            }
        }
        return 0;
    }

    private int compWithNull(Object  in1, Object  in2) {
        Comparable<Object> o1 = (Comparable<Object>) in1;
        Comparable<Object> o2 = (Comparable<Object>) in2;
        if (o1 != null) {
            if (o2 == null) {
                return 1;
            } else {
                return o1.compareTo(o2);
            }
        } else {
            if (o1 == null && o2 == null)
            {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
