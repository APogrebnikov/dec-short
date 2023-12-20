package org.edec.notificationCenter.ctrl;

import org.edec.notificationCenter.ctrl.renderer.ReceiverRenderer;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.model.ReceiverModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.notificationCenter.service.impl.NotificationServiceImpl;
import org.edec.utility.constants.ReceiverStatusConst;
import org.edec.utility.constants.ReceiverTypeConst;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.edec.notificationCenter.ctrl.IndexPageCtrl.NOTIFICATION;
import static org.edec.notificationCenter.ctrl.IndexPageCtrl.NOTIFICATION_SERVICE;
import static org.edec.notificationCenter.ctrl.IndexPageCtrl.UPDATE_NOTIFICATION_LIST;

/**
 * Класс-контроллер для окна редактора уведомлений
 */
public class WinNotificationEditorCtrl extends CabinetSelector {

    public static final String RECEIVER_TYPE = "receiverType";
    public static final String ADDED_RECEIVERS = "addedReceivers";
    public static final String UPDATE_RECEIVERS_LIST = "updateReceiverList";

    @Wire
    private Label lType;

    @Wire
    private Listbox lbAttachedFiles, lbReceivers;

    @Wire
    private Textbox tbHeader, tbText;

    @Wire
    private Button btnSendNotification;

    private NotificationService service;

    private NotificationModel notification;

    private List<String> attachedFiles;

    private Consumer<NotificationModel> updateNotificationList;

    private Consumer<ReceiverModel> updateReceiverList;

    private Consumer<ReceiverModel> deleteReceiver;

    @Override
    protected void fill() {

        notification = (NotificationModel) Executions.getCurrent().getArg().get(NOTIFICATION);
        updateNotificationList = (Consumer<NotificationModel>) Executions.getCurrent().getArg().get(UPDATE_NOTIFICATION_LIST);
        service = (NotificationServiceImpl) Executions.getCurrent().getArg().get(NOTIFICATION_SERVICE);

        updateReceiverList = (receiver) -> {
            receiver.setStatus(ReceiverStatusConst.ADDED);

            //добавляем нового получателя
            notification.getReceivers().add(0, receiver);
            fillReceiverList();
        };

        deleteReceiver = (receiver) -> {
            //Удаление пользователя из БД, если он там находится
            if (!receiver.getStatus().equals(ReceiverStatusConst.ADDED)) {
                service.deleteReceiver(ReceiverTypeConst.getByName(notification.getReceiverType()), notification.getId(), receiver);
            }

            notification.getReceivers().remove(receiver);
        };

        attachedFiles = service.getAttachedFiles(notification);

        notification.setReceivers(service.getReceivers(ReceiverTypeConst.getByName(notification.getReceiverType()), notification.getId()));

        tbHeader.setValue(notification.getHeader());
        tbText.setValue(notification.getText());
        lType.setValue(notification.getReceiverType());

        constructListbox();

        lbReceivers.setItemRenderer(new ReceiverRenderer(deleteReceiver, ReceiverRenderer.IS_EDITOR_ELEMENT));

        fillReceiverList();

        //checkSendBtnAvailability();

        lbAttachedFiles.setItemRenderer((Listitem listitem, String file, int i) -> {
            new Listcell(file).setParent(listitem);
            Listcell lcDelete = new Listcell();
            lcDelete.setParent(listitem);

            Button btnDel = new Button("", "/imgs/del.png");
            btnDel.setParent(lcDelete);

            btnDel.addEventListener(Events.ON_CLICK, event -> {
                service.deleteFile(notification, file);
                attachedFiles.remove(file);
                lbAttachedFiles.setModel(new ListModelList<>(attachedFiles));
                lbAttachedFiles.renderAll();
            });
        });
        lbAttachedFiles.setModel(new ListModelList<>(attachedFiles));
        lbAttachedFiles.renderAll();
    }

    @Listen("onClick = #btnSaveNotification")
    public void saveNotification() {

        setNotificationModel();

        if (!service.saveNotification(notification)) {
            PopupUtil.showError("Сохранить информацию по уведомлению не удалось!");
        } else {
            PopupUtil.showInfo("Уведомление успешно сохранено!");
            updateNotificationList.accept(notification);
        }

        fillReceiverList();

    }

    @Listen("onClick = #btnSendNotification")
    public void sendNotification() {

        setNotificationModel();

        service.saveNotification(notification);

        //рассылаем уведомления и обновляем статусы
        notification.setReceivers(lbReceivers.getItems().stream().map(listItem -> {
            ReceiverModel receiver = listItem.getValue();

            if (receiver.getStatus().equals(ReceiverStatusConst.ADDED) || receiver.getStatus().equals(ReceiverStatusConst.SAVED)) {
                receiver.setStatus(ReceiverStatusConst.NOTIFIED);
            }

            return receiver;
        }).collect(Collectors.toList()));

        if (!service.sendNotification(notification.getId())) {
            PopupUtil.showError("Уведомления разослать не удалось!");
        } else {
            PopupUtil.showInfo("Уведомления были успешно разосланы!");
        }

        //обновляем общий статус уведомления как отправленный
        service.updateNotificationStatus(notification.getId());

        notification.setIsSent(true);
        updateNotificationList.accept(notification);

        fillReceiverList();
    }

