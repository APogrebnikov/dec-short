package org.edec.student.curriculum.renderer;

import org.edec.student.curriculum.model.SubjectCurriculumModel;
import org.zkoss.zul.*;

import java.util.Date;

public class StudentsCurriculumRenderer implements ListitemRenderer<SubjectCurriculumModel> {

    @Override
    public void render(Listitem listitem, SubjectCurriculumModel data, int i) throws Exception {
        Listcell lcSemesterNumber = new Listcell(data.getSemesterNumber() + " семестр");
        Listcell lcSubject = new Listcell(data.getSubjectname());
        lcSubject.setStyle("text-align: left");
        listitem.appendChild(lcSemesterNumber);
        listitem.appendChild(new Listcell(data.getCodeSubject()));
        listitem.appendChild(lcSubject);
        listitem.appendChild(new Listcell(data.getZE().toString()));
        listitem.appendChild(new Listcell(data.getHoursAud().toString()));
        listitem.appendChild(new Listcell(data.getHoursAll().toString()));
        Listcell lcFoc = new Listcell();
        Vbox vbFoc = new Vbox();
        if (!data.getFocList().isEmpty()) {
            for (String foc : data.getFocList()) {
                Label lfoc = new Label(foc);
                vbFoc.appendChild(lfoc);
            }
        } else {
            Label lfoc = new Label("-");
            vbFoc.appendChild(lfoc);
        }
        vbFoc.setStyle("text-align: left");
        lcFoc.appendChild(vbFoc);
        listitem.appendChild(lcFoc);

        if (data.getCheckSubject() == null) {
            listitem.setStyle("background : #cceeff;");
        } else if (data.getCheckSubject()) {
            listitem.setStyle("background : #99ff99;");
        } else if (data.getDateofendsemester() != null && !data.getCheckSubject() && data.getDateofendsemester().after(new Date())) {
            listitem.setStyle("background : #FF7373;");
        }
    }
}

