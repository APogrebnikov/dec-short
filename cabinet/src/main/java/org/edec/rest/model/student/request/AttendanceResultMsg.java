package org.edec.rest.model.student.request;

import lombok.Data;
import org.edec.rest.model.BaseUserMsg;
import org.edec.rest.model.student.response.Attendance;
import org.edec.rest.model.student.response.Shedule;

import java.util.Date;
import java.util.List;

@Data
public class AttendanceResultMsg extends BaseUserMsg {
    private Date date;
    private Shedule scheduleSubject;
    private List<Attendance> students;
}
