package org.edec.notificationCenter.ctrl;

import org.edec.notificationCenter.ctrl.renderer.ReceiverRenderer;
import org.edec.notificationCenter.model.ReceiverModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.notificationCenter.service.impl.NotificationServiceImpl;
import org.edec.utility.constants.ReceiverTypeConst;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.edec.notificationCenter.ctrl.IndexPageCtrl.NOTIFICATION_SERVICE;
import static org.edec.notificationCenter.ctrl.WinNotificationEditorCtrl.ADDED_RECEIVERS;
import static org.edec.notificationCenter.ctrl.WinNotificationEditorCtrl.RECEIVER_TYPE;
import static org.edec.notificationCenter.ctrl.WinNotificationEditorCtrl.UPDATE_RECEIVERS_LIST;

/**
 * Класс-контроллер для окна поиска для добавления получаетелей уведомления
 */
public class WinReceiversSearchCtrl extends CabinetSelector {

    @Wire
    Listbox lbSearchReceivers;

    private ReceiverTypeConst type;
    private List<ReceiverModel> receivers;
    private Consumer<ReceiverModel> updateReceiverList;
    private NotificationService service;

    @Override
    protected void fill() {

        type = (ReceiverTypeConst) Executions.getCurrent().getArg().get(RECEIVER_TYPE);
        receivers = (List<ReceiverModel>) Executions.getCurrent().getArg().get(ADDED_RECEIVERS);
        updateReceiverList = (Consumer<ReceiverModel>) Executions.getCurrent().getArg().get(UPDATE_RECEIVERS_LIST);
        service = (NotificationServiceImpl) Executions.getCurrent().getArg().get(NOTIFICATION_SERVICE);

        constructListbox();

        lbSearchReceivers.setItemRenderer(new ReceiverRenderer(updateReceiverList, ReceiverRenderer.IS_SEARCH_ELEMENT));

        fillListbox();
    }

    //Отрисовка визуальных элементов
    private void constructListbox() {
        Auxhead auxhead = new Auxhead();
        auxhead.setParent(lbSearchReceivers);

        Listhead listhead = new Listhead();
        listhead.setParent(lbSearchReceivers);

        switch (type) {
            case STUDENTS:
            case LEADERS:

                Auxheader ahSearchStudentFio = createAuxHeader(auxhead, "left");

                Auxheader ahSearchStudentGroup = createAuxHeader(auxhead, "left");

                //fio
                createListHeader(listhead, "3", "left");
                //group
                createListHeader(listhead, "1", "center");

                Textbox tbSearchStudentFio = createTextbox(ahSearchStudentFio, "..Введите ФИО", "100%");

                Textbox tbSearchStudentGroup = createTextbox(ahSearchStudentGroup, "..Введите группу", "100%");

                tbSearchStudentFio.addEventListener(Events.ON_OK,
                                                    event -> searchStudents(tbSearchStudentFio.getText(), tbSearchStudentGroup.getText())
                );

                tbSearchStudentGroup.addEventListener(Events.ON_OK,
                                                      event -> searchStudents(tbSearchStudentFio.getText(), tbSearchStudentGroup.getText())
                );

                break;
            case GROUPS:
                Auxheader ahSearchGroup = createAuxHeader(auxhead, "left");

                createListHeader(listhead, "5", "left");

                Textbox tbSearchGroup = createTextbox(ahSearchGroup, "..Введите группу", "100%");

                tbSearchGroup.addEventListener(Events.ON_OK, event -> searchGroups(tbSearchGroup.getText()));

                break;
            case COURSES:

                Listheader lhCourse = createListHeader(listhead, "1", "center");

                createLabel(lhCourse, "Курс");

                break;
            case EMPLOYEES:
                Auxheader ahSearchEmployeeFio = createAuxHeader(auxhead, "left");
                createListHeader(listhead, "5", "left");
                Textbox tbSearchEmployeeFio = createTextbox(ahSearchEmployeeFio, "..Введите ФИО", "100%");

                tbSearchEmployeeFio.addEventListener(Events.ON_OK, event -> searchEmployee(tbSearchEmployeeFio.getText()));

                break;
        }

        //button
        createListHeader(listhead, "1", "center");
    }

    private Auxheader createAuxHeader(Component parent, String align) {
        Auxheader auxheader = new Auxheader();
        auxheader.setParent(parent);
        auxheader.setAlign(align);

        return auxheader;
    }

    private Listheader createListHeader(Component parent, String hflex, String align) {
        Listheader listheader = new Listheader();

        listheader.setParent(parent);
        listheader.setHflex(hflex);
        listheader.setAlign(align);

        return listheader;
    }

    private Textbox createTextbox(Component parent, String placeHolder, String width) {
        Textbox textbox = new Textbox();

        textbox.setParent(parent);
        textbox.setPlaceholder(placeHolder);
        textbox.setWidth(width);

        return textbox;
    }

    private Label createLabel(Component parent, String value) {
        Label label = new Label(value);
        label.setParent(parent);
        label.setSclass("cwf-listheader-label");

        return label;
    }

    //Валидаторы для полей поиска
    private void searchStudents(String fio, String group) {
        if (fio.length() < 4 && group.length() < 4) {
            PopupUtil.showWarning("ФИО или название группы должно содержать хотя бы четыре символа!");
            return;
        }

        List<String> params = new ArrayList<>();

        params.add(fio);
        params.add(group);

        fillReceiversList(params);
    }

    private void searchGroups(String group) {
        if (group.length() < 4) {
            PopupUtil.showWarning("Название группы должно содержать хотя бы четыре символа!");
            return;
        }

        List<String> params = new ArrayList<>();

        params.add(group);

        fillReceiversList(params);
    }

    private void searchEmployee(String fio) {
        if (fio.length() < 4) {
            PopupUtil.showWarning("ФИО должно содержать хотя бы четыре символа!");
            return;
        }

        List<String> params = new ArrayList<>();

        params.add(fio);

        fillReceiversList(params);
    }

    private void fillReceiversList(List<String> params) {
        List<ReceiverModel> foundReceivers = service.searchReceivers(template.getCurrentModule().getFormofstudy(),
                                                                     template.getCurrentModule().getIdInstituteByFirstDepartment(), type,
                                                                     receivers, params
        );

        lbSearchReceivers.setModel(new ListModelList<>(foundReceivers));
        lbSearchReceivers.renderAll();
    }

    private void fillListbox() {

        //Так как изначально подразумевалось, что метод fillReceiverList() используется при поиске получателей
        //при обязательном условии, что пользователь вводит параметры поиска (например фио или группа)
        //поэтому необходимо передавать параметры для поиска, хотя бы пустые - иначе вылетает NPE
        //заполняем максимальное количество параметров для поиска, которое может передаться (на текущий момент - 2)

        List<String> params = new ArrayList<>();
        params.add("");
        params.add("");

        fillReceiversList(params);
    }
}
