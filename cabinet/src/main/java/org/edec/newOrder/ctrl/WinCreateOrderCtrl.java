package org.edec.newOrder.ctrl;

import org.edec.newOrder.component.ListboxDocument;
import org.edec.newOrder.model.OrderVisualParamModel;
import org.edec.newOrder.model.createOrder.*;
import org.edec.newOrder.model.editOrder.OrderEditModel;
import org.edec.newOrder.service.ComponentProvider;
import org.edec.newOrder.service.CreateOrderService;
import org.edec.newOrder.service.esoImpl.CreateOrderServiceESO;
import org.edec.newOrder.service.orderCreator.OrderService;
import org.edec.newOrder.service.orderCreator.concept.AcademicFirstCourseMultiCreator;
import org.edec.newOrder.service.orderCreator.concept.OrderServiceFactory;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.component.service.impl.ComponentServiceESOimpl;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.constants.OrderRuleConst;
import org.edec.utility.zk.PopupUtil;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;
import org.zkoss.zul.impl.XulElement;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.edec.newOrder.ctrl.WinEditOrderCtrl.ORDER_MODEL;

// TODO проработать отображения, обработать случае отсутствия приказов в системе, но наличие их в комобобоксе при создании
//
public class WinCreateOrderCtrl extends CabinetSelector {
    public static final String UPDATE_UI = "updateUi";
    public static final String STUDENT_DEDUCTION_BY_СOMISSION = "deductionByComission";

    @Wire
    private Combobox cmbInst, cmbFormOfStudy, cmbListOrderTypes, cmbListOrderRules;

    @Wire
    private Hbox hbInst, hbFormOfStudy;

    @Wire
    private Vbox vbOrderRules;

    @Wire
    private Groupbox gbOrderParams, gbOrderStudents, gbOrderDocuments;

    @Wire
    private Listbox lbParams, lbStudents;

    private ListboxDocument lbDocuments;

    @Wire
    private Window winCreateOrder;

    @Wire
    private Textbox tbDescription;

    private List<OrderCreateTypeModel> listTypes;
    private List<OrderCreateRuleModel> listRules;
    private List<XulElement> orderParamElements = new ArrayList<>();
    private HashSet<OrderCreateStudentModel> studentsToAdd = new HashSet<>();

    private FormOfStudy currentFOS;
    private InstituteModel currentInstitute;
    private Long currentSem;
    private Runnable updateUI;

    private CreateOrderService createOrderService = new CreateOrderServiceESO();
    private ComponentService componentService = new ComponentServiceESOimpl();
    private ComponentProvider componentProvider = new ComponentProvider();
    private OrderService orderService;

    private boolean firstStudentAdded = false;

    @Override
    protected void fill() {
        updateUI = (Runnable) Executions.getCurrent().getArg().get(UPDATE_UI);
        currentInstitute = componentService.fillCmbInst(cmbInst, hbInst, currentModule.getDepartments(), false);
        currentFOS = componentService.fillCmbFormOfStudy(cmbFormOfStudy, hbFormOfStudy, currentModule.getFormofstudy(), false);
        currentSem = createOrderService.getCurrentSemester(currentInstitute.getIdInst(), currentFOS.getType());

        // TODO заглушка по получению всех правил
        // TODO необходимо ввести понятие: правило для института и формы контроля(ограничения на уровне бд)
        listTypes = createOrderService.getOrderTypes();
        cmbListOrderTypes.setModel(new ListModelList<>(listTypes));
        cmbListOrderTypes.setItemRenderer((ComboitemRenderer<OrderCreateTypeModel>) (comboitem, orderCreateTypeModel, i) -> {
            comboitem.setValue(orderCreateTypeModel);
            comboitem.setLabel(orderCreateTypeModel.getName());
        });
    }

    @Listen("onChange = #cmbInst")
    public void onChangeCmbInst() {
        currentInstitute = cmbInst.getSelectedItem().getValue();
        clearWorkspace();
        updateSemesterAndRules();
    }

    @Listen("onChange = #cmbFormOfStudy")
    public void onChangeCmbFOS() {
        currentFOS = cmbFormOfStudy.getSelectedItem().getValue();
        clearWorkspace();
        updateSemesterAndRules();
    }

