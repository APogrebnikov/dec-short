package org.edec.order.ctrl.visualCreate;

import org.edec.order.ctrl.delegate.IndexPageCtrlDelegate;
import org.edec.order.model.OrderModel;
import org.edec.order.model.OrderRuleModel;
import org.edec.order.service.CreateOrderService;
import org.edec.order.service.impl.CreateOrderServiceESO;
import org.edec.order.service.orderCreator.OrderCreator;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.util.*;


public class VisualAcademical extends VisualAbstract {

    private CreateOrderService service = new CreateOrderServiceESO();

    public VisualAcademical(Window winCreateOrder, IndexPageCtrlDelegate delegate, OrderRuleModel rule, Long currentSem,
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
        final Label label1;
        final Checkbox chStartScholarship = new Checkbox();
        final Datebox dbStartScholarship = new Datebox(new Date());

        if (rule.getName().equals("Академическая стипендия(не в сессию)")) {
            final Hbox hbox = new Hbox();
            final Label lbStartScholarship = new Label("Назначить с");
            dbStartScholarship.setVisible(false);
            chStartScholarship.addEventListener(Events.ON_CHECK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    dbStartScholarship.setVisible(chStartScholarship.isChecked());
                }
            });

            hbox.appendChild(chStartScholarship);
            hbox.appendChild(lbStartScholarship);
            hbox.appendChild(dbStartScholarship);
            vLayout.appendChild(hbox);

            label1 = new Label("Срок сдачи долгов с ");
        } else {
            label1 = new Label("Срок окончания сдачи с ");
        }

        final Hbox hbox = new Hbox();

        final Label label2 = new Label(" по ");
        final Datebox dateEndMarks = new Datebox();
        final Datebox dateStartMarks = new Datebox();
        dateStartMarks.setValue(new Date());
        dateEndMarks.setValue(new Date());
        hbox.appendChild(label1);
        hbox.appendChild(dateStartMarks);
        hbox.appendChild(label2);
        hbox.appendChild(dateEndMarks);

        final Label labelDescription = new Label("Примечание к приказу");
        final Textbox tbDescription = new Textbox();
        tbDescription.setHflex("1");
        tbDescription.setMultiline(true);
        tbDescription.setRows(2);
        tbDescription.setPlaceholder("Введите примечание...");

        vLayout.appendChild(hbox);
        vLayout.appendChild(labelDescription);
        vLayout.appendChild(tbDescription);
        component.appendChild(vLayout);

        Button beginCreate = new Button("Создать");
        beginCreate.setStyle("margin-top: 10px;");
        beginCreate.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                HashMap<String, Object> args = new HashMap<>();

                args.put(OrderCreator.ORDER_RULE, rule);
                args.put(OrderCreator.FIRST_DATE, dateStartMarks.getValue());
                args.put(OrderCreator.SECOND_DATE, dateEndMarks.getValue());
                args.put(OrderCreator.THIRD_DATE, chStartScholarship.isChecked() ? dbStartScholarship.getValue() : null);
                args.put(OrderCreator.DESCRIPTION, tbDescription.getValue());
                args.put(OrderCreator.ID_SEMESTER, sem);

                try {
                    Long id = new OrderCreator().executeCreate(args);

                    if (id != null) {
                        String pathCreatedFile = service
                                .createOrderByParams(institute, FileModel.SubTypeDocument.ACADEMIC, sem, String.valueOf(id), null);

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

                        Map arg = new HashMap();
                        arg.put(ORDER_MODEL, order);

                        ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

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
