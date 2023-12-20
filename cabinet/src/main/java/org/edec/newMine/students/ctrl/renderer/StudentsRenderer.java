package org.edec.newMine.students.ctrl.renderer;

import org.edec.newMine.students.ctrl.WinCreateStudentInASUctrl;
import org.edec.newMine.students.model.StudentsModel;
import org.edec.newMine.students.service.StudentsService;
import org.edec.newMine.students.service.impl.StudentsServiceImpl;
import org.edec.synchroMine.ctrl.WinCreateStudentCtrl;
import org.edec.synchroMine.model.StudentStatusModel;
import org.edec.utility.component.ctrl.WinStudentSubjectCtrl;
import org.edec.utility.constants.StudentStatus;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsRenderer implements ListitemRenderer<StudentsModel> {
    private StudentsService studentsService = new StudentsServiceImpl();
    Runnable fillStudentsListbox;
    Boolean isMineStudents;

    public StudentsRenderer(Runnable fillStudentsListbox, Boolean isMineStudents) {
        this.fillStudentsListbox = fillStudentsListbox;
        this.isMineStudents = isMineStudents;
    }

    @Override
    public void render(Listitem li, StudentsModel data, int i) throws Exception {
        li.setValue(data);
        StudentsModel otherStudentModel = data.getOtherStudentModel();

        Listcell lcPopupFio = new Listcell(i + 1 + ". " + data.getFio());
        lcPopupFio.setParent(li);
        new Listcell(data.getRecordbook()).setParent(li);


        if (otherStudentModel != null) {
            if (data.getIdStudCardMine() != null && otherStudentModel.getIdStudCardMine() != null
                    && data.getIdStudCardMine().equals(otherStudentModel.getIdStudCardMine())) {
                if (data.getRecordbook().equals(otherStudentModel.getRecordbook()) && data.getStatus() == otherStudentModel.getStatus()) {
                    li.setStyle("background: #99ff99;");
                    if (isMineStudents) {
                        final Popup popup = new Popup();
                        popup.setParent(lcPopupFio);
                        popup.setId("popup" + data.getFio() + li.getUuid());

                        Vbox vb = new Vbox();
                        vb.setParent(popup);

                        new Label("Зачетная книжка: " + data.getRecordbook()).setParent(vb);
                        new Label("id Шахты: " + data.getIdStudCardMine()).setParent(vb);
                        new Label("Статус: " + StudentStatus.getStatusByValue(data.getStatus()).getName()).setParent(vb);
                        li.setPopup("popup" + data.getFio() + li.getUuid());
                    }
                } else if (data.getStatus() != otherStudentModel.getStatus()) {
                    li.setStyle("background: #FFFE7E;");
                    if (isMineStudents) {
                        final Popup popup = new Popup();
                        popup.setParent(lcPopupFio);
                        popup.setId("popup" + data.getFio() + li.getUuid());

                        Vbox vb = new Vbox();
                        vb.setParent(popup);

                        new Label("Зачетные книжки: " + data.getRecordbook() + " : " + otherStudentModel.getRecordbook()).setParent(vb);
                        new Label("id Шахты: " + data.getIdStudCardMine() + " : " + otherStudentModel.getIdStudCardMine()).setParent(vb);
                        new Label("Статусы: " + StudentStatus.getStatusByValue(data.getStatus()).getName() + " : "
                                + StudentStatus.getStatusByValue(otherStudentModel.getStatus()).getName()).setParent(vb);
                        Button btnSynch = new Button("Синхронизировать");
                        btnSynch.setParent(vb);
                        btnSynch.addEventListener(Events.ON_CLICK, event -> {
                            studentsService.updateStudentStatus(otherStudentModel, data.getIdSSS());
                            fillStudentsListbox.run();
                            popup.detach();
                        });
                    }
                    li.setPopup("popup" + data.getFio() + li.getUuid());
                }
            } else {
                li.setStyle("background: #FF7373");
                if (isMineStudents) {
                    final Popup popup = new Popup();
                    popup.setParent(lcPopupFio);
                    popup.setId("popup" + data.getFio() + li.getUuid());

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Зачетные книжки: " + data.getRecordbook() + " : " + otherStudentModel.getRecordbook()).setParent(vb);
                    new Label("id Шахты: " + data.getIdStudCardMine() + " : " + otherStudentModel.getIdStudCardMine()).setParent(vb);
                    new Label("Статусы: " + StudentStatus.getStatusByValue(data.getStatus()).getName() + " : "
                            + StudentStatus.getStatusByValue(otherStudentModel.getStatus()).getName()).setParent(vb);
                    if (data.getIdStudCard() != null) {
                        Button btnSynch = new Button("Синхронизировать");
                        btnSynch.setParent(vb);
                        btnSynch.addEventListener(Events.ON_CLICK, event -> {
                            studentsService.updateStudentCardFromMine(data.getIdStudCard(),
                                    otherStudentModel.getIdStudCardMine(), otherStudentModel.getRecordbook());
                            fillStudentsListbox.run();
                            popup.detach();
                        });
                    }
                    li.setPopup("popup" + data.getFio() + li.getUuid());
                }
            }
        } else if (isMineStudents) {
            li.addEventListener(Events.ON_CLICK, event -> {
                Map arg = new HashMap();
                arg.put(WinCreateStudentInASUctrl.SELECTED_STUDENTS, data);
                arg.put(WinCreateStudentInASUctrl.UPDATE_STUDENTS_LISTBOX, fillStudentsListbox);
                ComponentHelper.createWindow("/newMine/students/winCreateStudentInASU.zul", "winCreateStudentInASU", arg).doModal();
            });
        }
        new Listcell(StudentStatus.getStatusByValue(data.getStatus()).getName()).setParent(li);
        new Listcell(data.getCondOfEducationStr()).setParent(li);
    }
}