    @Listen("onChange = #cmbListOrderTypes")
    public void onChangeCmbListOrderTypes() {
        clearWorkspace();

        listRules = createOrderService.getOrderRulesByInstituteAndType(currentInstitute.getIdInst(),
                                                                       ((OrderCreateTypeModel) cmbListOrderTypes.getSelectedItem()
                                                                                                                .getValue()).getId()
        );
        cmbListOrderRules.setModel(new ListModelList<>(listRules));
        cmbListOrderRules.setItemRenderer((ComboitemRenderer<OrderCreateRuleModel>) (comboitem, orderCreateRuleModel, i) -> {
            comboitem.setValue(orderCreateRuleModel);
            comboitem.setLabel(orderCreateRuleModel.getName());
        });

        cmbListOrderRules.setValue("");
        cmbListOrderRules.setSelectedIndex(-1);

        vbOrderRules.setVisible(true);
    }

    @Listen("onAfterRender = #cmbListOrderRules")
    public void selectOnlyOption() {
        if (listRules.size() == 1) {
            cmbListOrderRules.setSelectedItem(cmbListOrderRules.getItems().get(0));
            Events.sendEvent(cmbListOrderRules, new Event(Events.ON_CHANGE));
        }
    }

    @Listen("onChange = #cmbListOrderRules")
    public void onChangeCmbListOrderRules() {
        clearWorkspace();
        if (cmbListOrderRules.getSelectedIndex() != -1) {
            setWorkspace(cmbListOrderRules.getSelectedItem().getValue());
        }
    }

    @Listen("onClick = #btnCreateOrder")
    public void onClickBtnCreateOrder() {
        if (cmbListOrderRules.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Выберите тип приказа");
            return;
        }

        List<Object> valueOrderParams = orderParamElements.stream().filter(e -> componentProvider.getValueComponent(e) != null)
                                                          .map(element -> componentProvider.getValueComponent(element))
                                                          .collect(Collectors.toList());

        if (valueOrderParams.size() != orderParamElements.size()) {
            PopupUtil.showWarning("Заполните все параметры приказа");
            return;
        }

        valueOrderParams.add(currentInstitute.getIdInst());
        valueOrderParams.add(currentFOS.getType());
        valueOrderParams.add(currentSem);
        valueOrderParams.add(tbDescription.getValue());

        List<Object> documentParams = lbDocuments == null ? new ArrayList<>() : lbDocuments.getParams();
        List<Media> attachedDocuments = lbDocuments == null ? new ArrayList<>() : lbDocuments.getAttachedDocuments();

        OrderCreateRuleModel orderRule = cmbListOrderRules.getSelectedItem().getValue();

        //Создаем множество приказов для академ стипендии первого курса
        if (orderRule.getId().equals(OrderRuleConst.ACADEMIC_FIRST_COURSE.getId())) {

            AcademicFirstCourseMultiCreator academicFirstCourseMultiCreator = new AcademicFirstCourseMultiCreator(
                    valueOrderParams, orderService);

            if (academicFirstCourseMultiCreator.createMultipleAcademicOrders()) {
                updateUI.run();

                PopupUtil.showInfo("Приказы были успешно созданы!");
                this.winCreateOrder.detach();
            } else {
                PopupUtil.showError("Не удалось создать приказ");
            }
        } else {

            OrderEditModel order = orderService.getOrderCreateService().createOrder(valueOrderParams, documentParams, attachedDocuments,
                                                                                    new ArrayList<>(studentsToAdd), null
            );

            if (order == null) {
                Messagebox.show("Не удалось создать приказ");
                return;
            }

            Map<String, Object> arg = new HashMap<>();
            arg.put(ORDER_MODEL, order);
            ComponentHelper.createWindow("winEditOrder.zul", "winEditOrder", arg).doModal();

            updateUI.run();
        }
    }

