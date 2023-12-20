package org.edec.synchroMine.ctrl.renderer;

import org.edec.synchroMine.model.dao.SubjectGroupModel;
import org.edec.synchroMine.service.GroupSubjectService;
import org.edec.synchroMine.service.impl.GroupSubjectImpl;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;
import java.util.Objects;


public class GroupSubjecRenderer implements ListitemRenderer<SubjectGroupModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private GroupSubjectService groupSubjectService = new GroupSubjectImpl();

    private Long idLGS;
    private Runnable update;

    public GroupSubjecRenderer(Long idLGS, Runnable update) {
        this.idLGS = idLGS;
        this.update = update;
    }

    @Override
    public void render(Listitem li, SubjectGroupModel data, int index) throws Exception {
        li.setValue(data);
        if (li.getListbox().getListhead().getChildren().size() == 0) {
            createListheader(li.getListbox().getListhead());
        }

        Listcell lcCode = new Listcell(data.getSubjectcode());
        lcCode.setParent(li);

        Listcell lcSubjectname = new Listcell(data.getSubjectname());
        lcSubjectname.setParent(li);
        new Listcell(data.getExam() && data.getType() == 1 ? "диф" : "", data.getExam() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell(data.getPass() && data.getType() == 1 ? "диф" : "", data.getPass() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell(data.getCp() && data.getType() == 1 ? "диф" : "", data.getCp() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell(data.getCw() && data.getType() == 1 ? "диф" : "", data.getCw() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell(data.getPractic() && data.getType() == 1 ? "диф" : "", data.getPractic() ? "/imgs/okCLR.png" : "").setParent(li);
        new Listcell(data.getIdSubjMine() == null ? "" : String.valueOf(data.getIdSubjMine())).setParent(li);

        if (data.getOtherSubject() != null) {
            SubjectGroupModel otherSubject = data.getOtherSubject();
            String subjectname = data.getSubjectname(), subjectnameOther = otherSubject.getSubjectname();
            String subjectcode = data.getSubjectcode(), subjectcodeOther = otherSubject.getSubjectcode();
            Long idSubjMine = data.getIdSubjMine(), idSubjMineOther = otherSubject.getIdSubjMine();
            Double hoursCount = data.getHoursCount(), otherHoursCount = otherSubject.getHoursCount();
            Long idChairMine = data.getIdChairMine();
            //Если совпадают и ид шахт и название, то оставить так
            if (idSubjMine != null && idSubjMineOther != null && subjectcode!=null && idSubjMine.equals(idSubjMineOther)
                    && subjectcode.equals(subjectcodeOther) &&
                subjectname.toLowerCase().equals(subjectnameOther.toLowerCase()) && Objects.deepEquals(hoursCount, otherHoursCount)) {
                li.setStyle("background: #99ff99;");
            }
            //Если не совпадают ИД шахт или название, то можно обновить данные у нас в системе
            else {
                li.setStyle("background: #FFFE7E;");
                //Если предмет из АСУ ИКИТ
                if (idChairMine == null) {
                    Popup popup = new Popup();
                    popup.setParent(lcSubjectname);
                    popup.setId("popup" + data.getSubjectname() + li.getListbox().getUuid());

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Название: АСУ ИКИТ - '" + subjectname + "'").setParent(vb);
                    new Label("Название: шахты - '" + subjectnameOther + "'").setParent(vb);
                    new Label("Идентификатор: АСУ ИКИТ - '" + String.valueOf(idSubjMine) + "'").setParent(vb);
                    new Label("Идентификатор: шахты - '" + String.valueOf(idSubjMineOther) + "'").setParent(vb);
                    new Label("Код: АСУ ИКИТ - '" + subjectcode + "'").setParent(vb);
                    new Label("Код: шахты - '" + subjectcodeOther + "'").setParent(vb);
                    new Label("Часы у нас: " + hoursCount + ", в шахтах: " + otherHoursCount).setParent(vb);
                    Button btnUpdate = new Button("Обновить");
                    btnUpdate.setParent(vb);
                    btnUpdate.addEventListener(Events.ON_CLICK, event -> {
                        if (otherSubject.getIdChair() == null) {
                            PopupUtil.showError("Невозможно определить кафедру, принадлежащая предмету");
                            return;
                        }
                        List<String> registers = groupSubjectService.getRegisterNumberByLGSandSubj(idLGS, data.getIdSubj());
                        if (registers.size() != 0) {
                            PopupUtil.showWarning("Нельзя обновить предмет, потому что есть не закрытые ведомости: " + registers.toString());
                            return;
                        }
                        if (groupSubjectService.updateSubject(data, otherSubject)) {
                            PopupUtil.showInfo("Обновление предмета прошло успешно!");
                            update.run();
                        } else {
                            Messagebox.show("Обновить предмет не удалось!", "Ошибка", Messagebox.OK, Messagebox.ERROR);
                        }
                    });

                    li.setPopup("popup" + data.getSubjectname() + li.getListbox().getUuid());
                }
            }
        } else {
            li.setStyle("background: #FF7373;");
            //Если предмет из шахт, то мы можем его добавить
            if (data.getIdChairMine() != null) {
                Popup popup = new Popup();
                popup.setParent(lcSubjectname);
                popup.setId("popup" + data.getSubjectname() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("ИД кафедры: " + (data.getIdChair() == null ? "не найден" : String.valueOf(data.getIdChair()))).setParent(vb);
                new Label("ИД кафедры(шахты): " + data.getIdChairMine()).setParent(vb);

                Button btnAddSubject = new Button("Добавить предмет");
                btnAddSubject.setParent(vb);
                btnAddSubject.addEventListener(Events.ON_CLICK, event -> {
                    if (data.getIdChair() == null) {
                        Messagebox.show("Невозможно определить кафедедру у предмета, обратитесь к администратору!", "Ошибка", Messagebox.OK,
                                        Messagebox.ERROR
                        );
                        return;
                    }
                    if (groupSubjectService.createSubjectSRforLGS(idLGS, data)) {
                        PopupUtil.showInfo("Предмет успешно создан");
                        update.run();
                    } else {
                        PopupUtil.showError("Предмет не удалось создать");
                    }
                });
                li.setPopup("popup" + data.getSubjectname() + li.getListbox().getUuid());
            } else {
                Popup popup = new Popup();
                popup.setParent(lcSubjectname);
                popup.setId("popup" + data.getIdLGSS() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);
                vb.setStyle("width: 200px; height: 200px;");

                Button btnDeleteSubject = new Button("Удалить");
                btnDeleteSubject.setParent(vb);
                btnDeleteSubject.addEventListener(Events.ON_CLICK, event -> {
                   if (groupSubjectService.deleteSubjectByLGSS(data.getIdLGSS())) {
                       PopupUtil.showInfo("Предмет успешно удален");
                   } else {
                       PopupUtil.showError("Не удалось удалить предмет");
                   }
                });

                li.setPopup("popup" + data.getIdLGSS() + li.getListbox().getUuid());
            }
        }
    }

    private void createListheader(Listhead lh) {
        componentService.getListheader("Код", "", "", "").setParent(lh);
        componentService.getListheader("Название предмета", "", "1", "").setParent(lh);
        componentService.getListheader("Экз", "60px", "", "center").setParent(lh);
        componentService.getListheader("Зачет", "60px", "", "center").setParent(lh);
        componentService.getListheader("КП", "60px", "", "center").setParent(lh);
        componentService.getListheader("КР", "60px", "", "center").setParent(lh);
        componentService.getListheader("Практика", "70px", "", "center").setParent(lh);
        componentService.getListheader("ИД шахты", "80px", "", "center").setParent(lh);
    }
}