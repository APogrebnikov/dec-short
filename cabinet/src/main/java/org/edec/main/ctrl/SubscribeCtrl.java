package org.edec.main.ctrl;

import lombok.extern.log4j.Log4j;
import org.edec.fcmNotifications.service.fcmNotificationService;
import org.edec.fcmNotifications.service.impl.fcmNotificationServiceImpl;
import org.edec.main.auth.AuthInit;
import org.edec.main.auth.AuthLDAP;
import org.edec.main.model.UserModel;
import org.edec.main.service.UserService;
import org.edec.main.service.impl.UserServiceESOimpl;
import org.edec.profile.service.ProfileService;
import org.edec.profile.service.impl.ProfileServiceEsoImpl;
import org.edec.utility.zk.CabinetSelector;
import org.edec.utility.zk.DialogUtil;
import org.edec.utility.zk.PopupUtil;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

import javax.naming.NamingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j
public class SubscribeCtrl extends CabinetSelector {

    private ProfileService profileService = new ProfileServiceEsoImpl();
    private UserService userService = new UserServiceESOimpl();
    private fcmNotificationService fcmService = new fcmNotificationServiceImpl();

    private String prevPage;

    private String login;
    private String password;
    private String serverURL;

    private UserModel userModel;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        prevPage = (String) Executions.getCurrent().getSession().getAttribute(AuthInit.FROM_PAGE);
    }

    @Override
    protected void fill() throws InterruptedException {
        userModel = template.getCurrentUser();
        System.out.println("test  >>>  "+userModel);
    }

    @Listen("onSubscribe = #hbMain")
    public void finishSubscribe(Event event) {
        org.json.JSONObject jsonData = new org.json.JSONObject(event.getData().toString());
        String url = String.valueOf(jsonData.getString("URL"));
        String token = String.valueOf(jsonData.getString("token"));
        System.out.println(url + " << " + userModel.getIdHum() + " << " + token);

        Set<String> setOfTokens = new HashSet<>(fcmService.getTokenByIdHumanface(userModel.getIdHum()));

        if (!setOfTokens.isEmpty()){
            if (setOfTokens.contains(token)){
                    PopupUtil.showInfo("Подписка уже оформлена.");
                } else {
                    if (userService.createTokenAndIdHumanface(userModel.getIdHum(), token)){
                        PopupUtil.showInfo("Подписка успешно оформлена!");
                    } else PopupUtil.showWarning("Не удалось оформить подписку.");
            }
        } else {
            if (userService.createTokenAndIdHumanface(userModel.getIdHum(), token)){
                PopupUtil.showInfo("Подписка успешно оформлена!");
            } else PopupUtil.showWarning("Не удалось оформить подписку.");
        }

     //   Executions.sendRedirect("http://dec.sfu-kras.ru/cabinetsubtest/index.zul");
    }
}