    private void updateSemesterAndRules() {
        currentSem = createOrderService.getCurrentSemester(currentInstitute.getIdInst(), currentFOS.getType());
        listRules = createOrderService.getOrderRulesByInstituteAndType(currentInstitute.getIdInst(),
                                                                       ((OrderCreateTypeModel) cmbListOrderTypes.getSelectedItem()
                                                                                                                .getValue()).getId()
        );
        cmbListOrderRules.setModel(new ListModelList<>(listRules));
        cmbListOrderRules.setValue("");
        cmbListOrderRules.setSelectedIndex(-1);
    }

    private void clearWorkspace() {
        gbOrderParams.setVisible(false);
        while (lbParams.getItems().size() > 0) {
            lbParams.getItems().remove(0);
        }

        gbOrderStudents.setVisible(false);
        while (lbStudents.getItems().size() > 0) {
            lbStudents.getItems().remove(0);
        }

        gbOrderDocuments.setVisible(false);
        while (gbOrderDocuments.getFirstChild() != null) {
            gbOrderDocuments.removeChild(gbOrderDocuments.getFirstChild());
        }

        orderParamElements = new ArrayList<>();
    }

    private void setWorkspace(OrderCreateRuleModel ruleModel) {
        orderService = OrderServiceFactory.getServiceByRule(ruleModel.getId(), currentFOS, currentInstitute.getIdInst());

        if (orderService == null) {
            return;
        }

        if (ruleModel.getAutomatic()) {
            fillParamsTable();
        } else {
            fillStudentsTable();
        }

        if (orderService.isFilesNeeded() || orderService.getOrderDocuments().size() > 0) {
            fillDocumentsTable();
        }

        winCreateOrder.setPosition("center, center");
    }

    private void fillParamsTable() {
        gbOrderParams.setVisible(true);

        for (OrderCreateParamModel orderCreateParamModel : orderService.getOrderParams()) {
            Listitem li = new Listitem();

            Listcell lcLabelParam = new Listcell(orderCreateParamModel.getLabelParam());
            Listcell lcElementParam = new Listcell();
            XulElement element = componentProvider.provideComponent(orderCreateParamModel.getUiElement());
            orderParamElements.add(element);
            lcElementParam.appendChild(element);

            li.appendChild(lcLabelParam);
            li.appendChild(lcElementParam);
            lbParams.appendChild(li);
        }
    }

    private void fillDocumentsTable() {
        gbOrderDocuments.setVisible(true);

        lbDocuments = new ListboxDocument(orderService);
        gbOrderDocuments.appendChild(lbDocuments);
    }

    private void fillStudentsTable() {
        gbOrderStudents.setVisible(true);

        lbStudents.appendChild(getLiWithAddStudentsButton());
    }

    private Listitem getLiWithAddStudentsButton() {
        Listitem li = new Listitem();

        Listcell lcButtonAdd = new Listcell();
        lcButtonAdd.appendChild(getAddStudentsButton());

        li.appendChild(lcButtonAdd);
        li.appendChild(new Listcell());
        li.appendChild(new Listcell());
        li.appendChild(new Listcell());
        return li;
    }

    private Button getAddStudentsButton() {
        Button btnAddStudent = new Button("Добавить");

        btnAddStudent.addEventListener(Events.ON_CLICK, event -> {
            HashMap<Integer, Object> args = new HashMap<>();

            Consumer<List<OrderCreateStudentModel>> updateListStudentsFunc = list -> {
                lbStudents.removeChild(lbStudents.getLastChild());

                studentsToAdd.addAll(list);

                addRowsToStudentsList(list);

                lbStudents.appendChild(getLiWithAddStudentsButton());
            };

            args.put(WinSelectStudentsForCreateCtrl.INSTITUTE, currentInstitute);
            args.put(WinSelectStudentsForCreateCtrl.FOS, currentFOS);
            args.put(WinSelectStudentsForCreateCtrl.ADD_ACTION, updateListStudentsFunc);

            ComponentHelper.createWindow("/newOrder/winSelectStudentsForCreate.zul", "winSelectStudentsForCreate", args).doModal();
        });

        return btnAddStudent;
    }

