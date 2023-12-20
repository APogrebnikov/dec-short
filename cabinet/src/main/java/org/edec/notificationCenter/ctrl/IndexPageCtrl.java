package org.edec.notificationCenter.ctrl;

import org.edec.notificationCenter.ctrl.renderer.NotificationRenderer;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.notificationCenter.service.impl.NotificationServiceImpl;
import org.edec.utility.constants.ReceiverTypeConst;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.ComponentHelper;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Класс-контроллер для окна отображения списка отправленных уведомлений
 */
public class IndexPageCtrl extends CabinetSelector {

    public static final String NOTIFICATION = "notification";
    public static final String UPDATE_NOTIFICATION_LIST = "updateNotificationList";
    public static final String NOTIFICATION_SERVICE = "notificationService";

    @Wire
    private Listbox lbNotifications;

    @Wire
    private Combobox cmbNotificationType;

    private NotificationService service;

    private List<NotificationModel> notifications;

    //Используется для передачи обработки события открытия уведомления в рендерере
    private Consumer<NotificationModel> openNotificationEditor;
    //Используется для передачи обработки события открытия обратной связи по уведомлению
    private Consumer<NotificationModel> openFeedback;
    //Используется для передачи обработки события обновления заголовка редактируемого уведомления
    private Consumer<NotificationModel> updateNotificationList;
    //Используется для передачи обработки события удаления выбранного уведомления
    private Consumer<NotificationModel> removeNotification;

    @Override
    protected void fill() {
        service = new NotificationServiceImpl();
        notifications = new ArrayList<>();

        openNotificationEditor = (notification) -> openNotificationEditor(notification);
        openFeedback = (notification) -> openFeedback(notification);
        updateNotificationList = (notification) -> {

            for (NotificationModel notificationModel : notifications) {
                if (notificationModel.getId() == notification.getId()) {
                    notificationModel.setHeader(notification.getHeader());
                }
            }

            lbNotifications.setModel(new ListModelList<>(notifications));
            lbNotifications.renderAll();
        };

        removeNotification = (notification) -> removeNotification(notification);

        notifications = service.getAllNotifications(template.getCurrentUser().getIdHum());

        //Задаем список типов уведомлений
        List<String> notificationTypeList = ReceiverTypeConst.getNames();

        cmbNotificationType.setModel(new ListModelList<>(notificationTypeList));


        lbNotifications.setItemRenderer(new NotificationRenderer(openNotificationEditor, openFeedback, removeNotification));

        fillNotificationList();
    }

    @Listen("onClick = #btnCreateNotification")
    public void createNotification() {

        if (cmbNotificationType.getSelectedIndex() == -1) {
            PopupUtil.showWarning("Сначала выберите тип уведомления!");
            return;
        }

        NotificationModel newNotification = service.createNotification(template.getCurrentUser().getIdHum(), ReceiverTypeConst.getByName(cmbNotificationType.getSelectedItem().getValue()));

        //Новые уведомления оторбражаем всегда вверху списка
        notifications.add(0, newNotification);

        fillNotificationList();

        openNotificationEditor(newNotification);
    }

    public void openNotificationEditor(NotificationModel notification) {
        Map<String, Object> arg = new HashMap<>();

        arg.put(NOTIFICATION, notification);
        arg.put(UPDATE_NOTIFICATION_LIST, updateNotificationList);
        arg.put(NOTIFICATION_SERVICE, service);

        ComponentHelper.createWindow("/notificationCenter/winNotificationCreator.zul", "winNotificationCreator", arg).doModal();
    }


    public void openFeedback(NotificationModel notification) {
        Map<String, Object> arg = new HashMap<>();

        arg.put(NOTIFICATION, notification);
        arg.put(NOTIFICATION_SERVICE, service);

        ComponentHelper.createWindow("/notificationCenter/winFeedback.zul","winFeedback", arg).doModal();
    }

    private void fillNotificationList(){
        lbNotifications.setModel(new ListModelList<>(notifications));
        lbNotifications.renderAll();
    }

    public void removeNotification(NotificationModel notificationModel){
        DialogUtil.questionWithYesNoButtons("Вы точно хотите удалить данное уведомление?","Удаление уведомления", event -> {

            if(event.getName().equals(DialogUtil.ON_YES)) {
                if (service.removeNotification(notificationModel.getId())) {
                    PopupUtil.showInfo("Уведомление было успешно удалено!");

                    notifications = service.getAllNotifications(template.getCurrentUser().getIdHum());
                    fillNotificationList();
                } else {
                    PopupUtil.showError("Ошибка при удалении уведомления!");
                }
            }
        });

    }
}
