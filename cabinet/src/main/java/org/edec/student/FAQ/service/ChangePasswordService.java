package org.edec.student.FAQ.service;

import org.edec.student.FAQ.model.ChangePasswordModel;

import java.util.List;

public interface ChangePasswordService {
    void changePassword (String newPassword, Long idParent);
    String getOldPassword(Long idParent);
}