    private void addRowsToStudentsList(List<OrderCreateStudentModel> studentsToAdd) {
        studentsToAdd.forEach(student -> {
            Listitem li = new Listitem();

            Listcell lcFamily = new Listcell(student.getFio());
            li.appendChild(lcFamily);

            Listcell lcGroup = new Listcell(student.getGroupname());
            li.appendChild(lcGroup);

            Label btnUpdateParam = new Label("Параметры");
            Listcell lcUpdateParam = new Listcell();
            lcUpdateParam.appendChild(btnUpdateParam);
            li.appendChild(lcUpdateParam);

            Popup popupParam = new Popup();
            popupParam.setParent(lcUpdateParam);

            lcUpdateParam.addEventListener(Events.ON_CLICK, event -> {
                popupParam.open(btnUpdateParam, "Overlap");
            });

            fillPopupStudentParams(student, popupParam);

            Button btnDelete = new Button();
            btnDelete.setImage("/imgs/cross.png");
            btnDelete.addEventListener(Events.ON_CLICK, event -> {
                lbStudents.removeChild(li);
                studentsToAdd.remove(li.getValue());
            });
            Listcell lcDelete = new Listcell();
            lcDelete.appendChild(btnDelete);
            li.appendChild(lcDelete);

            lbStudents.appendChild(li);

            if (!firstStudentAdded) {
                Clients.showNotification("Нажмите чтобы заполнить параметры студента", "info", lcUpdateParam, "before_end", 2000);
            }

            firstStudentAdded = true;
        });
    }

    public void fillPopupStudentParams(OrderCreateStudentModel student, Popup popupParam) {
        Vbox vbSectionParam = new Vbox();
        Hbox hbSection = new Hbox();
        Label lSection = new Label("Название секции:");

        Combobox cmbSection = new Combobox();
        cmbSection.setItemRenderer((Comboitem ci, OrderSectionModel data, int i) -> {
            ci.setValue(data);
            ci.setLabel(data.getSectionName());
        });

        cmbSection.setModel(new ListModelList<>(createOrderService.getSection(orderService.getOrderRuleConst().getId())));
        lSection.setParent(hbSection);
        cmbSection.setParent(hbSection);

        Vbox vbParam = new Vbox();
        vbParam.setStyle("padding-bottom: 10px");

        cmbSection.addEventListener(Events.ON_CHANGE, event -> {
            while (vbParam.getFirstChild() != null) {
                vbParam.removeChild(vbParam.getFirstChild());
            }
            orderParamElements.clear();
            OrderSectionModel sectionModel = cmbSection.getSelectedItem().getValue();

            for (OrderVisualParamModel model : orderService.getEditCreatedOrderService()
                                                           .getVisualParamsByIdSection(sectionModel.getIdOrderSection())) {
                Hbox hbParam = new Hbox();
                hbParam.setWidth("230px");

                Label lParamName = new Label();
                lParamName.setValue(model.getName());
                lParamName.setHflex("1");
                lParamName.setStyle("line-height: 24px");

                XulElement element = componentProvider.provideComponent(model.getEditComponent());
                orderParamElements.add(element);
                element.setWidth("120px");

                hbParam.appendChild(lParamName);
                hbParam.appendChild(element);

                vbParam.appendChild(hbParam);
            }
        });

        cmbSection.addEventListener("onAfterRender", event -> {
            cmbSection.setSelectedIndex(0);
            Events.echoEvent(Events.ON_CHANGE, cmbSection, null);

            if (cmbSection.getItems().size() == 1) {
                hbSection.setVisible(false);
            }
        });

        vbSectionParam.appendChild(hbSection);
        vbSectionParam.appendChild(vbParam);

        Button btnClosePopUp = new Button("Закрыть");
        btnClosePopUp.addEventListener(Events.ON_CLICK, event -> {
            student.setParamsFromCreate(
                    orderParamElements.stream().map(el -> componentProvider.getValueComponent(el)).collect(Collectors.toList()));
            popupParam.close();
        });

        popupParam.addEventListener(Events.ON_OPEN, (OpenEvent event) -> {
            if (!event.isOpen()) {
                student.setParamsFromCreate(
                        orderParamElements.stream().map(el -> componentProvider.getValueComponent(el)).collect(Collectors.toList()));
            }
        });

        popupParam.appendChild(vbSectionParam);
        popupParam.appendChild(btnClosePopUp);
    }
}
