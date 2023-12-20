package org.edec.commission.report.model.schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter @Setter @NoArgsConstructor
public class ScheduleChairModel {

    private String fulltitle;

    private List<ScheduleSubjectModel> subjects = new ArrayList<>();
    private Boolean printGroups, printMembersComm;

    @Override
    public ScheduleChairModel clone(){
        ScheduleChairModel clone = new ScheduleChairModel();
        clone.fulltitle = this.fulltitle;
        clone.subjects = new ArrayList<>();
        return clone;
    }
}
