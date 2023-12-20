package org.edec.newOrder.ctrl;

import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

import java.util.Date;
import java.util.function.BiConsumer;

public class WinFillOrderInfoCtrl extends SelectorComposer<Component> {

    public static final String FILL_ORDER_INFO = "fill_order_info";
    public static final String ORDER_NUMBER = "order_number";
    public static final String ORDER_DATE = "order_date";

    @Wire
    private Datebox dbOrderDate;

    @Wire
    private Textbox tbOrderNumber;

    private BiConsumer<String, Date> fillOrderInfoCons;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        fillOrderInfoCons = (BiConsumer<String, Date>) Executions.getCurrent().getArg().get(FILL_ORDER_INFO);

        String orderNumber = (String) Executions.getCurrent().getArg().get(ORDER_NUMBER);
        Date orderDate = (Date) Executions.getCurrent().getArg().get(ORDER_DATE);

        if(orderNumber!=null){
            tbOrderNumber.setText(orderNumber);
        }

        if(orderDate!=null){
            dbOrderDate.setValue(orderDate);
        }
    }

    @Listen("onClick = #btnSync")
    public void syncOrder(){
        if(tbOrderNumber.getValue().isEmpty() || dbOrderDate.getValue() == null){
            PopupUtil.showWarning("Заполните номер и дату приказа!");
            return;
        }

        fillOrderInfoCons.accept(tbOrderNumber.getValue(), dbOrderDate.getValue());
    }
}
