package org.edec.personalNotifications.ctrl;

import org.edec.personalNotifications.ctrl.renderer.NotificationRenderer;
import org.edec.notificationCenter.model.NotificationModel;
import org.edec.notificationCenter.service.NotificationService;
import org.edec.notificationCenter.service.impl.NotificationServiceImpl;
import org.edec.utility.zk.CabinetSelector;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

import java.util.List;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    Listbox lbNotifications;

    NotificationService service;

    List<NotificationModel> notifications;

    @Override
    protected void fill(){
        service = new NotificationServiceImpl();

        notifications = service.getUsersNotifications(this.getCurrentUser().getIdHum());

        lbNotifications.setItemRenderer(new NotificationRenderer(service));
        lbNotifications.setModel(new ListModelList<>(notifications));
        lbNotifications.renderAll();
    }
}
