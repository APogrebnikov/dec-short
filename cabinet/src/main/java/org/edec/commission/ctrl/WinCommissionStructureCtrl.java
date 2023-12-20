package org.edec.commission.ctrl;

import org.edec.commission.service.CommissionService;
import org.edec.commission.service.impl.CommissionServiceESOimpl;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * Created by dmmax
 */
public class WinCommissionStructureCtrl extends SelectorComposer<Component> {
    public static final String ID_COMMISSION = "id_commission";

    @Wire
    private Listbox lbCommissionStructure;

    private CommissionService commissionService = new CommissionServiceESOimpl();

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);
        Long idComm = (Long) Executions.getCurrent().getArg().get(ID_COMMISSION);
        lbCommissionStructure.setModel(new ListModelList<>(commissionService.getCommissionStructure(idComm)));
        lbCommissionStructure.renderAll();
    }
}
