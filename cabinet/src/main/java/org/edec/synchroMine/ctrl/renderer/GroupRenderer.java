package org.edec.synchroMine.ctrl.renderer;

import org.apache.poi.ss.formula.functions.Even;
import org.edec.model.SemesterModel;
import org.edec.synchroMine.model.eso.GroupMineModel;
import org.edec.synchroMine.service.GroupSubjectService;
import org.edec.synchroMine.service.impl.GroupSubjectImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GroupRenderer implements ListitemRenderer<GroupMineModel> {
    private GroupSubjectService groupSubjectService = new GroupSubjectImpl();

    private InstituteModel institute;
    private SemesterModel semester;

    private final Pattern pattern = Pattern.compile("((\\d{2,}\\.)+(\\d{2,})?){1}(.)*");

    public GroupRenderer (InstituteModel institute, SemesterModel semester) {
        this.institute = institute;
        this.semester = semester;
    }

    @Override
    public void render (Listitem li, GroupMineModel data, int index) throws Exception {
        li.setValue(data);
        Listcell lcGroupname = new Listcell(data.getGroupname());
        lcGroupname.setParent(li);
        new Listcell(String.valueOf(data.getCourse())).setParent(li);
        new Listcell(data.getIdGroupMine() == null ? "" : String.valueOf(data.getIdGroupMine())).setParent(li);

        if (data.getLinkedGroup() != null) {
            GroupMineModel otherGroup = data.getLinkedGroup();

            if (data.getIdGroupMine() != null && data.getIdGroupMine().equals(otherGroup.getIdGroupMine()) &&
                    otherGroup.getIdChairMine() != null  && data.getIdChairMineCabinet() != null
                    && data.getIdChairMineCabinet() == otherGroup.getIdChairMine().longValue()) {
                li.setStyle("background: #99ff99;");
            } else {
                li.setStyle("background: #99ff99;");
                //Группа из АСУ ИКИТ
                if (data.getIdLGS() != null && data.getIdChairMineCabinet() != otherGroup.getIdChairMine().longValue()) {
                    li.setStyle("background: #ADD8E6;");
                    Popup popupChair = new Popup();
                    popupChair.setParent(lcGroupname);
                    popupChair.setId("popupChair" + data.getIdLGS() + li.getListbox().getUuid() + "idChair");
                    li.setPopup("popupChair" + data.getIdLGS() + li.getListbox().getUuid() + "idChair");

                    Vbox vbox = new Vbox();
                    vbox.setParent(popupChair);
                    new Label("ИД выпускающей кафедры поменяется C " + data.getIdChairMineCabinet() + " " + data.getChairName()
                            + "\nНА " + otherGroup.getIdChairMine() + " " + otherGroup.getChairName() ).setParent(vbox);
                    Button btnUpdateChair = new Button("Изменить вып. кафедру");
                    btnUpdateChair.setParent(vbox);
                    btnUpdateChair.addEventListener(Events.ON_CLICK, event -> {
                        if (groupSubjectService.updateChair(data.getIdCurriculum(), otherGroup.getIdChairMine())) {
                            PopupUtil.showInfo("Группа успешно поменяла выпускающую кафедру!");
                        }
                    });

                } else if (data.getIdLGS() != null) {
                    li.setStyle("background: #FFFE7E;");
                    Popup popup = new Popup();
                    popup.setParent(lcGroupname);
                    popup.setId("popup" + data.getIdLGS() + li.getListbox().getUuid());
                    li.setPopup("popup" + data.getIdLGS() + li.getListbox().getUuid());

                    Vbox vb = new Vbox();
                    vb.setParent(popup);

                    new Label("Название поменяется с " + data.getGroupname() + " на " + otherGroup.getGroupname()).setParent(vb);
                    new Label("ИД группы поменяется с " + String.valueOf(data.getIdGroupMine()) + " на " +
                              otherGroup.getIdGroupMine()).setParent(vb);
                    new Label("Курс поменяется с " + data.getCourse() + " на " + otherGroup.getCourse()).setParent(vb);
                    new Label("ИД кафедры из шахт: " + data.getIdChairMineCabinet()).setParent(vb);

                    Button btnAddGroup = new Button("Изменить группу");
                    btnAddGroup.setParent(vb);
                    btnAddGroup.addEventListener(Events.ON_CLICK, event -> {
                        if (groupSubjectService.updateGroup(data.getIdLGS(), otherGroup.getIdGroupMine())) {
                            PopupUtil.showInfo("Группа успешно поменяла свои свойства!");
                        }
                    });
                }
            }
        } else {
            li.setStyle("background: #FF7373;");
            //Группа из шахт
            if (data.getIdLGS() == null) {
                Popup popup = new Popup();
                popup.setParent(lcGroupname);
                popup.setId("popup" + data.getIdGroupMine() + li.getListbox().getUuid());
                li.setPopup("popup" + data.getIdGroupMine() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Название: " + data.getGroupname()).setParent(vb);
                new Label("ИД группы: " + String.valueOf(data.getIdGroupMine())).setParent(vb);
                new Label("Курс: " + data.getCourse()).setParent(vb);
                new Label("Специальность: " + data.getDirectionCode() + ", " + data.getSpecialityTitle()).setParent(vb);
                new Label("Кафедра: ид шахт(" + data.getIdChairMine() +") " +  data.getChairName()).setParent(vb);
                if (data.getDirectionTitle() != null) {
                    Matcher m = pattern.matcher(data.getDirectionTitle());
                    if (m.find()) {
                        new Label("Профиль (код): '" + data.getDirectionTitle().substring(0, m.end(1)) + "'").setParent(vb);
                        new Label("Профиль (название): '" + data.getDirectionTitle().substring(m.end(1)).trim() + "'").setParent(vb);
                    }
                }
                new Label("Период обучения: " + data.getPeriodOfStudy()).setParent(vb);
                new Label("Название файла: " + data.getPlanfileName()).setParent(vb);

                Button btnAddGroup = new Button("Добавить группу");
                btnAddGroup.setParent(vb);
                btnAddGroup.addEventListener(Events.ON_CLICK, event -> {
                    groupSubjectService.createGroup(data, institute.getIdInst(), semester);
                });
            }
        }
    }
}
