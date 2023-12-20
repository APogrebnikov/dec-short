package org.edec.passportGroup.ctrl;

import org.edec.passportGroup.ctrl.renderer.GroupReportListRenderer;
import org.edec.passportGroup.model.GroupModel;
import org.edec.passportGroup.model.StudentModel;
import org.edec.passportGroup.model.SubjectModel;
import org.edec.passportGroup.service.PassportGroupService;
import org.edec.passportGroup.service.impl.PassportGroupServiceESO;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

import java.util.List;


public class WinGroupReportCtrl extends SelectorComposer<Component> {
    @Wire
    private Window winGroupReport;

    @Wire
    private Listbox listReport;

    @Wire
    private Checkbox chbShowNegativeMarks;

    PassportGroupService service = new PassportGroupServiceESO();

    private Runnable updateGroupReport = this::fillGroupReport;

    List<SubjectModel> subjects;
    List<StudentModel> students;
    GroupModel group;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        group = (GroupModel) Executions.getCurrent().getArg().get("group");
        subjects = service.getSubjectsByGroup(group.getIdLgs());

        for (SubjectModel subject : subjects) {
            if (subject.getIdLgss() != null) {
                subject.setEmployeeModels(service.getEmployeesBySubject(subject.getIdLgss()));
            }
        }

        fillGroupReport();
    }

    public void fillGroupReport () {
        students = service.getStudentsByGroup(group.getIdLgs());

        service.setStatisticForSubjects(students, subjects);

        listReport.setItemRenderer(new GroupReportListRenderer(subjects, group, false, updateGroupReport, chbShowNegativeMarks.isChecked()));
        ListModelList listModelList = new ListModelList<>(students);
        listModelList.setMultiple(true);
        listReport.setModel(listModelList);
        listReport.renderAll();
    }

    @Listen("onCheck = #chbShowNegativeMarks")
    public void showNegativeMarks(){
        fillGroupReport();
    }

    @Listen("onClick = #report_close_btn")
    public void close () {
        winGroupReport.detach();
    }
}
