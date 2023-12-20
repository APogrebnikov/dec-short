package org.edec.order.ctrl.visualCreate;

import org.edec.model.GroupModel;
import org.edec.order.ctrl.WinCreateOrderCtrl;
import org.edec.order.ctrl.WinDefineProlongationCtrl;
import org.edec.order.ctrl.WinDefineTransferFromDateCtrl;
import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.model.OrderModel;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.order.service.orderCreator.OrderCreator;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

import java.util.*;


public class VisualTransfer extends VisualAbstract {
    private CreateOrderService service = new CreateOrderServiceESO();
    public WinCreateOrderCtrl ctrl;

    public VisualTransfer(Window winCreateOrder, IndexPageCtrlDelegate delegate, OrderRuleModel rule, Long currentSem,
                          InstituteModel institute, FormOfStudy formOfStudy) {
        super(winCreateOrder, delegate, rule, currentSem, institute, formOfStudy);
    }

    @Override
    public void draw(Component component) {
        component.setVisible(true);
        while (component.getFirstChild() != null) {
            component.removeChild(component.getFirstChild());
        }

        final Vlayout vLayout = new Vlayout();

        final Hbox hbox1 = new Hbox();
        final Hbox hbox2 = new Hbox();

        final Label label1 = new Label(
                rule.getName().equals(OrderRuleConst.TRANSFER_PROLONGATION.getName()) ? "Продлить до " : "Перевести с ");
        final Label label2 = new Label(
                rule.getName().equals(OrderRuleConst.TRANSFER_PROLONGATION.getName()) ? "Дата служебной ЛАЗ" : "Cроки ликвидации по ");
        final Datebox dateTransfer = new Datebox();
        final Datebox dateLikvidation = new Datebox();
        dateTransfer.setValue(new Date());
        dateLikvidation.setValue(new Date());
        hbox1.appendChild(label1);
        hbox1.appendChild(dateTransfer);
        vLayout.appendChild(hbox1);

        if (rule.getName().equals(OrderRuleConst.TRANSFER_CONDITIONALLY.getName()) ||
            rule.getName().equals(OrderRuleConst.TRANSFER_PROLONGATION.getName())) {
            hbox2.appendChild(label2);
            hbox2.appendChild(dateLikvidation);
            vLayout.appendChild(hbox2);
        }

        final Listbox lbGroups = new Listbox();
        Listhead lh = new Listhead();
        Listheader lhr = new Listheader();
        Label lb = new Label("Название");
        lb.setSclass("cwf-listheader-label");
        lhr.appendChild(lb);
        lh.appendChild(lhr);
        lbGroups.appendChild(lh);

        lbGroups.setItemRenderer((Listitem li, GroupModel o, int i) -> {
            li.setValue(o);
            new Listcell(o.getGroupname()).setParent(li);
        });

        lbGroups.setModel(new ListModelList<>(service.getGroupsBySemester(sem)));
        lbGroups.renderAll();
        lbGroups.setMultiple(true);
        lbGroups.setCheckmark(true);
        lbGroups.setStyle("width: 300px; height: 400px");

        vLayout.appendChild(lbGroups);

        final Label labelDescription = new Label("Примечание к приказу");
        final Textbox tbDescription = new Textbox();
        tbDescription.setWidth("450px");
        tbDescription.setMultiline(true);
        tbDescription.setRows(2);
        tbDescription.setPlaceholder("Введите примечание...");

        vLayout.appendChild(labelDescription);
        vLayout.appendChild(tbDescription);
        component.appendChild(vLayout);

        Button beginCreate = new Button("Создать");
        beginCreate.setStyle("margin-top: 10px;");
        beginCreate.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                HashMap<String, Object> args = new HashMap<>();

                List<GroupModel> groups = new ArrayList<>();

                for (Listitem li : lbGroups.getSelectedItems()) {
                    groups.add(li.getValue());
                }

                if (groups.size() == 0) {
                    PopupUtil.showWarning("Выберите по крайней мере одну группу");
                    return;
                }

                args.put(OrderCreator.ORDER_RULE, rule);
                args.put(OrderCreator.FIRST_DATE, dateTransfer.getValue());
                if (rule.getName().equals(OrderRuleConst.TRANSFER_CONDITIONALLY.getName())) {
                    args.put(OrderCreator.SECOND_DATE, dateLikvidation.getValue());
                }
                args.put(OrderCreator.DESCRIPTION, tbDescription.getValue());
                args.put(OrderCreator.ID_SEMESTER, sem);
                args.put(OrderCreator.GROUPS, groups);

                try {
                    Long id = new OrderCreator().executeCreate(args);

                    if (id != null) {
                        if (!rule.getName().equals(OrderRuleConst.TRANSFER_CONDITIONALLY.getName())) {
                            String pathCreatedFile = service
                                    .createOrderByParams(institute, FileModel.SubTypeDocument.TRANSFER, sem, String.valueOf(id), null);

                            if (pathCreatedFile == null || pathCreatedFile.equals("")) {
                                PopupUtil.showError("Не удалось создать папку для приказа");
                                return;
                            }

                            OrderModel order = service.getCreatedOrderById(id);
                            order.setUrl(pathCreatedFile);
                            order = service.updateOrder(order);

                            if (order == null) {
                                PopupUtil.showError("Не удалось создать приказ");
                                return;
                            }
                        }

                        Map arg = new HashMap();

                        OrderModel order = service.getCreatedOrderById(id);
                        if (rule.getName().equals(OrderRuleConst.TRANSFER_CONDITIONALLY.getName())) {
                            arg.put(WinDefineProlongationCtrl.ORDER, order);
                            arg.put(WinDefineProlongationCtrl.INSTITUTE, institute);
                            arg.put(WinDefineProlongationCtrl.SEMESTER, sem);
                            ComponentHelper.createWindow("winDefineProlongation.zul", "winDefineProlongation", arg).doModal();
                        } else if (rule.getName().equals(OrderRuleConst.TRANSFER.getName())) {
                            arg.put(ORDER_MODEL, order);
                            ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();
                        } else {
                            arg.put(WinDefineProlongationCtrl.ORDER, order);
                            arg.put(WinDefineProlongationCtrl.INSTITUTE, institute);
                            arg.put(WinDefineTransferFromDateCtrl.DATE_SERVICE_NOTE, dateLikvidation.getValue());
                            arg.put(WinDefineProlongationCtrl.SEMESTER, sem);
                            ComponentHelper.createWindow("winDefineTransferFromDate.zul", "winDefineTransferFromDate", arg).doModal();
                        }

                        delegate.updateUI();
                    } else {
                        PopupUtil.showError("Не удалось создать приказ");
                    }
                } catch (UnsupportedOperationException e) {
                    PopupUtil.showError("Невозможно создать приказ данного типа, обратитесь к администратору");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        component.appendChild(beginCreate);
    }
}
