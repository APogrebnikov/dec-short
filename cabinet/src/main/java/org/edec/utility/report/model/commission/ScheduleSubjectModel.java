package org.edec.utility.report.model.commission;

import lombok.Data;

import java.util.Date;
@Data
public class ScheduleSubjectModel {
    private Date datecommission;

    private String classroom;
    private String subjectname;
    private String groups;
    private String teachers;
    private String groupname;
    private String groupstudentfionames;

    public ScheduleSubjectModel () {
    }
}
