package org.edec.curriculumScan.ctrl.renderer;

import org.edec.model.GroupModel;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import org.edec.curriculumScan.model.Subject;
/**
 * Created by alex
 */
public class SubjectLbRenderer implements ListitemRenderer<Subject> {

    @Override
    public void render (Listitem li, Subject data, int index) throws Exception {
        li.setValue(data);
        // Сем
        new Listcell(data.getSemesterNumber().toString()).setParent(li);
        // Цикл
        new Listcell(data.getCicleCode()).setParent(li);
        // Код
        new Listcell(data.getCode()).setParent(li);
        // Название
        new Listcell(data.getName()).setParent(li);
        // Часы (все)
        new Listcell(data.getAllHours().toString()).setParent(li);
        // Лек
        new Listcell(data.getLecHours().toString()).setParent(li);
        // Лаб
        new Listcell(data.getLabHours().toString()).setParent(li);
        // Практ
        new Listcell(data.getPraHours().toString()).setParent(li);
        // СРС
        new Listcell(data.getKsrHours().toString()).setParent(li);
        // Кафедра
        new Listcell(data.getChairCode()==null ? "" : data.getChairCode().toString()).setParent(li);
    }
}
