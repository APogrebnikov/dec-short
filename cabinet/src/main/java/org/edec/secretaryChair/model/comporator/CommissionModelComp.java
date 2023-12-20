package org.edec.secretaryChair.model.comporator;

import org.edec.secretaryChair.model.CommissionModel;

import java.util.Comparator;

/**
 * Created by dmmax
 */
public class CommissionModelComp implements Comparator<CommissionModel> {
    public enum CompareMethods {
        BY_DATE, BY_DATE_REV, BY_SEMESTER, BY_SEMESTER_REV, BY_SUBJECT, BY_SUBJECT_REV
    }

    private CompareMethods compareMethods;

    public CommissionModelComp (CompareMethods compareMethods) {
        this.compareMethods = compareMethods;
    }

    @Override
    public int compare (CommissionModel o1, CommissionModel o2) {
        if (o1 != null && o2 != null) {
            switch (compareMethods) {
                case BY_DATE:
                    return compWithNull(o1.getCommissionDate(), o2.getCommissionDate());
                case BY_DATE_REV:
                    return compWithNull(o2.getCommissionDate(), o1.getCommissionDate());
                case BY_SEMESTER:
                    return compWithNull(o1.getSemesterStr(), o2.getSemesterStr());
                case BY_SEMESTER_REV:
                    return compWithNull(o2.getSemesterStr(), o1.getSemesterStr());
                case BY_SUBJECT:
                    return compWithNull(o1.getSubjectName(), o2.getSubjectName());
                case BY_SUBJECT_REV:
                    return compWithNull(o2.getSubjectName(), o1.getSubjectName());
                default:
                    return 0;
            }
        }
        return 0;
    }

    public int compWithNull(Object  in1, Object  in2) {
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
