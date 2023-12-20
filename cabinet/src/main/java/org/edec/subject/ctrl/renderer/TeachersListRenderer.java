package org.edec.subject.ctrl.renderer;

import org.edec.subject.model.TeacherModel;
import org.edec.subject.service.SubjectService;
import org.edec.subject.service.impl.SubjectServiceImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.List;

public class TeachersListRenderer implements ListitemRenderer<TeacherModel> {
    SubjectService service = new SubjectServiceImpl();

    private List<TeacherModel> teacherModelList;

    private Runnable filterTeacherList;

    public TeachersListRenderer(List<TeacherModel> teacherModelList, Runnable filterTeacherList) {
        this.teacherModelList = teacherModelList;
        this.filterTeacherList = filterTeacherList;
    }

    // Заполнение строк
    private void fillCells(Listitem listitem, TeacherModel teacherModel, int i) {

        // Колонка для radio button
        Listcell checkCell = new Listcell();
        checkCell.setStyle("color: #000000;");

        // Колонка "Преподаватель"
        Listcell teacherNameCell = new Listcell();
        Label lTeacherName = new Label(" " + (++i) + ". " + teacherModel.getFullName());
        lTeacherName.setStyle(((teacherModel.getHidden()) ? "color: #A4A4A4;" : "color: #000000;"));
        teacherNameCell.appendChild(lTeacherName);

        Listcell visibilityCell = new Listcell();
        Button btn = new Button(((teacherModel.getHidden()) ? "Отобразить" : "Скрыть"));
        btn.setWidth("120px");

        btn.addEventListener(Events.ON_CLICK, event -> {
            if (service.changeTeacherVisibility(teacherModel.getIdLed(), !teacherModel.getHidden()) && !teacherModel.getHidden()){
                PopupUtil.showInfo("Преподаватель успешно скрыт!");
            }
            filterTeacherList.run();
        });

        visibilityCell.appendChild(btn);

        listitem.appendChild(checkCell);
        listitem.appendChild(teacherNameCell);
        listitem.appendChild(visibilityCell);
    }

    @Override
    public void render(Listitem listitem, TeacherModel subjectModel, int i) throws Exception {
        fillCells(listitem, subjectModel, i);
    }
}