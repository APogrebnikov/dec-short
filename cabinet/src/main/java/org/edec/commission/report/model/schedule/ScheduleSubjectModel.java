package org.edec.commission.report.model.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ScheduleSubjectModel implements Cloneable {

    private Date datecommission;

    private String classroom;
    private String subjectname;
    private String teachers;
    private String groupstudentfionames;

    private Set<String> groups = new HashSet<>();
    private Set<String> studentGroupNames = new HashSet<>();

    private List<StudentModel> students = new ArrayList<>();


    @Override
    public ScheduleSubjectModel clone() {
        ScheduleSubjectModel clone = new ScheduleSubjectModel();
        clone.classroom = this.classroom;
        clone.datecommission = this.datecommission;
        clone.subjectname = this.subjectname;
        clone.students = new ArrayList<>();
        return clone;
    }
}
