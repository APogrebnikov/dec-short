package org.edec.schedule.ctrl;

import org.edec.schedule.model.dao.GroupSubjectLesson;
import org.edec.schedule.service.AttendanceService;
import org.edec.schedule.service.ScheduleParser;
import org.edec.schedule.service.impl.AttendanceImpl;
import org.edec.schedule.service.xls.XlsScheduleParser;
import org.edec.schedule.service.xls.XlsScheduleParserConfig;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.SubjectModel;
import org.edec.utility.component.renderer.SubjectRenderer;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.QualificationConst;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.List;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Combobox cmbGroup;
    @Wire
    private Vbox vbScheduleSfu, vbScheduleEso;

    private AttendanceService attendanceService;
    private XlsScheduleParserConfig config;
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ScheduleParser parser;

    private List<SubjectModel> subjectsGroup;

    protected void fill() {
        config = new XlsScheduleParserConfig(currentModule.getDepartments().get(0).getInstitute());
        parser = new XlsScheduleParser(config);
        attendanceService = new AttendanceImpl(parser);

        cmbGroup.setItemRenderer((comboitem, o, i) -> {
            comboitem.setValue(o);
            comboitem.setLabel(((GroupModel) o).getGroupname());
        });
        cmbGroup.setModel(new ListModelList<>(
                attendanceService.getGroupByInstAndFormOfStudy(
                        currentModule.getDepartments().get(0).getIdInstitute(),
                        currentModule.getFormofstudy())));

    }

    @Listen("onChange = #cmbGroup")
    public void selectedGroup() {

        GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        subjectsGroup = attendanceService.getSubjectsByGroupname(selectedGroup.getGroupname());
        refreshTimetableSfu(selectedGroup.getGroupname(), selectedGroup.getCourse(), selectedGroup.getQualification());
        refreshLessonsEso();
    }

    @Listen("onClick = #btnRefreshSchedule")
    public void refreshScheduleOnClickBtn() {

        if (cmbGroup.getSelectedItem() == null) {
            PopupUtil.showError("Выберите сначала группу");
            return;
        }
        selectedGroup();
    }

    @Listen("onClick = #btnOpenConfigWindow")
    public void openConfigWindow() {
        WinScheduleConfigCtrl win = new WinScheduleConfigCtrl(config);
        win.setParent(getSelf());
        win.doModal();
    }

    private void refreshTimetableSfu(String groupname, Integer course, Integer qualification) {
        List<GroupSubjectLesson> lessonsTimetable = null;
        try {
            lessonsTimetable = parser.parseScheduleByParams(
                    groupname,
                    course,
                    qualification == 3 ? QualificationConst.MASTER : QualificationConst.BACHELOR);
        } catch (IllegalArgumentException e) {
            // TODO: Сообщить что не удалось найти оригинал
        }

        if (lessonsTimetable == null) {
            // костыль на случай кривого названия групп
            groupname = groupname.replace("б","").replace("Б","");
            lessonsTimetable = parser.parseScheduleByParams(
                    groupname,
                    course,
                    qualification == 3 ? QualificationConst.MASTER : QualificationConst.BACHELOR);
        }
        vbScheduleSfu.getChildren().clear();

        if (lessonsTimetable == null) {
            PopupUtil.showError("Не удалось найти группу в расписании");
            new Label("Не удалось найти группу в расписании").setParent(vbScheduleSfu);
            Hbox hbox = new Hbox();
            hbox.setParent(vbScheduleSfu);
            Textbox tbGroup = new Textbox();
            tbGroup.setParent(hbox);
            tbGroup.setPlaceholder("Группа");
            Intbox ibCourse = new Intbox();
            ibCourse.setParent(hbox);
            ibCourse.setPlaceholder("Курс");
            Checkbox chQualification = new Checkbox();
            chQualification.setParent(hbox);
            chQualification.setLabel("Магистр");
            Button btnSearch = new Button("Поиск");
            btnSearch.setParent(hbox);
            btnSearch.addEventListener(Events.ON_CLICK, event -> refreshTimetableSfu(tbGroup.getValue(), ibCourse.getValue(), chQualification.isChecked() ? 3 : 1));
        } else {
            new Label("Расписание из exel СФУ:").setParent(vbScheduleSfu);
            Button btnSynchSchedule = new Button("Синхронизация");
            btnSynchSchedule.setParent(vbScheduleSfu);
            List<GroupSubjectLesson> finalLessonsTimetable = lessonsTimetable;
            btnSynchSchedule.addEventListener(Events.ON_CLICK, event -> syncSchedule(finalLessonsTimetable));
            fillVboxByLessons(vbScheduleSfu, lessonsTimetable, true);
        }
    }

    public void refreshLessonsEso() {

        vbScheduleEso.getChildren().clear();
        GroupModel selectedGroup = cmbGroup.getSelectedItem().getValue();
        List<GroupSubjectLesson> lessonsEso = attendanceService.getLessonsFromDb(selectedGroup.getGroupname());
        if (lessonsEso == null || lessonsEso.size() == 0) {
            PopupUtil.showInfo("Расписания в базе данных нет");
            vbScheduleEso.getChildren().clear();
            new Label("Расписания в базе данных нет").setParent(vbScheduleEso);
        } else {
            new Label("Расписание из БД АСУ ИКИТ:").setParent(vbScheduleEso);
            Hbox hbEsoAction = new Hbox();
            hbEsoAction.setParent(vbScheduleEso);
            Button btnDelAll = new Button("Удалить все");
            btnDelAll.setParent(hbEsoAction);
            btnDelAll.addEventListener(Events.ON_CLICK, event -> deleteAllScheduleOnClick(selectedGroup));
            Button btnAddSchedule = new Button("Добавить расписание");
            btnAddSchedule.setParent(hbEsoAction);
            btnAddSchedule.addEventListener(Events.ON_CLICK, event -> addScheduleWin(selectedGroup, this));
            fillVboxByLessons(vbScheduleEso, lessonsEso, false);
        }
    }

    private void addScheduleWin(final GroupModel selectedGroup, final IndexPageCtrl indexPageCtrl) {
        selectedGroup.setSubjects(subjectsGroup);
        new WinCreateScheduleCtrl().showWin(selectedGroup, indexPageCtrl);
    }

    private void deleteAllScheduleOnClick(final GroupModel selectedGroup) {
        if (attendanceService.deleteScheduleByGroup(selectedGroup.getGroupname())) {
            PopupUtil.showInfo("Расписание группы успешно удалено");
            selectedGroup();
        }
    }

    private void syncSchedule(final List<GroupSubjectLesson> lessons) {
        for (GroupSubjectLesson lesson : lessons) {
            for (SubjectModel subject : subjectsGroup) {
                if (subject.getSubjectname().equals(lesson.getSubjectName())) {
                    lesson.setIdLGSS(subject.getIdLGSS());
                    break;
                }
            }
            if (lesson.getIdLGSS() != null) {
                Long dicDay = lesson.getDicDayLesson() != null ? lesson.getDicDayLesson().getIdDicDayLesson() : null;
                Long dicTimeLesson = lesson.getDicTimeLesson()!=null ? lesson.getDicTimeLesson().getIdDicTimeLesson() : null;
                if (!attendanceService.createSchedule(lesson.getIdLGSS(), lesson.getWeek(), lesson.getRoom(), lesson.getTeacher(), dicDay, dicTimeLesson, lesson.getLesson())) {
                    System.out.println("Ну удалось создать расписание: " + lesson.getSubjectName());
                }
            }
        }
        selectedGroup();
    }

    private void fillVboxByLessons(Vbox vbox, List<GroupSubjectLesson> lessons, boolean create) {

        for (GroupSubjectLesson lesson : lessons) {
            boolean addDay = true;
            for (Component component : vbox.getChildren()) {
                if (component.getId().equals("gb" + vbox.getId() + lesson.getDicDayLesson().getName())) {
                    setLbForDay((Listbox) component.getChildren().get(0), lesson, create);
                    addDay = false;
                    break;
                }
            }
            if (addDay) {
                Groupbox gbDay = new Groupbox();
                gbDay.setParent(vbox);
                gbDay.setId("gb" + vbox.getId() + lesson.getDicDayLesson().getName());
                gbDay.setMold("3d");
                gbDay.setTitle(lesson.getDicDayLesson().getName());
                Listbox lbDay = new Listbox();
                lbDay.setParent(gbDay);
                Listhead lhDay = new Listhead();
                lhDay.setParent(lbDay);
                componentService.getListheader("Время", "85px", "", "").setParent(lhDay);
                componentService.getListheader("Нед.", "50px", "", "center").setParent(lhDay);
                componentService.getListheader("Предмет", "", "1", "").setParent(lhDay);
                componentService.getListheader("Ауд.", "100px", "", "").setParent(lhDay);
                componentService.getListheader("Преподаватель", "125px", "", "").setParent(lhDay);
                componentService.getListheader("", "140px", "", "center").setParent(lhDay);
                setLbForDay(lbDay, lesson, create);
            }
        }
    }

    private void setLbForDay(Listbox lbDay, final GroupSubjectLesson lesson, boolean create) {
        Listitem liTime = new Listitem();
        liTime.setParent(lbDay);
        if(lesson.getDicTimeLesson() != null) {
            new Listcell(lesson.getDicTimeLesson().getTimeName()).setParent(liTime);
        } else {
            new Listcell("").setParent(liTime);
        }
        new Listcell(String.valueOf(lesson.getWeek())).setParent(liTime);
        new Listcell(lesson.getSubjectName()).setParent(liTime);
        new Listcell(lesson.getRoom()).setParent(liTime);
        new Listcell(lesson.getTeacher()).setParent(liTime);
        Listcell lc = new Listcell();
        lc.setParent(liTime);
        Hbox hbox = new Hbox();
        hbox.setParent(lc);
        if (create) {
            final Combobox cmbLesson = new Combobox();
            cmbLesson.setParent(hbox);
            cmbLesson.setReadonly(true);
            cmbLesson.setPlaceholder("Предмет..");
            cmbLesson.setWidth("80px");
            cmbLesson.setModel(new ListModelList<>(subjectsGroup));
            cmbLesson.setItemRenderer(new SubjectRenderer());
            cmbLesson.addEventListener(Events.ON_CHANGE, event -> {
                SubjectModel selectedSubject = cmbLesson.getSelectedItem().getValue();
                lesson.setIdLGSS(selectedSubject.getIdLGSS());
            });
            final SubjectModel selectedSubject = getSubject(lesson.getSubjectName());
            if (selectedSubject != null) {
                cmbLesson.addEventListener("onAfterRender", event -> cmbLesson.setSelectedIndex(subjectsGroup.indexOf(selectedSubject)));
                lesson.setIdLGSS(selectedSubject.getIdLGSS());
                liTime.setStyle("background: #95FF82;");
            } else {
                liTime.setStyle("background: #FFFE7E;");
            }
            Button btnAdd = new Button("", "/imgs/addaltCLR.png");
            btnAdd.setParent(hbox);
            btnAdd.addEventListener(Events.ON_CLICK, event -> addSchedule(lesson));
        } else {
            Button btnDel = new Button("", "/imgs/del.png");
            btnDel.setParent(hbox);
            btnDel.addEventListener(Events.ON_CLICK, event -> removeSchedule(lesson.getIdLSCH()));
        }
    }

    private void removeSchedule(final Long idLSCH) {
        if (!attendanceService.deleteScheduleById(idLSCH)) {
            PopupUtil.showError("Не удалось удалить расписание");
        } else {
            selectedGroup();
        }
    }

    private void addSchedule(final GroupSubjectLesson selectedLesson) {
        if (selectedLesson.getIdLGSS() == null) {
            PopupUtil.showWarning("Выберите предмет");
            return;
        }
        if (!attendanceService.createSchedule(selectedLesson.getIdLGSS(), selectedLesson.getWeek(), selectedLesson.getRoom(),
                selectedLesson.getTeacher(), selectedLesson.getDicDayLesson().getIdDicDayLesson(), selectedLesson.getDicTimeLesson().getIdDicTimeLesson(), selectedLesson.getLesson())) {
            PopupUtil.showError("Не удалось создать");
        } else {
            selectedGroup();
        }
    }

    private SubjectModel getSubject(String subjectname) {
        for (SubjectModel subject : subjectsGroup) {
            if (subject.getSubjectname().equals(subjectname.trim())) {
                return subject;
            }
        }
        return null;
    }
}