package org.edec.mine.ctrl.curriculumLine;

import org.edec.cloud.sync.mine.api.curriculumLine.data.response.CurriculumLineResponse;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class WinCurriculumLineAdditionalInfo extends Window {

    private Grid grid;

    private CurriculumLineResponse curriculumLine;

    public WinCurriculumLineAdditionalInfo(CurriculumLineResponse curriculumLine) {

        this.curriculumLine = curriculumLine;
        fillDefaultSettings();
        createGrid();
        fillContent();
    }

    private void fillDefaultSettings() {

        setTitle("Подробная информация о предмете из Шахт");
        setClosable(true);
        setWidth("50%");
        setHeight("60%");
    }

    private void createGrid() {

        grid = new Grid();
        grid.setParent(this);
        grid.setHflex("1");
        grid.setVflex("1");

        Rows rows = new Rows();
        rows.setParent(grid);
    }

    private void fillContent() {

        Rows rows = grid.getRows();

        createRow("Предмет", curriculumLine.getSubjectName()).setParent(rows);
        createRow("Код предмета", curriculumLine.getSubjectCode()).setParent(rows);
        createRow("Часы всего", String.valueOf(curriculumLine.getHoursCount())).setParent(rows);
        createRow("Часы аудиторных", String.valueOf(curriculumLine.getHoursAudCount())).setParent(rows);
        createRow("Часы лекционных", String.valueOf(curriculumLine.getHoursLection())).setParent(rows);
        createRow("Часы практики", String.valueOf(curriculumLine.getHoursPractice())).setParent(rows);
        createRow("Часы лабораторных", String.valueOf(curriculumLine.getHoursLabor())).setParent(rows);
        createRow("Факультатив", curriculumLine.getIsFacultative() ? "Да" : "Нет").setParent(rows);
        createRow("Практика", curriculumLine.getIsPracticeType() ? "Да" : "Нет").setParent(rows);
        createRow("ФК", curriculumLine.focString()).setParent(rows);
    }

    private Row createRow(String propertyName, String propertyValue) {

        Row row = new Row();

        new Label(propertyName).setParent(row);
        new Label(propertyValue).setParent(row);

        return row;
    }
}
