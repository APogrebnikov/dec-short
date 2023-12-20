package org.edec.contingentMovement.model.comparator;

import lombok.experimental.UtilityClass;
import org.edec.contingentMovement.model.ResitRatingModel;

@UtilityClass
public class ResitRatingComparator {

    public int compareRatingsBySemesterSubject(ResitRatingModel o1, ResitRatingModel o2) {
        if (o1.getSemesternumber().equals(o2.getSemesternumber())) {
            return o1.getSubjectname().compareTo(o2.getSubjectname());
        } else {
            return o1.getSemesternumber().compareTo(o2.getSemesternumber());
        }
    }
}
