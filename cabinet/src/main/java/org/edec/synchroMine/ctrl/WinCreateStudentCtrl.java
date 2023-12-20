package org.edec.synchroMine.ctrl;

import org.apache.log4j.Logger;
import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.synchroMine.service.StudentMineService;
import org.edec.synchroMine.service.impl.StudentMineImpl;
import org.edec.utility.component.model.StudentModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;


public class WinCreateStudentCtrl extends SelectorComposer<Component> {
    private static final Logger log = Logger.getLogger(WinCreateStudentCtrl.class.getName());

    public static final String INDEX_PAGE_CTRL = "index_page_ctrl";
    public static final String SELECTED_GROUP = "selected_group";
    public static final String SELECTED_STUDENTS = "selected_students";

    @Wire
    private Tabbox tbStudents;

    private StudentMineService studentMineService = new StudentMineImpl();

    private List<StudentModel> students;
    private IndexPageCtrl indexPageCtrl;
    private String selectedGroup;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        students = (List<StudentModel>) Executions.getCurrent().getArg().get(SELECTED_STUDENTS);
        selectedGroup = (String) Executions.getCurrent().getArg().get(SELECTED_GROUP);
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(INDEX_PAGE_CTRL);

        fillTabbox(students);
    }

    private void fillTabbox (List<StudentModel> students) {
        for (StudentModel student : students) {
            if (student.getOtherStudentModel() != null) {
                continue;
            }
            Tab tab = new Tab();
            tab.setParent(tbStudents.getTabs());
            tab.setValue(student);
            tab.setLabel(student.getShortFio());

            Tabpanel tabpanel = new Tabpanel();
            tabpanel.setParent(tbStudents.getTabpanels());
            final Vbox vbContent = new Vbox();
            vbContent.setParent(tabpanel);

            Hbox hbSearch = new Hbox();
            hbSearch.setParent(vbContent);

            List<StudentModel> tmpStudents = new ArrayList<>();
            tmpStudents.add(student);
            tmpStudents.addAll(
                    studentMineService.getStudentsByFilter(student.getFio(), student.getRecordbook(), student.getIdStudCardMine()));
            if (tmpStudents.size() > 1) {
                final Combobox cmbStudent = new Combobox();
                cmbStudent.setParent(hbSearch);
                cmbStudent.setReadonly(true);
                for (StudentModel tmpStudent : tmpStudents) {
                    Comboitem ci = new Comboitem(tmpStudent.getGroupname() + "/" + tmpStudent.getRecordbook() +
                                                 (tmpStudent.getIdHum() == null ? "(шахты)" : ""));
                    ci.setParent(cmbStudent);
                    ci.setValue(tmpStudent);
                    tmpStudent.setRecordbook(student.getRecordbook());
                    tmpStudent.setIdStudCardMine(student.getIdStudCardMine());
                }
                cmbStudent.addEventListener(Events.ON_CHANGE, changeStudent(vbContent, cmbStudent));

                Button btnCreate = new Button("Добавить");
                btnCreate.setParent(hbSearch);
                btnCreate.addEventListener(Events.ON_CLICK, createStudentInGroup(null, cmbStudent, false));

                Button btnAddToHum = new Button("Добавить к сущности студента");
                btnAddToHum.setParent(hbSearch);
                btnAddToHum.addEventListener(Events.ON_CLICK, createStudentInGroup(student, cmbStudent, true));
            } else {
                tmpStudents.addAll(studentMineService.getStudentsByFilter(student.getFio()));

                Combobox cmbStudent = new Combobox();
                cmbStudent.setParent(hbSearch);
                cmbStudent.setReadonly(true);
                for (StudentModel tmpStudent : tmpStudents) {
                    tmpStudent.setRecordbook(student.getRecordbook());
                    tmpStudent.setIdStudCardMine(student.getIdStudCardMine());
                    String name = (tmpStudent.getIdHum() == null ? "(шахты)" : "(" + tmpStudent.getIdHum() + ")") + tmpStudent.getShortFio();
                    Comboitem ci = new Comboitem(name);
                    ci.setParent(cmbStudent);
                    ci.setValue(tmpStudent);
                }
                cmbStudent.addEventListener(Events.ON_CHANGE, changeStudent(vbContent, cmbStudent));

                Button btnCreate = new Button("Добавить");
                btnCreate.setParent(hbSearch);
                btnCreate.addEventListener(Events.ON_CLICK, createStudentInGroup(student, null, false));

                Button btnCreateWithoutCheck = new Button("Добавить без проверки");
                if (tmpStudents.size() > 1) {
                    btnCreateWithoutCheck.setParent(hbSearch);
                }
                btnCreateWithoutCheck.addEventListener(Events.ON_CLICK, createStudentInGroup(student, cmbStudent, true));
            }
        }
    }

    private EventListener<Event> changeStudent (final Vbox vb, final Combobox cmbStudent) {
        return event -> {
            List<Component> components = new ArrayList<>();
            for (Component comp : vb.getChildren()) {
                if (comp instanceof Grid) {
                    components.add(comp);
                }
            }
            vb.getChildren().removeAll(components);
            Grid grid = createGrid(cmbStudent.getSelectedItem().getValue());
            grid.setParent(vb);
        };
    }

    private EventListener<Event> createStudentInGroup (final StudentModel student, final Combobox cmbStudent, boolean addToHum) {
        return event -> {
            StudentModel selectedStudent = (student != null && cmbStudent == null)
                                           ? student
                                           : (StudentModel) cmbStudent.getSelectedItem().getValue();
            if (addToHum) {
                Long idStudentCard = null;
                idStudentCard = studentMineService.createStudent(student.getGroupname(), null, null, null, null, student.getRecordbook(), null, student.getIdStudCardMine(),
                                                                 selectedStudent.getIdHum()
                );
                if (idStudentCard == null) {
                    PopupUtil.showError("Не удалось создать студнета2");
                    return;
                }
                selectedStudent.setIdStudCard(idStudentCard);
            }
            if (selectedStudent.getIdStudCard() == null) {
                Long idStudentCard = studentMineService.createStudent(selectedStudent.getGroupname(),selectedStudent.getFamily(), selectedStudent.getName(),
                                                                      selectedStudent.getPatronymic(), selectedStudent.getBirthday(),
                                                                      selectedStudent.getRecordbook(), selectedStudent.getSex(),
                                                                      selectedStudent.getIdStudCardMine(), null
                );
                if (idStudentCard == null) {
                    PopupUtil.showError("Не удалось создать студнета");
                    return;
                }
                selectedStudent.setIdStudCard(idStudentCard);
            }
            try {
                int trustAgreement = student.getCondOfEducation() == 2 ? 1 : 0;
                int governmentFinanced = (student.getCondOfEducation() == 1 || student.getCondOfEducation() == 2) ? 1 : 0;
                studentMineService.createSSSforStudent(selectedStudent.getIdStudCard(), trustAgreement, governmentFinanced,
                                                       student.getStatus() == -1 ? 1 : 0, null, selectedGroup
                );
                studentMineService.createSRforStudent(selectedStudent.getIdStudCard(), null, selectedGroup);
                PopupUtil.showInfo("Студент успешно добавлен в группу");
                log.info("Добавление студента из шахт: " + new TemplatePageCtrl().getCurrentUser().getShortFIO() + ". Студент: " +
                         selectedStudent.getFio() + ", группа: " + selectedGroup);
                indexPageCtrl.fillStudents();
                int index = tbStudents.getSelectedIndex();
                tbStudents.getTabs().getChildren().remove(index);
                tbStudents.getTabpanels().getChildren().remove(index);
                if (tbStudents.getTabs().getChildren().size() == 0) {
                    getSelf().detach();
                } else {
                    tbStudents.setSelectedTab((Tab) tbStudents.getTabs().getChildren().get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
                PopupUtil.showError("Ошибка при добавление студента (" + selectedStudent.getFio() + ") в группу ()");
            }
        };
    }

    private Grid createGrid (StudentModel data) {
        Grid gridContent = new Grid();
        Rows rows = new Rows();
        rows.setParent(gridContent);

        Row rowFamily = new Row();
        rowFamily.setParent(rows);
        new Label("Фамилия").setParent(rowFamily);
        new Label(data.getFamily()).setParent(rowFamily);

        Row rowName = new Row();
        rowName.setParent(rows);
        new Label("Имя").setParent(rowName);
        new Label(data.getName()).setParent(rowName);

        Row rowPatronymic = new Row();
        rowPatronymic.setParent(rows);
        new Label("Отчество").setParent(rowPatronymic);
        new Label(data.getPatronymic()).setParent(rowPatronymic);

        Row rowRecordbook = new Row();
        rowRecordbook.setParent(rows);
        new Label("Зач. книжка").setParent(rowRecordbook);
        new Label(data.getRecordbook()).setParent(rowRecordbook);

        Row rowGroupname = new Row();
        rowGroupname.setParent(rows);
        new Label("Группа").setParent(rowGroupname);
        new Label(data.getGroupname()).setParent(rowGroupname);

        return gridContent;
    }
}
