package org.edec.order.ctrl.visualCreate;

import org.edec.order.ctrl.WinCreateOrderCtrl;
import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.model.OrderRuleModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;


public abstract class VisualAbstract {
    protected static final String ORDER_MODEL = "order_model";

    protected Window winCreateOrder;
    protected IndexPageCtrlDelegate delegate;
    protected OrderRuleModel rule;
    protected Long sem;
    protected InstituteModel institute;
    protected FormOfStudy formOfStudy;

    public VisualAbstract (Window winCreateOrder, IndexPageCtrlDelegate delegate, OrderRuleModel rule, final Long sem,
                           final InstituteModel institute, final FormOfStudy formOfStudy) {
        this.winCreateOrder = winCreateOrder;
        this.delegate = delegate;
        this.rule = rule;
        this.sem = sem;
        this.institute = institute;
        this.formOfStudy = formOfStudy;
    }

    abstract public void draw (Component component);
}
