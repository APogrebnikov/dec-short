package org.edec.newMine.subjects.ctrl.renderer;

import org.edec.newMine.subjects.model.SubjectsModel;
import org.edec.newMine.subjects.service.SubjectService;
import org.edec.newMine.subjects.service.impl.SubjectServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class SubjectRenderer implements ListitemRenderer<SubjectsModel> {
    private Long idLGS;
    private Runnable update;
    private Boolean isMine;

    private SubjectService subjectService = new SubjectServiceImpl();
    private static DecimalFormat df = new DecimalFormat("0.0");

    public SubjectRenderer(Long idLGS, Runnable update, Boolean isMine) {
        this.idLGS = idLGS;
        this.update = update;
        this.isMine = isMine;
    }
    @Override
    public void render(Listitem li, SubjectsModel data, int i) throws Exception {
        li.setValue(data);
        SubjectsModel otherSubject = data.getOtherSubject();

        new Listcell(data.getSubjectcode()).setParent(li);
        Listcell lcSubjectname = new Listcell(data.getSubjectname());
        lcSubjectname.setParent(li);

        new Listcell(isMine ? df.format(data.getZE()).replace(",", ".") : df.format(data.getHoursAudCount()/36).replace(",", ".")).setParent(li);
        new Listcell(isMine ? data.getHoursAud().toString() : data.getHoursAudCount().toString()).setParent(li);
        new Listcell(isMine ? data.getHoursAll().toString() : data.getHoursCount().toString()).setParent(li);
        new Listcell(data.getFoc()).setParent(li);
        new Listcell(data.getIdSubjMine() == null ? "" : String.valueOf(data.getIdSubjMine())).setParent(li);
        if (isMine) new Listcell(data.getSessionNumber() == null ? "" : String.valueOf(data.getSessionNumber())).setParent(li);

        if (otherSubject != null) {
            //Если совпадают и ид шахт и название, то оставить так
            if (data.getIdSubjMine() != null && otherSubject.getIdSubjMine() != null && data.getSubjectcode() != null
                    && data.getIdSubjMine().equals(otherSubject.getIdSubjMine()) && data.getSubjectcode().equals(otherSubject.getSubjectcode()) &&
                    data.getSubjectname().toLowerCase().equals(otherSubject.getSubjectname().toLowerCase())
                    && Objects.deepEquals(data.getHoursCount(), otherSubject.getHoursCount())) {
                li.setStyle("background: #99ff99;");
                /// Добавляем возможность удалить (Костыль для заочного отделения)
                final Popup popup = new Popup();
                popup.setParent(lcSubjectname);
                //popup.setId("popup" + data.getIdLGSS() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Информация о предмете в АСУ").setParent(vb);
                new Label("Название: " + data.getSubjectname()).setParent(vb);
                new Label("Идентификатор: " + data.getIdSubjMine().toString()).setParent(vb);
                new Label("Код: " + data.getSubjectcode()).setParent(vb);
                new Label("Часы: " + data.getHoursCount()).setParent(vb);
                new Label("Преподаватели: " + data.printTeachers()).setParent(vb);

                Button btnDeleteSubject = new Button("Удалить");
                btnDeleteSubject.setParent(vb);
                btnDeleteSubject.addEventListener(Events.ON_CLICK, event -> {
                    if (subjectService.deleteSubjectByLGSS(data.getIdLGSS())) {
                        PopupUtil.showInfo("Предмет успешно удален");
                        update.run();
                    } else {
                        PopupUtil.showError("Не удалось удалить предмет");
                    }
                    popup.detach();
                });

                //li.setPopup("popup" + data.getIdLGSS() + li.getListbox().getUuid());
                li.setPopup(popup);
            }
            //Если не совпадают ИД шахт или название, то можно обновить данные у нас в системе
            else {
                li.setStyle("background: #FFFE7E;");
                //Если предмет из АСУ ИКИТ
                if (!isMine) {
                   final Popup popup = new Popup();
                    popup.setParent(lcSubjectname);
                    //popup.setId("popup" + data.getSubjectcode() + li.getListbox().getUuid() + "-"+i);

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Название: АСУ ИКИТ - '" + data.getSubjectname() + "'").setParent(vb);
                    new Label("Название: шахты - '" + otherSubject.getSubjectname() + "'").setParent(vb);
                    new Label("Идентификатор: АСУ ИКИТ - '" + data.getIdSubjMine().toString() + "'").setParent(vb);
                    new Label("Идентификатор: шахты - '" + otherSubject.getIdSubjMine().toString() + "'").setParent(vb);
                    new Label("Код: АСУ ИКИТ - '" + data.getSubjectcode() + "'").setParent(vb);
                    new Label("Код: шахты - '" + otherSubject.getSubjectcode() + "'").setParent(vb);
                    new Label("Часы: АСУ ИКИТ - " + data.getHoursCount() + ", шахты - " + otherSubject.getHoursCount()).setParent(vb);
                    Button btnUpdate = new Button("Обновить");
                    btnUpdate.setParent(vb);
                    btnUpdate.addEventListener(Events.ON_CLICK, event -> {
                        if (otherSubject.getIdChair() == null) {
                            PopupUtil.showError("Невозможно определить кафедру, принадлежащая предмету");
                            return;
                        }
                        List<String> registers = subjectService.getRegisterNumberByLGSandSubj(idLGS, data.getIdSubj());
                        if (registers.size() != 0) {
                            PopupUtil.showWarning("Нельзя обновить предмет, потому что есть не закрытые ведомости с номерами: " + registers.toString());
                            return;
                        }
                        if (subjectService.updateSubject(data, otherSubject)) {
                            PopupUtil.showInfo("Обновление предмета прошло успешно!");
                            update.run();
                        } else {
                            Messagebox.show("Обновить предмет не удалось!", "Ошибка", Messagebox.OK, Messagebox.ERROR);
                        }
                        popup.detach();
                    });

                    //li.setPopup("popup" + data.getSubjectname() + li.getListbox().getUuid());
                    li.setPopup(popup);
                }
            }
        } else {
            li.setStyle("background: #FF7373;");
            //Если предмет из шахт, то мы можем его добавить
            if (isMine) {
               final Popup popup = new Popup();
                popup.setParent(lcSubjectname);
                //popup.setId("popup" + data.getSubjectcode()+data.getHoursAud() +data.getFoc()+ li.getListbox().getUuid()+"-"+i);

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("ИД кафедры: " + (data.getIdChair() == null ? "не найден" : String.valueOf(data.getIdChair()))).setParent(vb);
                new Label("ИД кафедры(шахты): " + data.getIdChairMine()).setParent(vb);
                new Label("Преподаватели: " + data.printTeachers()).setParent(vb);

                Button btnAddSubject = new Button("Добавить предмет");
                btnAddSubject.setParent(vb);
                btnAddSubject.addEventListener(Events.ON_CLICK, event -> {
                    if (data.getIdChair() == null) {
                        Messagebox.show("Невозможно определить кафедедру у предмета, обратитесь к администратору!", "Ошибка", Messagebox.OK,
                                Messagebox.ERROR
                        );
                        return;
                    }
                    if (subjectService.createSubjectSRforLGS(idLGS, data)) {
                        PopupUtil.showInfo("Предмет успешно создан");
                        update.run();
                    } else {
                        PopupUtil.showError("Предмет не удалось создать");
                    }
                    popup.detach();
                });
                //li.setPopup("popup" + data.getSubjectname() + li.getListbox().getUuid());
                li.setPopup(popup);
            } else {
               final Popup popup = new Popup();
                popup.setParent(lcSubjectname);
                //popup.setId("popup" + data.getIdLGSS() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Информация о предмете в АСУ").setParent(vb);
                new Label("Название: " + data.getSubjectname()).setParent(vb);
                if (data.getIdSubjMine() != null) {
                    new Label("Идентификатор: " + data.getIdSubjMine().toString()).setParent(vb);
                } else {
                    new Label("Идентификатор: null").setParent(vb);
                }
                new Label("Код: " + data.getSubjectcode()).setParent(vb);
                new Label("Часы: " + data.getHoursCount()).setParent(vb);

                Button btnDeleteSubject = new Button("Удалить");
                btnDeleteSubject.setParent(vb);
                btnDeleteSubject.addEventListener(Events.ON_CLICK, event -> {
                    if (subjectService.deleteSubjectByLGSS(data.getIdLGSS())) {
                        PopupUtil.showInfo("Предмет успешно удален");
                        update.run();
                    } else {
                        PopupUtil.showError("Не удалось удалить предмет");
                    }
                    popup.detach();
                });
                li.setPopup(popup);
                //li.setPopup("popup" + data.getIdLGSS() + li.getListbox().getUuid());
            }
        }
    }
}