    @Listen("onUpload = #btnUploadFile")
    public void attachFile(UploadEvent event) {
        List<Media> medias = new ArrayList<>();

        if (event.getMedias() != null && event.getMedias().length > 0) {
            Collections.addAll(medias, event.getMedias());
        } else {
            return;
        }

        //Сохраняем файлы на сервере
        if (medias != null && medias.size() > 0) {
            service.attachFile(notification, medias);
        }

        for (Media m : medias) {
            if (attachedFiles.contains(m.getName())) {
                continue;
            }

            attachedFiles.add(m.getName());
        }

        lbAttachedFiles.setModel(new ListModelList<>(attachedFiles));
        lbAttachedFiles.renderAll();
    }

    @Listen("onClick = #btnAddReceiver")
    public void addReceiver() {
        Map<String, Object> arg = new HashMap<>();

        arg.put(RECEIVER_TYPE, ReceiverTypeConst.getByName(notification.getReceiverType()));
        arg.put(ADDED_RECEIVERS, notification.getReceivers());
        arg.put(NOTIFICATION_SERVICE, service);
        arg.put(UPDATE_RECEIVERS_LIST, updateReceiverList);

        ComponentHelper.createWindow("/notificationCenter/winReceiverSearch.zul", "winReceiverSearch", arg).doModal();
    }

    @Listen("onClick = #btnAddAll")
    public void addAllReceivers() {

        List<String> params = new ArrayList<>();
        params.add("");
        params.add("");
        params.add("");

        notification.setReceivers(service.searchReceivers(template.getCurrentModule().getFormofstudy(),
                                                          template.getCurrentModule().getIdInstituteByFirstDepartment(),
                                                          ReceiverTypeConst.getByName(notification.getReceiverType()),
                                                          Collections.EMPTY_LIST, params
        ));

        notification.setReceivers(notification.getReceivers().stream().map(receiverModel -> {
            receiverModel.setStatus(ReceiverStatusConst.ADDED);
            return receiverModel;
        }).collect(Collectors.toList()));

        fillReceiverList();
    }

    private void fillReceiverList() {
        lbReceivers.setModel(new ListModelList<>(notification.getReceivers()));
        lbReceivers.renderAll();
    }

    //Функции для отрисовки списка получателей

    private void constructListbox() {

        Listhead listhead = new Listhead();
        listhead.setParent(lbReceivers);

        switch (ReceiverTypeConst.getByName(notification.getReceiverType())) {
            case STUDENTS:
            case LEADERS:

                Listheader lhStudentFio = createListHeader(listhead, "2", "left");

                createLabel(lhStudentFio, "ФИО");

                Listheader lhStudentGroup = createListHeader(listhead, "1", "center");

                createLabel(lhStudentGroup, "Группа");

                break;
            case GROUPS:
                Listheader lhGroup = createListHeader(listhead, "5", "left");

                createLabel(lhGroup, "Группа");

                break;
            case COURSES:
                Listheader lhCourse = createListHeader(listhead, "1", "center");

                createLabel(lhCourse, "Курс");

                break;
            case EMPLOYEES:
                Listheader lhEmployeeFio = createListHeader(listhead, "5", "left");

                createLabel(lhEmployeeFio, "ФИО");

                break;
        }

        //Для кнопки
        createListHeader(listhead, "1", "center");
    }

    private Listheader createListHeader(Component parent, String hflex, String align) {
        Listheader listheader = new Listheader();

        listheader.setParent(parent);
        listheader.setHflex(hflex);
        listheader.setAlign(align);

        return listheader;
    }

    private Label createLabel(Component parent, String value) {
        Label label = new Label(value);
        label.setParent(parent);
        label.setSclass("cwf-listheader-label");

        return label;
    }

    //нельзя рассылать уведомление без получателей (отключаем кнопку рассылки)
    private void checkSendBtnAvailability() {
        if (notification.getReceivers().isEmpty()) {
            btnSendNotification.setDisabled(true);
        } else {
            btnSendNotification.setDisabled(false);
        }
    }

    //Записываем данные из UI в модель
    private void setNotificationModel() {
        notification.setHeader(tbHeader.getValue());
        notification.setText(tbText.getValue());

        notification.setReceivers(lbReceivers.getItems().stream().map(listItem -> {
            ReceiverModel receiver = listItem.getValue();
            return receiver;
        }).collect(Collectors.toList()));
    }
}
