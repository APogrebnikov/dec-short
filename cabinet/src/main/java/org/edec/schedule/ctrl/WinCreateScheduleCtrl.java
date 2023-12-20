package org.edec.schedule.ctrl;

import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.HashMap;
import java.util.Map;


public class WinCreateScheduleCtrl extends SelectorComposer<Window> {
    public static final String URI = "/schedule/winCreateSchedule.zul";
    public static final String ID_COMPONENT = "winCreateSchedule";
    public static final String SELECTED_GROUP = "selected_group";
    public static final String INDEX_PAGE = "index_page";

    @Wire
    private Checkbox chLesson;
    @Wire
    private Combobox cmbCreateScheSubject, cmbCreateScheWeek, cmbCreateScheLessonDay, cmbCreateScheLessonTime;
    @Wire
    private Label lSelectedGroup;
    @Wire
    private Textbox tbCreateScheRoom, tbCreateScheTeacher;

    private AttendanceService attendanceService;

    private GroupModel selectedGroup;
    private IndexPageCtrl indexPage;

    public void showWin (GroupModel selectedGroup, IndexPageCtrl indexPage) {
        Map<String, Object> arg = new HashMap<>();
        arg.put(SELECTED_GROUP, selectedGroup);
        arg.put(INDEX_PAGE, indexPage);

        ComponentHelper.createWindow(WinCreateScheduleCtrl.URI, WinCreateScheduleCtrl.ID_COMPONENT, arg).doModal();
    }

    @Override
    public void doAfterCompose (Window comp) throws Exception {
        super.doAfterCompose(comp);
        selectedGroup = (GroupModel) Executions.getCurrent().getArg().get(SELECTED_GROUP);
        indexPage = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE);

        attendanceService = new AttendanceImpl();

        cmbCreateScheSubject.setModel(new ListModelList<>(selectedGroup.getSubjects()));
        cmbCreateScheSubject.setItemRenderer((ComboitemRenderer<SubjectModel>) (ci, data, index) -> {
            ci.setValue(data);
            ci.setLabel(data.getSubjectname());
        });
        lSelectedGroup.setValue("Выбранная группа: " + selectedGroup.getGroupname() + ", " + selectedGroup.getCourse() + " курс");
    }

    @Listen("onClick = #btnCreateSche")
    public void createSchedule () {
        if (cmbCreateScheSubject.getSelectedItem() == null) {
            DialogUtil.exclamation("Нужно выбрать предмет");
            return;
        }
        SubjectModel selectedSubject = cmbCreateScheSubject.getSelectedItem().getValue();
        Long idDay = (long) (cmbCreateScheLessonDay.getSelectedIndex() + 1);
        Long idTime = (long) (cmbCreateScheLessonTime.getSelectedIndex() + 1);
        if (attendanceService.createSchedule(selectedSubject.getIdLGSS(), cmbCreateScheWeek.getSelectedIndex() + 1,
                                             tbCreateScheRoom.getValue(), tbCreateScheTeacher.getValue(), idDay, idTime,
                                             chLesson.isChecked()
        )) {
            PopupUtil.showInfo("Расписание успешно добавлено");
            indexPage.refreshLessonsEso();
        } else {
            PopupUtil.showError("Расписание не удалось добавить");
        }
    }
}