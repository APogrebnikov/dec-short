package org.edec.newMine.orders.ctrl;

import org.edec.newMine.orders.ctrl.renderer.OrdersInfoRenderer;
import org.edec.newMine.orders.manager.OrderEsoManager;
import org.edec.newMine.orders.model.OrderActionsModel;
import org.edec.newMine.orders.service.OrderService;
import org.edec.newMine.orders.service.impl.OrdersServiceImpl;
import org.edec.utility.constants.ScholarshipTypeConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;

public class WinOrdersInfo extends SelectorComposer<Component> {
    public static final String ID_ORDER_MINE = "idOrderMine";

    @Wire
    Window winOrdersInfo;
    @Wire
    Vbox vbOrdersInfo;

    private OrderService service = new OrdersServiceImpl();
    private List<OrderActionsModel> listOrdersAction = new ArrayList<>();
    private Long idOrderMine;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        idOrderMine = (Long) Executions.getCurrent().getArg().get(ID_ORDER_MINE);
        listOrdersAction = service.getOrdersActions(idOrderMine);

        for (OrderActionsModel orderActionsModel : listOrdersAction) {
            Label lAction = new Label(orderActionsModel.getOrdersAction());
            lAction.setStyle("font-size: 16px;  font-weight: 400;");
            vbOrdersInfo.appendChild(lAction);

            Listbox lbStudents = new Listbox();
            vbOrdersInfo.appendChild(lbStudents);
            lbStudents.setVflex("1");
            lbStudents.setHflex("1");

            lbStudents.appendChild(new Listhead());
            lbStudents.getListhead().appendChild(createListheader("3", "ФИО студента" ));
            lbStudents.getListhead().appendChild(createListheader("1", "Курс"));
            lbStudents.getListhead().appendChild(createListheader("1", "Группа"));
            lbStudents.getListhead().appendChild(createListheader("2", "Зачетная книжка"));
            lbStudents.getListhead().appendChild(createListheader("1", "Сумма"));

            lbStudents.setModel(new ListModelList<>(orderActionsModel.getListStudents()));
            lbStudents.setItemRenderer(new OrdersInfoRenderer());
            lbStudents.renderAll();

        }
    }

    private Listheader createListheader(String hflex, String label) {
        Listheader lh = new Listheader();
        lh.setHflex(hflex);
        lh.setLabel(label);
        lh.setStyle(" color: #fff; font-size: 13px;  font-weight: 700;");
        return  lh;
    }

    @Listen("onClick = #btnUpdateESO")
    public void updateESO () {
        OrderEsoManager oem = new OrderEsoManager();
        for (OrderActionsModel orderActionsModel : listOrdersAction) {
            oem.updateScholarshipDatesFromExport(ScholarshipTypeConst.ACADEMIC, orderActionsModel.getListStudents());
        }
    }

}
