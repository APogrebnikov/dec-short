package org.edec.commission.model.comporator;

import org.edec.commission.model.SubjectDebtModel;

import java.util.Comparator;

public class ComissionComparator  implements Comparator<SubjectDebtModel> {

    public enum CompareMethods {
        BY_SUBJ, BY_SUBJ_REV, BY_CHAIR, BY_CHAIR_REV
    }

    private CompareMethods compareMethod;

    public ComissionComparator (CompareMethods compareMethod) {
        super();
        this.compareMethod = compareMethod;
    }

    @Override
    public int compare (SubjectDebtModel o1, SubjectDebtModel o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return -1;
        }
        if (o1 != null && o2 == null) {
            return 1;
        }

        switch (compareMethod) {
            case BY_SUBJ:
                return o1.getSubjectname().compareTo(o2.getSubjectname());
            case BY_SUBJ_REV:
                return o2.getSubjectname().compareTo(o1.getSubjectname());
            case BY_CHAIR:
                return o1.getFulltitle().compareTo(o2.getFulltitle());
            case BY_CHAIR_REV:
                return o2.getFulltitle().compareTo(o1.getFulltitle());
        }

        return 0;
    }
}
