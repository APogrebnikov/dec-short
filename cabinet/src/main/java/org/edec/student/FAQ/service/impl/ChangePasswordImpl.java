package org.edec.student.FAQ.service.impl;

import org.edec.student.FAQ.manger.ChangePasswordManager;
import org.edec.student.FAQ.service.ChangePasswordService;


public class ChangePasswordImpl implements ChangePasswordService {

    ChangePasswordManager changePasswordManager = new ChangePasswordManager();

    @Override
    public void changePassword(String newPassword, Long idParent) {
       changePasswordManager.changePassword(newPassword, idParent);
    }

    @Override
    public String getOldPassword(Long idParent) {
        return changePasswordManager.getOldPassword(idParent);
    }


}
