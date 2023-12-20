package org.edec.newOrder.ctrl;

import org.edec.newOrder.component.ListboxDocument;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.newOrder.service.orderCreator.concept.OrderServiceFactory;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.ArrayList;
import java.util.List;

public class WinRegenerateDocumentsCtrl extends CabinetSelector {
    public static final int ORDER_MODEL = 1;

    private ListboxDocument lbDocuments;

    @Wire
    private Window winRegenerateDocuments;

    @Wire
    private Vbox vboxLbDocumentContainer;

    private List<XulElement> documentParamElements = new ArrayList<>();
    private OrderEditModel orderEditModel;

    private ComponentProvider componentProvider = new ComponentProvider();
    private OrderService orderService;

    @Override
    protected void fill () {
        orderEditModel = (OrderEditModel) Executions.getCurrent().getArg().get(ORDER_MODEL);

        orderService = OrderServiceFactory.getServiceByRule(orderEditModel.getIdOrderRule(), null, null);
        lbDocuments = new ListboxDocument(orderService);
        lbDocuments.setParent(vboxLbDocumentContainer);
    }

    @Listen("onClick = #btnRegenerateDocuments")
    public void onClickBtnRegenerateDocuments () {
        List<Object> valueDocumentParams = lbDocuments.getParams();
        orderService.createAndAttachOrderDocuments(valueDocumentParams, null, orderEditModel);
        getSelf().detach();
    }
}
