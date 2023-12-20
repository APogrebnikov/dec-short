package org.edec.utility.sign.service.impl;

import org.edec.order.ctrl.IndexPageCtrl;
import org.edec.order.model.OrderModel;
import org.edec.teacher.model.register.RegisterModel;
import org.edec.utility.sign.service.SignService;


public class SignOrderImpl implements SignService {
    private OrderModel order;
    private IndexPageCtrl indexPageCtrl;

    public SignOrderImpl (OrderModel orderModel, IndexPageCtrl indexPageCtrl) {
        this.order = orderModel;
        this.indexPageCtrl = indexPageCtrl;
    }

    @Override
    public boolean createFileAndUpdateUI (byte[] bytesFile, String serialNumber, String thumbPrint) {
        return false;
    }

    @Override
    public RegisterModel getRegisterModel() {
        return null;
    }

    @Override
    public boolean hasSignRegister() {
        return false;
    }
}
