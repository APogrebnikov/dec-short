package org.edec.secretaryChair.ctrl.renderer;

import javafx.scene.control.ListCell;
import org.edec.secretaryChair.ctrl.IndexPageCtrl;
import org.edec.secretaryChair.ctrl.WinEditCommissionCtrl;
import org.edec.secretaryChair.ctrl.WinStudentCommissionCtrl;
import org.edec.secretaryChair.model.CommissionModel;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.report.service.jasperReport.JasperReportService;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CommissionRenderer implements ListitemRenderer<CommissionModel> {
    private IndexPageCtrl pageCtrl;

    public CommissionRenderer(IndexPageCtrl pageCtrl) {
        this.pageCtrl = pageCtrl;
    }

    @Override
    public void render(Listitem li, final CommissionModel data, int index) throws Exception {
        li.setValue(data);

        if (data.getStatus() == 0) {
            li.setStyle("background: #ccc;");
        } else if (data.getStatus() == 1) {
            li.setStyle("background: #fff;");
        } else if (data.getStatus() == 2) {
            li.setStyle("background: #99ff99;");
        }

        li.addEventListener(Events.ON_DOUBLE_CLICK, openEditCommission(data));
        Listcell lcName = new Listcell();
        lcName.setLabel(data.getSubjectName() + "(" + FormOfControlConst.getName(data.getFormOfControl()).getName() + ")");
        if (data.isCheckStatus()) {
            lcName.setImage("/imgs/okCLR.png");
            lcName.setTooltiptext("Оценки подтверждены учебным отделом");
        }
        lcName.setParent(li);
        new Listcell(data.getSemesterStr()).setParent(li);
        new Listcell(data.getClassroom() != null ? data.getClassroom() : "").setParent(li);
        new Listcell(data.getCommissionDate() != null ? DateConverter.convertTimestampToString(data.getCommissionDate()) : "").setParent(
                li);

        Listcell lcBtnStudent = new Listcell();
        lcBtnStudent.setParent(li);
        Button btnStudent = new Button("Просмотреть");
        btnStudent.setParent(lcBtnStudent);
        btnStudent.addEventListener(Events.ON_CLICK, openStudents(data));

        Listcell lcProtocol = new Listcell();
        lcProtocol.setParent(li);
        if (data.getCommissionDate() != null) {
            Button btnPdf = new Button("", "/imgs/pdf.png");
            btnPdf.setParent(lcProtocol);
            btnPdf.addEventListener(Events.ON_CLICK, showProtocol(data));
        }

        Listcell lcInfo = new Listcell();
        lcInfo.setParent(li);
        Button btnInfo = new Button("Открыть");
        btnInfo.setParent(lcInfo);
        btnInfo.addEventListener(Events.ON_CLICK, openEditCommission(data));
    }

    private EventListener<Event> showProtocol(final CommissionModel data) {
        return event -> new JasperReportService().getJasperReportProtocolCommission(data.getIdRegister()).showPdf();
    }

    private EventListener<Event> openStudents(final CommissionModel data) {
        return event -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(WinStudentCommissionCtrl.REGISTER_COMMISSION, data);
            ComponentHelper.createWindow("/secretaryChair/winStudent.zul", "winStudent", arg).doModal();
        };
    }

    private EventListener<Event> openEditCommission(final CommissionModel data) {
        return event -> {
            Map<String, Object> arg = new HashMap<>();
            arg.put(WinEditCommissionCtrl.COMMISSION_OBJ, data);
            arg.put(WinEditCommissionCtrl.COMMISSION_PAGE_CTRL, pageCtrl);
            ComponentHelper.createWindow("/secretaryChair/winEditCommission.zul", "winEditCommission", arg).doModal();
        };
    }
}