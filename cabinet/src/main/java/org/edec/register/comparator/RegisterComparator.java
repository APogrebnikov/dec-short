package org.edec.register.comparator;

import org.edec.register.model.RegisterModel;

import java.util.Comparator;

/**
 * Created by apple on 21.06.17.
 */
public class RegisterComparator implements Comparator<RegisterModel> {
    public static CompareMethods prevCompareMethod;

    public enum CompareMethods {
        BY_SEM, BY_SEM_REV, BY_SUBJ, BY_SUBJ_REV, BY_GROUP, BY_GROUP_REV, BY_FK, BY_FK_REV, BY_TYPE, BY_TYPE_REV, BY_DATE, BY_DATE_REV, BY_REG_NUMBER, BY_REG_NUMBER_REV;
    }

    private CompareMethods compareMethod;

    public RegisterComparator (CompareMethods compareMethod) {
        super();
        this.compareMethod = compareMethod;
    }

    @Override
    public int compare (RegisterModel o1, RegisterModel o2) {
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
            case BY_SEM:
                return o1.getIdSemester().compareTo(o2.getIdSemester());
            case BY_SEM_REV:
                return o2.getIdSemester().compareTo(o1.getIdSemester());
            case BY_SUBJ:
                return o1.getSubjectName().compareTo(o2.getSubjectName());
            case BY_SUBJ_REV:
                return o2.getSubjectName().compareTo(o1.getSubjectName());
            case BY_GROUP:
                return o1.getGroupName().compareTo(o2.getGroupName());
            case BY_GROUP_REV:
                return o2.getGroupName().compareTo(o1.getGroupName());
            case BY_FK:
                return new Integer(o1.getFoc()).compareTo(new Integer(o2.getFoc()));
            case BY_FK_REV:
                return new Integer(o2.getFoc()).compareTo(new Integer(o1.getFoc()));
            case BY_TYPE:
                return new Integer(Math.abs(o1.getRetakeCount())).compareTo(new Integer(Math.abs(o2.getFoc())));
            case BY_TYPE_REV:
                return new Integer(Math.abs(o2.getRetakeCount())).compareTo(new Integer(Math.abs(o1.getFoc())));
            case BY_DATE:
                return compareBydate(o1, o2, true);
            case BY_DATE_REV:
                return compareBydate(o1, o2, false);
            case BY_REG_NUMBER:
                return compareByNumber(o1, o2, true);
            case BY_REG_NUMBER_REV:
                return compareByNumber(o1, o2, false);
        }

        return 0;
    }

    private int compareByNumber (RegisterModel reg1, RegisterModel reg2, boolean isAscending) {

        if (reg1.getRegisterNumber() == null && reg2.getRegisterNumber() == null) {
            return 0;
        }

        if (reg1.getRegisterNumber() == null) {
            return 1;
        }

        if (reg2.getRegisterNumber() == null) {
            return -1;
        }

        String o1Str = reg1.getRegisterNumber().replaceAll("/к", "").replaceAll("/и", "").replaceAll("/о", "");

        String o2Str = reg2.getRegisterNumber().replaceAll("/к", "").replaceAll("/и", "").replaceAll("/о", "");

        if (isAscending) {
            return Integer.parseInt(o1Str) - Integer.parseInt(o2Str);
        } else {
            return Integer.parseInt(o2Str) - Integer.parseInt(o1Str);
        }
    }

    private int compareBydate (RegisterModel reg1, RegisterModel reg2, boolean isAscending) {

        if (reg1.getSignDate() == null && reg2.getSignDate() == null) {
            return 0;
        }

        if (reg1.getSignDate() == null) {
            return 1;
        }

        if (reg2.getSignDate() == null) {
            return -1;
        }

        if (isAscending) {
            return reg1.getSignDate().compareTo(reg2.getSignDate());
        } else {
            return reg2.getSignDate().compareTo(reg1.getSignDate());
        }
    }
}
