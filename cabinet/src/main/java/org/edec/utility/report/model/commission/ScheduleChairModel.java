package org.edec.utility.report.model.commission;

import java.util.ArrayList;
import java.util.List;

public class ScheduleChairModel {
    private String fulltitle;

    private List<ScheduleSubjectModel> subjects = new ArrayList<>();

    private Boolean printGroups, printMembersComm;

    public ScheduleChairModel () {
    }

    public String getFulltitle () {
        return fulltitle;
    }

    public void setFulltitle (String fulltitle) {
        this.fulltitle = fulltitle;
    }

    public List<ScheduleSubjectModel> getSubjects () {
        return subjects;
    }

    public void setSubjects (List<ScheduleSubjectModel> subjects) {
        this.subjects = subjects;
    }

    public Boolean getPrintGroups(){return printGroups;}

    public void setPrintGroups (Boolean printGroups) {
        this.printGroups = printGroups;
    }
    public Boolean getPrintMembersComm(){return printMembersComm;}

    public void setPrintMembersComm (Boolean printMembersComm) {
        this.printMembersComm = printMembersComm;
    }

}
