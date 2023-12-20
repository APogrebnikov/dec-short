package org.edec.passportGroup.ctrl.renderer;

import org.edec.passportGroup.model.SemesterModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.SimpleDateFormat;


public class SemesterListRenderer implements ListitemRenderer<SemesterModel> {
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

    @Override
    public void render (Listitem listitem, SemesterModel semesterModel, int i) throws Exception {
        // Получение id семестра и передача его в атрибутом item списка
        listitem.setValue(semesterModel);

        // Определение учебного года
        String schoolYear =
                simpleDateFormat.format(semesterModel.getDateOfBegin()) + " - " + simpleDateFormat.format(semesterModel.getDateOfEnd());
        listitem.appendChild(new Listcell("  " + schoolYear));

        String semesterStatusText, formOfStudy;

        if (semesterModel.getSeason() == 0) {
            semesterStatusText = "осенний";
        } else if (semesterModel.getSeason() == 1) {
            semesterStatusText = "весенний";
        } else {
            semesterStatusText = "";
        }

        listitem.appendChild(new Listcell(semesterStatusText));

        // Определение формы обучения
        if (semesterModel.getFormOfStudy() == 1) {
            formOfStudy = "очная";
        } else if (semesterModel.getFormOfStudy() == 2) {
            formOfStudy = "заочная";
        } else {
            formOfStudy = "";
        }
        listitem.appendChild(new Listcell(formOfStudy));
    }
}
