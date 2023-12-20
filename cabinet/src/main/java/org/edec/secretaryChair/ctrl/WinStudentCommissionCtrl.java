package org.edec.secretaryChair.ctrl;

import org.edec.secretaryChair.model.CommissionModel;
import org.edec.secretaryChair.service.SecretaryChairService;
import org.edec.secretaryChair.service.impl.SecretaryChairImpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

/**
 * @author Max Dimukhametov
 */
public class WinStudentCommissionCtrl extends SelectorComposer<Component> {
    public static final String REGISTER_COMMISSION = "register_commission";

    @Wire
    private Listbox lbShowStudentCommission;

    private SecretaryChairService chairService = new SecretaryChairImpl();

    private CommissionModel commission;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        commission = (CommissionModel) Executions.getCurrent().getArg().get(REGISTER_COMMISSION);
        Clients.showBusy(lbShowStudentCommission, "Загрузка данных");
        Events.echoEvent("onLater", lbShowStudentCommission, null);
    }

    @Listen("onLater = #lbShowStudentCommission")
    public void onLaterShowStudentCommission () {
        lbShowStudentCommission.setModel(new ListModelList<>(chairService.getStudentByCommission(commission.getId())));
        lbShowStudentCommission.renderAll();
        Clients.clearBusy(lbShowStudentCommission);
    }
}