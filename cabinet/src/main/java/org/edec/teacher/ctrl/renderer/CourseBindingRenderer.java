package org.edec.teacher.ctrl.renderer;

import org.edec.teacher.model.CourseHistoryModel;
import org.edec.teacher.model.EsoCourseModel;
import org.edec.teacher.model.GroupModel;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

public class CourseBindingRenderer implements ListitemRenderer<CourseHistoryModel> {

    @Override
    public void render (Listitem listitem, CourseHistoryModel courseHistoryModel, int i) throws Exception {
        listitem.setValue(courseHistoryModel);

        Listcell checkCell = new Listcell();
        checkCell.setStyle("color: #000000;");

        Listcell subjectNameCell = new Listcell();
        Label lSubjectName = new Label(courseHistoryModel.getSubjectname());
        lSubjectName.setStyle("color: #000000;");
        subjectNameCell.appendChild(lSubjectName);

        Listcell coursesCell = new Listcell();
        Combobox cmbCourses = new Combobox();
        cmbCourses.setWidth("100%");

        for (EsoCourseModel course : courseHistoryModel.getUsedCourses()) {
            cmbCourses.appendItem(course.getIdEsoCourse() + ". " + course.getFullname());
        }

        cmbCourses.setSelectedIndex(0);
        courseHistoryModel.getUsedCourses().get(0).setChecked(true);

        cmbCourses.addEventListener(Events.ON_CHANGE, event -> {
            for (EsoCourseModel course : courseHistoryModel.getUsedCourses()) {
                course.setChecked(false);
            }
            courseHistoryModel.getUsedCourses().get(cmbCourses.getSelectedIndex()).setChecked(true);
        });

        coursesCell.appendChild(cmbCourses);

        Listcell groupsCell = new Listcell();
        Combobox cmbGroups = new Combobox();
        cmbGroups.setWidth("100%");

        if (courseHistoryModel.getGroupList().size() > 1) {
            cmbGroups.appendItem("Все");

            for (GroupModel group : courseHistoryModel.getGroupList()) {
                group.setSelected(true);
            }
        } else {
            courseHistoryModel.getGroupList().get(0).setSelected(true);
        }

        for (GroupModel group : courseHistoryModel.getGroupList()) {
            cmbGroups.appendItem(group.getGroupname());
        }
        cmbGroups.setSelectedIndex(0);

        //помечаем выбранные в комбобоксах элементы, чтобы потом их вытащить в окне
        cmbGroups.addEventListener(Events.ON_CHANGE, event -> {
            if (cmbGroups.getItemCount() > 1 && cmbGroups.getSelectedIndex() == 0) {
                for (GroupModel group : courseHistoryModel.getGroupList()) {
                    group.setSelected(true);
                }
            } else {
                for (GroupModel group : courseHistoryModel.getGroupList()) {
                    group.setSelected(false);
                }
                courseHistoryModel.getGroupList().get(cmbGroups.getSelectedIndex() - 1).setSelected(true);
            }
        });

        groupsCell.appendChild(cmbGroups);

        listitem.appendChild(checkCell);
        listitem.appendChild(subjectNameCell);
        listitem.appendChild(coursesCell);
        listitem.appendChild(groupsCell);
    }
}
