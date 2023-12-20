package org.edec.teacher.ctrl;

import org.edec.teacher.model.GroupModel;
import org.edec.teacher.model.attendanceProgress.AttendanceModel;
import org.edec.teacher.model.attendanceProgress.StudentModel;
import org.edec.teacher.service.AttendProgressService;
import org.edec.teacher.service.impl.AttendProgressServiceImpl;
import org.edec.utility.converter.DateConverter;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.List;


public class WinReportGroupCtrl extends SelectorComposer<Component> {
    public static final String SELECTED_GROUP = "selectedGroup";

    @Wire
    private Label lReportAttendProgress;

    @Wire
    private Listbox lbAttendanceProgress;

    @Wire
    private Listhead lhAttendance;

    private AttendProgressService attendProgressService = new AttendProgressServiceImpl();

    private GroupModel selectedGroup;
    private List<StudentModel> students;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        selectedGroup = (GroupModel) Executions.getCurrent().getArg().get(SELECTED_GROUP);
        lReportAttendProgress.setValue(selectedGroup.getSubject().getSubjectname() + "(" + selectedGroup.getGroupname() + ") " +
                                       selectedGroup.getSubject().getSemester().getSemesterStr());
        Clients.showBusy(lbAttendanceProgress, "Загрузка данных");
        Events.echoEvent("onLater", lbAttendanceProgress, null);
    }

    private void fillStudent () {
        List<StudentModel> students = attendProgressService.getStudentsForGroup(selectedGroup.getIdLGSS());
        int count = 0;
        for (StudentModel student : students) {
            Listitem li = new Listitem();
            li.setParent(lbAttendanceProgress);
            if (count == 0) {
                for (AttendanceModel attendance : student.getAttendances()) {
                    Listheader lhr = new Listheader();
                    lhr.setParent(lhAttendance);
                    lhr.setAlign("center");
                    lhr.setWidth("85px");
                    Label l = new Label(DateConverter.convertDateToString(attendance.getVisitdate()));
                    l.setSclass("cwf-listheader-label");
                    l.setParent(lhr);
                }
            }
            count++;
            new Listcell(String.valueOf(count)).setParent(li);
            new Listcell(student.getFio()).setParent(li);
            new Listcell(student.getProgress() + "%").setParent(li);
            new Listcell(student.getAttendancecount() + "/" + student.getAttendances().size()).setParent(li);
            for (AttendanceModel attendance : student.getAttendances()) {
                Listcell lc = new Listcell();
                lc.setParent(li);
                if (attendance.getAttend() == null || !attendance.getAttend()) {
                    lc.setLabel("н");
                    lc.setStyle("background: #ff9494;");
                } else {
                    lc.setLabel("+");
                    lc.setStyle("background: #94db70;");
                }
            }
        }
    }

    @Listen("onLater = #lbAttendanceProgress")
    public void laterOnAttendanceProgress () {
        if (students == null) {
            fillStudent();
        }
        Clients.clearBusy(lbAttendanceProgress);
    }
}
