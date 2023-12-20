package org.edec.efficiency.ctrl.renderer;

import org.edec.efficiency.model.ProblemSubjectGroup;
import org.edec.efficiency.service.EfficiencyService;
import org.edec.efficiency.service.impl.EfficiencyImpl;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;


public class SubjectGroupRenderer implements ListitemRenderer<ProblemSubjectGroup> {
    private EfficiencyService efficiencyService = new EfficiencyImpl();

    @Override
    public void render (Listitem li, final ProblemSubjectGroup data, int index) throws Exception {
        li.setValue(data);
        new Listcell(data.getSubjectname()).setParent(li);
        new Listcell(data.getFoc()).setParent(li);
        new Listcell(data.getIdEok() == null ? "" : String.valueOf(data.getIdEok())).setParent(li);
        Listcell lcTeacher = new Listcell(data.getTeacher());
        lcTeacher.setParent(li);
        lcTeacher.setTooltiptext(data.getTeacher());

        final Listcell lcAttendance = new Listcell("", data.getAttendance() ? "/imgs/okCLR.png" : "");
        lcAttendance.setParent(li);
        lcAttendance.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
            if (efficiencyService.updateEfficiencySubject(data.getIdLGSS(), !data.getAttendance(), data.getEok(), data.getPerformance())) {
                data.setAttendance(!data.getAttendance());
                lcAttendance.setImage(data.getAttendance() ? "/imgs/okCLR.png" : "");
            } else {
                PopupUtil.showError("Не удалось обновить поле");
            }
        });

        final Listcell lcEok = new Listcell("", data.getEok() ? "/imgs/okCLR.png" : "");
        lcEok.setParent(li);
        lcEok.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
            if (efficiencyService.updateEfficiencySubject(data.getIdLGSS(), data.getAttendance(), !data.getEok(), data.getPerformance())) {
                data.setEok(!data.getEok());
                lcEok.setImage(data.getEok() ? "/imgs/okCLR.png" : "");
            } else {
                PopupUtil.showError("Не удалось обновить поле");
            }
        });

        final Listcell lcPerformance = new Listcell("", data.getPerformance() ? "/imgs/okCLR.png" : "");
        lcPerformance.setParent(li);
        lcPerformance.addEventListener(Events.ON_DOUBLE_CLICK, event -> {
            if (efficiencyService.updateEfficiencySubject(data.getIdLGSS(), data.getAttendance(), data.getEok(), !data.getPerformance())) {
                data.setPerformance(!data.getPerformance());
                lcPerformance.setImage(data.getPerformance() ? "/imgs/okCLR.png" : "");
            } else {
                PopupUtil.showError("Не удалось обновить поле");
            }
        });
    }
}
