package org.edec.newMine.groups.ctrl.render;

import org.edec.utility.component.model.SemesterModel;
import org.edec.newMine.groups.model.GroupsModel;
import org.edec.newMine.groups.service.GroupsService;
import org.edec.newMine.groups.service.impl.GroupsServiceImpl;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GroupsRenderer implements ListitemRenderer<GroupsModel> {
    Runnable refreshGrupsListbox;
    Boolean isMineGroup;
    InstituteModel institute;
    SemesterModel semester;

    public GroupsRenderer(Runnable refreshGrupsListbox, Boolean isMineGroup, SemesterModel semester, InstituteModel institute) {
        this.refreshGrupsListbox = refreshGrupsListbox;
        this.isMineGroup = isMineGroup;
        this.semester = semester;
        this.institute = institute;
    }

    GroupsService groupsService = new GroupsServiceImpl();

    private final Pattern pattern = Pattern.compile("((\\d{2,}\\.)+(\\d{2,})?){1}(.)*");

    @Override
    public void render(Listitem li, GroupsModel data, int i) throws Exception {
        li.setValue(data);

        Listcell lcGroupname = new Listcell(data.getGroupname() != null ? (i + 1 + ". " + data.getGroupname()) : i + 1 + ". ");
        lcGroupname.setParent(li);
        new Listcell(data.getCourse() != null ? data.getCourse().toString() : " ").setParent(li);
        new Listcell(data.getPlanfileName() != null ? data.getPlanfileName() : " ").setParent(li);
        new Listcell(data.getDirectionCode() != null ? data.getDirectionCode() : " ").setParent(li);
        new Listcell(data.getIdGroupMine() == null ? "" : String.valueOf(data.getIdGroupMine())).setParent(li);

        if (data.getLinkedGroup() != null) {
            GroupsModel otherGroup = data.getLinkedGroup();

            if (data.getIdGroupMine() != null && data.getIdGroupMine().equals(otherGroup.getIdGroupMine()) &&
                    otherGroup.getIdChairMine() != null && data.getIdChairMineCabinet() != null
                    && data.getIdChairMineCabinet() == otherGroup.getIdChairMine().longValue()) {
                li.setStyle("background: #99ff99;");
            } else {
                li.setStyle("background: #99ff99;");
                //Группа из АСУ ИКИТ
                if (data.getIdLGS() != null && (data.getIdChairMineCabinet() != otherGroup.getIdChairMine().longValue()
                        || data.getPlanfileName() == null || data.getIdGroupMine() == null)) {
                    li.setStyle("background: #FFFE7E;"); // желтый
                    Popup popupChair = new Popup();
                    popupChair.setParent(lcGroupname);
                    popupChair.setId("popupChair" + data.getIdLGS() + li.getListbox().getUuid() + "idChair");
                    li.setPopup("popupChair" + data.getIdLGS() + li.getListbox().getUuid() + "idChair");

                    Vbox vbox = new Vbox();
                    vbox.setParent(popupChair);
                    new Label("ИД выпускающей кафедры поменяется C " + data.getIdChairMineCabinet() + " " + data.getChairName()
                            + "\nНА " + otherGroup.getIdChairMine() + " " + otherGroup.getChairName()).setParent(vbox);
                    new Label("\n Название файла плана поменяется с " + data.getPlanfileName() + " на " + otherGroup.getPlanfileName()).setParent(vbox);
                    new Label("\n idШахт поменяются с " + data.getIdGroupMine() + " на " + otherGroup.getIdGroupMine()).setParent(vbox);
                    Button btnUpdateChair = new Button("Изменить свойства группы");
                    btnUpdateChair.setParent(vbox);
                    btnUpdateChair.addEventListener(Events.ON_CLICK, event -> {
                        if (groupsService.updateChair(data.getIdCurriculum(), otherGroup.getIdChairMine())
                                && groupsService.updatePlanFileName(data.getIdCurriculum(), otherGroup.getPlanfileName())
                                && groupsService.updateGroup(data.getIdLGS(), otherGroup.getIdGroupMine())) {
                            refreshGrupsListbox.run();
                            PopupUtil.showInfo("Свойства группы были успешно изменены!");
                        }
                    });

                }
            }
        } else {
            li.setStyle("background: #FF7373;"); // красный
            //Группа из шахт
            if (data.getIdLGS() == null) {
                Popup popup = new Popup();
                popup.setParent(lcGroupname);
                popup.setId("popup" + data.getIdGroupMine() + li.getListbox().getUuid());
                li.setPopup("popup" + data.getIdGroupMine() + li.getListbox().getUuid());

                Vbox vb = new Vbox();
                vb.setParent(popup);

                new Label("Название: " + data.getGroupname()).setParent(vb);
                new Label("ИД группы: " + data.getIdGroupMine()).setParent(vb);
                new Label("Курс: " + data.getCourse()).setParent(vb);
                new Label("Специальность: " + data.getDirectionCode() + ", " + data.getSpecialityTitle()).setParent(vb);
                new Label("Кафедра: ид шахт(" + data.getIdChairMine() + ") " + data.getChairName()).setParent(vb);
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
                    groupsService.createGroup(data, institute.getIdInst(), semester);
                    refreshGrupsListbox.run();
                });
            }
        }
    }
}
