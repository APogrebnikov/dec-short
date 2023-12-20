package org.edec.student.FAQ.ctrl;

import org.edec.student.FAQ.service.ChangePasswordService;
import org.edec.student.FAQ.service.impl.ChangePasswordImpl;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Textbox;

public class IndexPageCtrl extends CabinetSelector {

    @Wire
    private Button btnChangePassword, btnShowPassword;
    @Wire
    private Textbox tbOldPassword, tbNewPassword, tbNewPasswordTwo;
    @Wire
    private Groupbox gbChangePassword;

    private ChangePasswordService changePasswordService = new ChangePasswordImpl();

    @Override
    protected void fill() {
        if (!getCurrentUser().isParent()){
            gbChangePassword.setVisible(false);
        }
    }

    @Listen("onClick = #btnChangePassword")
    public void changePassword() {
        String oldPassword = tbOldPassword.getValue();
        String newPassword = tbNewPassword.getValue();
        String newPasswordTwo = tbNewPasswordTwo.getValue();
        Long idParent = getCurrentUser().getIdParent();
        if (newPassword.equals("") || newPasswordTwo.equals("") || oldPassword.equals("")) {
            PopupUtil.showError("Поле не заполнено!");
        } else {
            if (oldPassword.equals(changePasswordService.getOldPassword(idParent))) {
                if (newPassword.equals(newPasswordTwo)) {
                    changePasswordService.changePassword(newPassword, idParent);
                    PopupUtil.showInfo("Пароль успешно изменен!");
                } else {
                    PopupUtil.showError("Проверьте правильность написания нового пароля!");

                }
            } else {
                PopupUtil.showError("Неправильно введен текущий пароль!");
            }
        }
    }

    @Listen("onClick = #btnShowPassword")
    public void showPassword(){
        if (tbOldPassword.getType().equals("password") || tbNewPassword.getType().equals("password") || tbNewPasswordTwo.getType().equals("password")){
            tbOldPassword.setType("text");
            tbNewPassword.setType("text");
            tbNewPasswordTwo.setType("text");
        } else {
            tbOldPassword.setType("password");
            tbNewPassword.setType("password");
            tbNewPasswordTwo.setType("password");
        }
    }
}
