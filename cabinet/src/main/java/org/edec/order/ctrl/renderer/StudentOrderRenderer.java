package org.edec.order.ctrl.renderer;

import org.edec.order.ctrl.WinInfoStudentCtrl;
import org.edec.order.ctrl.delegate.WinEditOrderCtrlDelegate;
import org.edec.order.manager.EntityManagerOrderESO;
import org.edec.order.model.OrderModel;
import org.edec.order.model.SectionModel;
import org.edec.order.model.StudentModel;
import org.edec.order.service.impl.EditOrderServiceESO;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.constants.OrderTypeConst;
import org.edec.utility.converter.DateConverter;
import org.edec.utility.fileManager.FileManager;
import org.edec.utility.fileManager.FileModel;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class StudentOrderRenderer implements ListitemRenderer<StudentModel> {
    private OrderModel order;
    private WinEditOrderCtrlDelegate delegate;
    private SectionModel section;

    private final String CANCEL_SCHOLARSHIP = "Отменить";
    private final String ASSIGN_SCHOLARSHIP = "Назначить";

    public StudentOrderRenderer (OrderModel order, WinEditOrderCtrlDelegate delegate) {
        this.order = order;
        this.delegate = delegate;
    }

    public StudentOrderRenderer (OrderModel order, WinEditOrderCtrlDelegate delegate, SectionModel section) {
        this.order = order;
        this.delegate = delegate;
        this.section = section;
    }

    @Override
    public void render (final Listitem li, final StudentModel data, int index) throws Exception {
        li.setValue(data);

        Listcell lc = new Listcell();
        Checkbox chDelete = new Checkbox();

        chDelete.addEventListener(Events.ON_CHECK, event -> {
            data.setSelected(chDelete.isChecked());
        });

        chDelete.setParent(lc);
        lc.setParent(li);
        lc.setStyle("width: 30px;");

        createListcell(li, "(#" + data.getRecordnumber() + ") " + data.getFio(), "width: 100%", 0);

        switch (OrderTypeConst.getByType(order.getOrderType())) {
            case ACADEMIC:
                fillAcademic(data, li, index);
                break;
            case DEDUCTION:
                fillDeduction(data, li, index);
                break;
            case SOCIAL:
                fillSocial(data, li, index);
                break;
            case SOCIAL_INCREASED:
                fillSocialIncreased(data, li, index);
                break;
            case TRANSFER:
                fillTransfer(data, li, index);
                break;
            case SET_ELIMINATION_DEBTS:
                fillSetEliminationDebts(data, li, index);
                break;
            default:
                break;
        }

        Button btnDel = new Button("", "/imgs/del.png");

        if (!delegate.isReadOnlyMode()) {
            btnDel.addEventListener(Events.ON_CLICK,
                                    event -> DialogUtil.questionWithYesNoButtons("Вы действительно хотите удалить студента из приказа?",
                                                                                 "Удаление студента", evt -> {
                                                if (evt.getName().equals(DialogUtil.ON_YES)) {
                                                    new EntityManagerOrderESO().removeStudentFromOrder(data.getId());

                                                    FileModel pathOrder = new FileModel(
                                                            //FIXME убрать id inst
                                                            FileModel.Inst.getInstById(1L), FileModel.TypeDocument.ORDER,
                                                            order.getOrderType().equals(OrderTypeConst.SOCIAL.getType())
                                                            ? FileModel.SubTypeDocument.SOCIAL
                                                            : FileModel.SubTypeDocument.SOCIAL_INCREASE, order.getIdSemester(),
                                                            Long.toString(order.getIdOrder())
                                                    );

                                                    if (order.getOrderType().equals(OrderTypeConst.SOCIAL.getType()) ||
                                                        order.getOrderType().equals(OrderTypeConst.SOCIAL_INCREASED.getType())) {
                                                        new FileManager().removeReferenceFromOrder(pathOrder, data.getFio());
                                                    }
                                                    delegate.updateUI();
                                                    DialogUtil.info("Удаление студента прошло успешно", "Статус");
                                                }
                                            }
                                    )
            );
        } else {
            btnDel.setDisabled(true);
        }

        Button btnInfo = new Button("", "/imgs/edit.png");
        btnInfo.addEventListener(Events.ON_CLICK, event -> {
            Map<String, Object> arg = new HashMap();
            arg.put(WinInfoStudentCtrl.STUDENT, data);
            arg.put(WinInfoStudentCtrl.ORDER, order);
            ComponentHelper.createWindow("/order/winInfoStudent.zul", "winInfoStudent", arg).doModal();
        });

        btnInfo.setParent(createListcell(li, "", "width: 90px; align:center", 0));
        btnDel.setParent(createListcell(li, "", "width: 90px; align:center", 0));
    }

    private void fillAcademic (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            createListheader(lh, "Назначена", "cwf-listheader-label", "width: 180px;");
            createListheader(lh, "Заканчивается", "cwf-listheader-label", "width: 180px;");
            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);
        createListcell(li, DateConverter.convertDateToString(data.getSecondDate()), "width: 180px", 2);
    }

    private void fillSocial (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            createListheader(lh, "Назначена", "cwf-listheader-label", "width: 180px;");
            createListheader(lh, "Заканчивается", "cwf-listheader-label", "width: 180px;");
            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);
        createListcell(li, DateConverter.convertDateToString(data.getSecondDate()), "width: 180px", 2);
    }

    private void fillSocialIncreased (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            if (section.getName().equals(ASSIGN_SCHOLARSHIP)) {
                createListheader(lh, "Назначена", "cwf-listheader-label", "width: 180px;");
                createListheader(lh, "Заканчивается", "cwf-listheader-label", "width: 180px;");
            } else if (section.getName().equals(CANCEL_SCHOLARSHIP)) {
                createListheader(lh, "Отменить с", "cwf-listheader-label", "width: 180px;");
                createListheader(lh, "", "cwf-listheader-label", "width: 180px;");
            }
            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);

        if (section.getName().equals(ASSIGN_SCHOLARSHIP)) {
            createListcell(li, DateConverter.convertDateToString(data.getSecondDate()), "width: 180px", 2);
        } else if (section.getName().equals(CANCEL_SCHOLARSHIP)) {
            createListcell(li, "", "width: 180px", 0);
        }
    }

    private void fillDeduction (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            createListheader(lh, "Отчислен с", "cwf-listheader-label", "width: 180px;");

            if (data.getSecondDate() != null) {
                createListheader(lh, "Окончание выплат с", "cwf-listheader-label", "width: 180px;");
            }

            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);

        if (data.getSecondDate() != null) {
            createListcell(li, DateConverter.convertDateToString(data.getSecondDate()), "width: 180px", 2);
        }
    }

    private void fillTransfer (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            if (order.getIdOrderRule().equals(OrderRuleConst.TRANSFER_PROLONGATION.getId())) {
                createListheader(lh, "Дата продления по", "cwf-listheader-label", "width: 180px;");
            } else {
                createListheader(lh, "Переведен с", "cwf-listheader-label", "width: 180px;");
            }
            if (data.getSecondDate() != null) {
                createListheader(lh, "Срок ликвидации по", "cwf-listheader-label", "width: 180px;");
            }
            if (data.getDatePrevOrder() != null) {
                createListheader(lh, "Предыдущий приказ с", "cwf-listheader-label", "width: 180px;");
            }
            if (data.getNumberPrevOrder() != null) {
                createListheader(lh, "Номер приказа", "cwf-listheader-label", "width: 180px;");
            }
            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);
        if (data.getSecondDate() != null) {
            createListcell(li, DateConverter.convertDateToString(data.getSecondDate()), "width: 180px", 2);
        }
        if (data.getDatePrevOrder() != null) {
            createListcell(li, DateConverter.convertDateToString(data.getDatePrevOrder()), "width: 180px;", 3);
        }
        if (data.getNumberPrevOrder() != null) {
            createListcell(li, data.getNumberPrevOrder(), "width: 180px;", 4);
        }
    }

    private void fillSetEliminationDebts (StudentModel data, Listitem li, int index) {
        if (index == 0) {
            Listhead lh = new Listhead();
            lh.setParent(li.getListbox());

            createListheader(lh, "", "cwf-listheader-label", "width: 30px;");
            createListheader(lh, "Студент", "cwf-listheader-label", "width: 100%;");
            createListheader(lh, "Дата продления по", "cwf-listheader-label", "width: 180px;");
            createListheader(lh, "Инфо", "cwf-listheader-label", "width: 90px;");
            createListheader(lh, "Удаление", "cwf-listheader-label", "width: 90px;");
        }

        createListcell(li, DateConverter.convertDateToString(data.getFirstDate()), "width: 180px", 1);
    }

    private Listheader createListheader (Listhead lh, String name, String sclass, String style) {
        Listheader lhr = new Listheader();
        Label label = new Label(name);
        label.setParent(lhr);
        label.setSclass(sclass);
        lhr.setParent(lh);
        lhr.setStyle(style);

        return lhr;
    }

    private Listcell createListcell (final Listitem li, String name, String style, final int numDate) {
        final Listcell lc = new Listcell(name);
        lc.setParent(li);
        lc.setStyle(style);

        if (numDate > 0 && numDate < 3 && !delegate.isReadOnlyMode()) {
            try {
                final Date date;

                if (numDate == 1) {
                    date = ((StudentModel) li.getValue()).getFirstDate();
                } else if (numDate == 2) {
                    date = ((StudentModel) li.getValue()).getSecondDate();
                } else {
                    date = ((StudentModel) li.getValue()).getThirdDate();
                }

                lc.addEventListener("onClick", event -> {
                    if (lc.getFirstChild() != null) {
                        return;
                    }

                    final StudentModel model = li.getValue();

                    final Datebox datebox = new Datebox();
                    final Button btnOK = new Button("Сохранить");
                    btnOK.addEventListener("onClick", event1 -> {
                        switch (numDate) {
                            case 1:
                                model.setFirstDate(datebox.getValue());
                                break;
                            case 2:
                                model.setSecondDate(datebox.getValue());
                                break;
                            case 3:
                                model.setThirdDate(datebox.getValue());
                                break;
                        }

                        new EditOrderServiceESO().updateDateForLoss(model.getId(), datebox.getValue(), numDate);

                        while (lc.getFirstChild() != null) {
                            lc.removeChild(lc.getFirstChild());
                        }
                        lc.setLabel(new SimpleDateFormat("dd.MM.yyyy").format(datebox.getValue()));
                    });

                    final Button btnCancel = new Button("Отмена");
                    btnCancel.addEventListener("onClick", event12 -> {
                        while (lc.getFirstChild() != null) {
                            lc.removeChild(lc.getFirstChild());
                        }
                        switch (numDate) {
                            case 1:
                                lc.setLabel(new SimpleDateFormat("dd.MM.yyyy").format(((StudentModel) li.getValue()).getFirstDate()));
                                break;
                            case 2:
                                lc.setLabel(new SimpleDateFormat("dd.MM.yyyy").format(((StudentModel) li.getValue()).getSecondDate()));
                                break;
                            case 3:
                                lc.setLabel(new SimpleDateFormat("dd.MM.yyyy").format(((StudentModel) li.getValue()).getThirdDate()));
                                break;
                        }
                    });

                    lc.setLabel("");
                    lc.appendChild(datebox);
                    lc.appendChild(btnOK);
                    lc.appendChild(btnCancel);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return lc;
    }
}
