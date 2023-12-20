package org.edec.order.ctrl;

import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.ctrl.visualCreate.*;
import org.edec.order.model.*;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.util.*;


public class WinCreateOrderCtrl extends SelectorComposer<Component> {
    public static final String DELEGATE = "delegate";
    public static final String FORM_OF_STUDY = "formOfStudy";
    public static final String INSTITUTE_MODEL = "institute_model";
    public static final String ORDER_TYPE = "order_type";

    @Wire
    private Combobox cmbListOrderTypes;

    @Wire
    private Window winCreateOrder;

    @Wire
    private Vbox container;

    private WinCreateOrderCtrl ctrl = this;

    private List<OrderRuleModel> listRules;
    private CreateOrderService service = new CreateOrderServiceESO();
    private IndexPageCtrlDelegate delegate;
    private FormOfStudy formOfStudy;
    private InstituteModel selectedInst;
    private Long currentSem;

    @Override
    public void doAfterCompose (Component comp) throws Exception {
        super.doAfterCompose(comp);

        delegate = (IndexPageCtrlDelegate) Executions.getCurrent().getArg().get(DELEGATE);
        selectedInst = (InstituteModel) Executions.getCurrent().getArg().get(INSTITUTE_MODEL);
        formOfStudy = (FormOfStudy) Executions.getCurrent().getArg().get(FORM_OF_STUDY);

        currentSem = service.getCurrentSemester(selectedInst.getIdInst(), formOfStudy.getType());
        listRules = service.getOrderRulesByInstitute(selectedInst.getIdInst());

        cmbListOrderTypes.setModel(new ListModelList<OrderRuleModel>(listRules));
        cmbListOrderTypes.setItemRenderer(new ComboitemRenderer<OrderRuleModel>() {
            @Override
            public void render (Comboitem comboitem, OrderRuleModel orderRuleModel, int i) throws Exception {
                comboitem.setValue(orderRuleModel);
                comboitem.setLabel(orderRuleModel.getName());
            }
        });

        cmbListOrderTypes.addEventListener("onChange", new EventListener<Event>() {
            @Override
            public void onEvent (Event event) throws Exception {
                switch (OrderTypeConst.getByType(listRules.get(cmbListOrderTypes.getSelectedIndex()).getIdOrderType())) {
                    case ACADEMIC:
                        new VisualAcademical(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                    case TRANSFER:
                        new VisualTransfer(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                    case DEDUCTION:
                        new VisualDeduction(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                    case SOCIAL:
                        new VisualSocial(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                    case SOCIAL_INCREASED:
                        new VisualSocialIncreased(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                    case SET_ELIMINATION_DEBTS:
                        new VisualSetElimiantionDebts(
                                winCreateOrder, delegate, listRules.get(cmbListOrderTypes.getSelectedIndex()), currentSem, selectedInst,
                                formOfStudy
                        ).draw(container);
                        break;
                }
            }
        });

        container.setVisible(true);
    }
}
