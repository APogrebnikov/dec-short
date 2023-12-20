package org.edec.kcp.ctrl;

import org.edec.admin.model.RoleModel;
import org.edec.kcp.model.KCPFullModel;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class WinKCPInfoCtrl extends CabinetSelector {
    public static final String MAIN_PAGE = "main_page";
    public static final String KCP_INFO = "kcp_info";

    @Wire
    private Window winKCPInfo;
    @Wire
    Label lbDirection, lbYear, lbTotal;
    @Wire
    Textbox tbEditKCPBudget, tbEditKCPDogovor;

    KCPFullModel kcpFullModel;
    IndexPageCtrl indexPageCtrl;

    @Override
    protected void fill() throws InterruptedException {
        indexPageCtrl = (IndexPageCtrl) Executions.getCurrent().getArg().get(MAIN_PAGE);
        kcpFullModel = (KCPFullModel) Executions.getCurrent().getArg().get(KCP_INFO);
        if (kcpFullModel != null) {
            lbDirection.setValue(kcpFullModel.getCode()+" "+kcpFullModel.getDirection());
            lbYear.setValue(kcpFullModel.getStartyear());
            Integer kcpBudget = kcpFullModel.getKcpBudget() == null ? 0 : kcpFullModel.getKcpBudget();
            Integer kcpDogovor = kcpFullModel.getKcpDogovor() == null ? 0 : kcpFullModel.getKcpDogovor();
            Integer kcpTotal = kcpBudget + kcpDogovor;
            lbTotal.setValue(kcpTotal.toString());
            tbEditKCPBudget.setValue(kcpBudget.toString());
            tbEditKCPDogovor.setValue(kcpDogovor.toString());
        }
    }

    @Listen("onClick = #btnSaveEditKCP")
    public void saveKCP() {
        // TODO: Сохраняем значения КЦП во всех возможных curriculum (возможны дубликаты)
        indexPageCtrl.updateKCP(kcpFullModel);
        winKCPInfo.detach();
    }

    @Listen("onClick = #btnCancel")
    public void closeWindows() {
       winKCPInfo.detach();
    }

    @Listen("onChanging = #tbEditKCPDogovor;")
    public void changingKCPDogovor(InputEvent event) {
        Integer kcpBudget = (tbEditKCPBudget.getValue() == null || tbEditKCPBudget.getValue().equals("") )? 0 : Integer.valueOf(tbEditKCPBudget.getValue());
        Integer kcpDogovor = (event.getValue() == null || event.getValue().equals("")) ? 0 : Integer.valueOf(event.getValue());
        Integer kcpTotal = kcpBudget + kcpDogovor;
        kcpFullModel.setKcpDogovor(kcpDogovor);
        kcpFullModel.setKcpTotal(kcpTotal);
        lbTotal.setValue(kcpTotal.toString());
    }

    @Listen("onChanging = #tbEditKCPBudget;")
    public void changingKCPBudget(InputEvent event) {
        Integer kcpBudget = (event.getValue() == null || event.getValue().equals(""))? 0 : Integer.valueOf(event.getValue());
        Integer kcpDogovor = (tbEditKCPDogovor.getValue() == null || tbEditKCPDogovor.getValue().equals("")) ? 0 : Integer.valueOf(tbEditKCPDogovor.getValue());
        Integer kcpTotal = kcpBudget + kcpDogovor;
        kcpFullModel.setKcpBudget(kcpBudget);
        kcpFullModel.setKcpTotal(kcpTotal);
        lbTotal.setValue(kcpTotal.toString());
    }
}
