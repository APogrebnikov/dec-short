package org.edec.commission.ctrl.renderer;

import org.edec.commission.ctrl.IndexPageCtrl;
import org.edec.commission.ctrl.WinShowCommissionStudentPageCtrl;
import org.edec.commission.model.StudentDebtModel;
import org.edec.commission.model.SubjectDebtModel;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmmax
 */
public class SubjectDebtRenderer implements ListitemRenderer<SubjectDebtModel> {
    private IndexPageCtrl indexPageCtrl;

    public SubjectDebtRenderer (IndexPageCtrl indexPageCtrl){
        this.indexPageCtrl = indexPageCtrl;
    }
    @Override
    public void render (Listitem li, SubjectDebtModel data, int index) throws Exception {
        li.setValue(data);

        Label label = new Label();
        label.setValue(" (Есть отриц. оценки!)");
        label.setStyle("color: #FF4500");
        Listcell lcSubject = new Listcell(data.getSubjectname());
        for (StudentDebtModel student : data.getStudents()) {
            if ((student.getRating() < 0 || student.getRating() == 2) && data.isSigned()) {
                lcSubject.appendChild(label);
            }
        }
        lcSubject.setParent(li);
        new Listcell(data.getFocStr()).setParent(li);
        new Listcell(data.getFulltitle()).setParent(li);
        new Listcell(data.getSemesterStr()).setParent(li);
        new Listcell(data.getDateComission() == null
                     ? "Не назначена"
                     : new SimpleDateFormat("dd.MM.yyyyг. HH:mm").format(data.getDateComission())).setParent(li);
        new Listcell(data.getClassroom()).setParent(li);
        new Listcell(data.getCheckedcount() + "/" + data.getCountstudent()).setParent(li);
        li.setStyle("background: #" +
                    (data.getDateComission() != null ? (data.getDateComission().after(new Date()) ? "fff;" : "ffcccc;") : "ccc;"));
        if (data.isSigned()) {
            li.setStyle("background: #99ff99;");
        }

        li.addEventListener(Events.ON_CLICK, event -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(WinShowCommissionStudentPageCtrl.SELECTED_LISTITEM, li);
            arg.put(WinShowCommissionStudentPageCtrl.COMMISSION_CTRL, indexPageCtrl);

            ComponentHelper.createWindow("winShowCommissionStudent.zul", "winShowCommissionStudent", arg).doModal();
        });
    }
}
