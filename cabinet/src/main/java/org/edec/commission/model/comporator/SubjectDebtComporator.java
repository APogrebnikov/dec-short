package org.edec.commission.model.comporator;

import org.edec.commission.model.SubjectDebtModel;

import java.util.Comparator;

/**
 * Created by dmmax
 */
public class SubjectDebtComporator implements Comparator<SubjectDebtModel> {
    public enum CompareMethods {
        BY_SUBJECT, BY_SUBJECT_REV, BY_COUNT_DEBTS, BY_COUNT_DEBTS_REV, BY_FOC, BY_FOC_REV, BY_CHAIR, BY_CHAIR_REV, BY_SEM, BY_SEM_REV
    }

    private CompareMethods compareMethods;

    public SubjectDebtComporator (CompareMethods compareMethods) {
        this.compareMethods = compareMethods;
    }

    @Override
    public int compare (SubjectDebtModel o1, SubjectDebtModel o2) {
        if (o1 != null && o2 != null) {
            switch (compareMethods) {
                case BY_SUBJECT:
                    if (o1.getSubjectname() != null && o2.getSubjectname() != null) {
                        return o1.getSubjectname().compareTo(o2.getSubjectname());
                    }
                    return 0;
                case BY_SUBJECT_REV:
                    if (o1.getSubjectname() != null && o2.getSubjectname() != null) {
                        return o2.getSubjectname().compareTo(o1.getSubjectname());
                    }
                case BY_CHAIR:
                    if (o1.getFulltitle() != null && o2.getFulltitle() != null) {
                        return o1.getFulltitle().compareTo(o2.getFulltitle());
                    } else {
                        if (o1.getFulltitle() == null && o2.getFulltitle() == null) {
                            return 0;
                        } else if (o1.getFulltitle() == null) {
                            return -1;
                        } else if (o2.getFulltitle() == null) {
                            return 1;
                        }
                    }
                    return 0;
                case BY_CHAIR_REV:
                    if (o1.getFulltitle() != null && o2.getFulltitle() != null) {
                        return o2.getFulltitle().compareTo(o1.getFulltitle());
                    } else {
                        if (o1.getFulltitle() == null && o2.getFulltitle() == null) {
                            return 0;
                        } else if (o1.getFulltitle() == null) {
                            return 1;
                        } else if (o2.getFulltitle() == null) {
                            return -1;
                        }
                    }
                    return 0;
                case BY_COUNT_DEBTS:
                    return ((Integer) o1.getStudents().size()).compareTo(((Integer) o2.getStudents().size()));
                case BY_COUNT_DEBTS_REV:
                    return ((Integer) o2.getStudents().size()).compareTo(((Integer) o1.getStudents().size()));
                case BY_FOC:
                    if (o1.getFocStr() != null && o2.getFocStr() != null) {
                        return o1.getFocStr().compareTo(o2.getFocStr());
                    }
                    return 0;
                case BY_FOC_REV:
                    if (o1.getFocStr() != null && o2.getFocStr() != null) {
                        return o2.getFocStr().compareTo(o1.getFocStr());
                    }
                    return 0;
                case BY_SEM:
                    if (o1.getSemesterStr() != null && o2.getSemesterStr() != null) {
                        return o1.getSemesterStr().compareTo(o2.getSemesterStr());
                    }
                    return 0;
                case BY_SEM_REV:
                    if (o1.getSemesterStr() != null && o2.getSemesterStr() != null) {
                        return o2.getSemesterStr().compareTo(o1.getSemesterStr());
                    }
                    return 0;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }
}
