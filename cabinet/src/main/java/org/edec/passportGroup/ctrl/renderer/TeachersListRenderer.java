package org.edec.passportGroup.ctrl.renderer;

import antlr.debug.NewLineEvent;
import org.edec.passportGroup.model.TeacherModel;
import org.zkoss.zul.*;

import java.util.List;

/**
 * Created by ilyabaikalow on 20.11.17.
 */
public class TeachersListRenderer implements ListitemRenderer<TeacherModel> {
    private List<TeacherModel> teacherModelList;

    public TeachersListRenderer (List<TeacherModel> teacherModelList) {
        this.teacherModelList = teacherModelList;
    }

    // Заполнение строк
    private void fillCells (Listitem listitem, TeacherModel teacherModel, int i) {
        // Колонка для radio button
        Listcell checkCell = new Listcell();
        checkCell.setStyle("color: #000000;");
        checkCell.setSclass("pg_student_name");

        // Колонка "Преподаватель"
        Listcell teacherNameCell = new Listcell();
        Label lTeacherName = new Label(" " + (++i) + ". " + teacherModel.getFullName());
        lTeacherName.setStyle("color: #000000;");
        teacherNameCell.setSclass("pg_student_name");
        teacherNameCell.appendChild(lTeacherName);

        // Колонка "Институт"
        Listcell instNameCell = new Listcell();
        Label lInstName = new Label(teacherModel.getInstTitle());
        lInstName.setStyle("color: #000000;");
        instNameCell.setSclass("pg_student_name");
        instNameCell.appendChild(lInstName);

        // Колонка "Кафедра"
        Listcell depNameCell = new Listcell();
        Vbox vLabel = new Vbox();
        String label = "";
        for (int j = 0; j < teacherModel.getListDepTitles().size(); j++) {
            vLabel.appendChild(new Label(teacherModel.getListDepTitles().get(j)));
        }
        Label lDepName = new Label(label);
        lDepName.setStyle("color: #000000;");
        depNameCell.setSclass("pg_student_name");
        depNameCell.appendChild(vLabel);

        // Добавление
        listitem.appendChild(checkCell);
        listitem.appendChild(teacherNameCell);
        listitem.appendChild(instNameCell);
        listitem.appendChild(depNameCell);
    }

    //рэндеринг listitem
    @Override
    public void render (Listitem listitem, TeacherModel subjectModel, int i) throws Exception {
        fillCells(listitem, subjectModel, i);
    }
}
