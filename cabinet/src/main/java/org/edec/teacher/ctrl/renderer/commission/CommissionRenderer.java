package org.edec.teacher.ctrl.renderer.commission;

import org.apache.commons.lang3.time.DateUtils;
import org.edec.teacher.ctrl.WinCommissionCtrl;
import org.edec.teacher.model.commission.CommissionModel;
import org.edec.utility.DateUtility;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfControlConst;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CommissionRenderer implements ListitemRenderer<CommissionModel> {
    private ComponentService componentService = new ComponentServiceESOimpl();
    private Runnable updateIndex;

    public CommissionRenderer (Runnable updateIndex) {
        this.updateIndex = updateIndex;
    }

    @Override
    public void render (Listitem li, final CommissionModel data, int index) throws Exception {
        li.setValue(data);
        componentService.createListcell(li, String.valueOf(index + 1), "color: #000;", "", "");
        Listcell lcName = new Listcell();
        lcName.setParent(li);
        if (data.isCheckStatus()) {
            lcName.setImage("/imgs/okCLR.png");
            lcName.setTooltiptext("Оценки подтверждены учебным отделом");
        }
        lcName.setLabel(data.getSubjectName());
        //componentService.createListcell(li, data.getSubjectName(), "color: #000;", "", "");
        componentService.createListcell(li, new SimpleDateFormat("dd.MM.yyyy").format(data.getDateOfCommission()) + ",  " + data.getTimeCom(), "color: #000;", "", "");
        componentService.createListcell(li, data.getClassroom(), "color: #000;", "", "");
        componentService.createListcell(li, FormOfControlConst.getName(data.getFormOfControl()).getName(), "color: #000;", "", "");

        Listcell lcBtn = new Listcell();
        lcBtn.setParent(li);
        lcBtn.setStyle("text-align: center;");
        Button btn = new Button("", "/imgs/docs.png");
        btn.setParent(lcBtn);
        btn.setTooltiptext("Просмотреть ведомость");
        btn.setHoverImage("/imgs/docsCLR.png");
        btn.addEventListener(Events.ON_CLICK, event -> {
            Map arg = new HashMap();
            arg.put(WinCommissionCtrl.SELECTED_COMMISSION, data);
            arg.put(WinCommissionCtrl.UPDATE_INDEX, updateIndex);

            ComponentHelper.createWindow("/teacher/winCommissionRegister.zul", "winCommissionRegister", arg).doModal();
        });

       // li.setStyle("background: #" + (data.isSigned() ? "99FF99" : "FFF"));
        // Подписана
        if (data.isSigned()){
            li.setStyle("background: #99FF99");
        }
        // Подписана без ЭЦП
        if (data.isSigned() && data.getCertnumber() != null && data.getCertnumber().equals("  ")){
            li.setStyle("background: #bff0b6");
        }
        // Не подписана, но время есть (+1 день)
        if (!data.isSigned() && DateUtility.isSameDayOrAfter(DateUtils.addDays(data.getDateOfCommission(),1), new Date())){
            li.setStyle("background: #eedc82");
        }
        // Просрочена
        if (!data.isSigned() && DateUtility.isSameDayOrAfter(new Date(), DateUtils.addDays(data.getDateOfCommission(),2))){
            li.setStyle("background: #FF7373");
        }
    }
}
