package org.edec.register.comparator;

import org.edec.register.model.SubjectModel;

import java.util.Comparator;

public class SubjectComparator implements Comparator<SubjectModel> {

    public enum CompareMethods {
        BY_COURSE_AND_GROUP
    }

    private CompareMethods compareMethod;

    public SubjectComparator (CompareMethods compareMethod) {
        super();
        this.compareMethod = compareMethod;
    }

    @Override
    public int compare (SubjectModel o1, SubjectModel o2) {
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
            case BY_COURSE_AND_GROUP:
                Integer course1 = o1.getCourse();
                Integer course2 = o2.getCourse();
                int sComp = course1.compareTo(course2);

                if (sComp != 0) {
                    return sComp;
                } else {
                    String groupName1 = o1.getGroupName();
                    String groupName2 = o2.getGroupName();
                    return groupName1.compareTo(groupName2);
                }
        }
        return 0;
    }
}
