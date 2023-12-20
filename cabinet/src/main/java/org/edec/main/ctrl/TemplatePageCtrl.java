package org.edec.main.ctrl;

import javafx.util.Pair;
import org.apache.http.NameValuePair;
import org.edec.main.auth.AuthInit;
import org.edec.main.model.*;
import org.edec.main.service.UserService;
import org.edec.main.service.impl.UserServiceESOimpl;
import org.edec.utility.httpclient.manager.HttpClient;
import org.edec.utility.zk.PopupUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.zkoss.zhtml.Li;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zhtml.Ul;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TemplatePageCtrl extends SelectorComposer<Component> {

    private static final String CURRENT_MODULE = "current_module";
    public static final String CURRENT_USER = "current_user";
    public static final String CURRENT_COUNT_REQUEST = "current_count_request";
    public static final String CURRENT_COUNT_PERSONAL_NOTIFICATIONS = "current_count_personal_notifications";
    public static final String CURRENT_COUNT_ALL_NOTIFICATIONS = "current_count_all_notifications";

    @Wire
    private Div divSidebar, divBadgeNotification, divRequest;
    @Wire
    private Hbox hbTemplateNotification;
    @Wire
    private Label lPageName, lCurrentUser;
    @Wire
    private Popup popupTemplateCogs;
    @Wire
    private Span spanCogs, spanShowNav, spanReqCount;
    @Wire
    private Audio auNote;
    @Wire
    private Label lbReqCount;
    @Wire
    private Timer timerReq;

    private UserService userService = new UserServiceESOimpl();

    private UserModel userModel;
    private Set<Pair<Long,Integer>> rights;
    private boolean hasAccess;

    private Integer currentCountOfRequest = 0;
    private Integer currentCountOfPersonalNotifications = 0;
    private Integer currentCountOfAllNotifications = 0;

    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        timerReq.stop();
        userModel = getCurrentUser();
        if (userModel != null) {
            generateDiv();

            collectAllRights();

            hasAccess = accessToNotificationCenter();

            timerReq.start();

            currentCountOfRequest = getCurrentCountOfRequest();
            currentCountOfAllNotifications = getCurrentCountOfAllNotifications();
            currentCountOfPersonalNotifications = getCurrentCountOfPersonalNotifications();

            if (currentCountOfRequest == null && currentCountOfAllNotifications == null && currentCountOfPersonalNotifications == null) {
                currentCountOfRequest = 0;
                currentCountOfAllNotifications = 0;
                currentCountOfPersonalNotifications = 0;
                spanReqCount.setVisible(false);
                lbReqCount.setVisible(false);

                setCurrentCountOfRequest(currentCountOfRequest);
                setCurrentCountOfPersonalNotifications(currentCountOfPersonalNotifications);
                setCurrentCountOfAllNotifications(currentCountOfAllNotifications);

            } else if (currentCountOfRequest > 0 || currentCountOfPersonalNotifications > 0 || currentCountOfAllNotifications > 0) {
                lbReqCount.setValue(Integer.toString(currentCountOfRequest + currentCountOfAllNotifications + currentCountOfPersonalNotifications));
                spanReqCount.setVisible(true);
                lbReqCount.setVisible(true);
            }
        }
    }

    public void collectAllRights() {
        this.rights = new HashSet<>();

        for (RoleModel role : userModel.getRoles()) {
            for (ModuleModel module : role.getModules()) {
                for (DepartmentModel department : module.getDepartments()) {
                    if(department.getIdInstitute() != null && module.getFormofstudy() != null) {
                        Pair<Long, Integer> rightModel = new Pair<>(department.getIdInstitute(), module.getFormofstudy());
                        this.rights.add(rightModel);
                    }
                }
            }
        }
    }

    public boolean accessToNotificationCenter(){
        for (RoleModel role : userModel.getRoles()) {
            for(ModuleModel module : role.getModules()){
                if(module.getName().equals("Центр уведомлений")){
                    return true;
                }
            }
        }

        return false;
    }

    @Listen("onTimer = #timerReq")
    public void onTimerCheck() {
        Integer reqCount = 0;
        Integer personalNotifCount = 0;
        Integer allNotifCount = 0;
        List<NameValuePair> params = new ArrayList();

        JSONObject jsonObjectParams = new JSONObject();
        
        if(getCurrentUser()==null) return;

        jsonObjectParams.put("idHumanface", getCurrentUser().getIdHum());
        jsonObjectParams.put("rights", rights);
        jsonObjectParams.put("hasAccess", hasAccess);

        // Стучимся в общий REST сервис для просмотра кол-ва заявок
        try {
            JSONObject jsonObject = new JSONObject(
                    //TODO: Поменять на динамический адрес приложения - в HTTPS!
                    HttpClient.makeHttpRequest("https://dec.sfu-kras.ru/cabinet/rest/register/request/count", HttpClient.POST, params, jsonObjectParams.toString())
            );

            if (jsonObject.has("count")) {
                reqCount = (Integer) jsonObject.get("count");
            }

            if (jsonObject.has("personal")) {
                personalNotifCount = (Integer) jsonObject.get("personal");
            }

            if (jsonObject.has("all")){
                allNotifCount = (Integer) jsonObject.get("all");
            }

        } catch (JSONException e) {
            return;
        }

        // Смог получить количество запросов?
        if (reqCount != null || personalNotifCount != null || allNotifCount != null) {

            // Если количество - 0, то убираем все оповещения
            if (reqCount == 0 && personalNotifCount == 0 && allNotifCount == 0) {
                spanReqCount.setVisible(false);
                lbReqCount.setVisible(false);

                currentCountOfRequest = reqCount;
                currentCountOfPersonalNotifications = personalNotifCount;
                currentCountOfAllNotifications = allNotifCount;

                setCurrentCountOfRequest(currentCountOfRequest);
                setCurrentCountOfPersonalNotifications(currentCountOfPersonalNotifications);
                setCurrentCountOfAllNotifications(currentCountOfAllNotifications);

                return;
            } else {
                spanReqCount.setVisible(true);
                lbReqCount.setVisible(true);
            }

            // Достаем количество из сессии
            currentCountOfRequest = getCurrentCountOfRequest();
            currentCountOfPersonalNotifications = getCurrentCountOfPersonalNotifications();
            currentCountOfAllNotifications = getCurrentCountOfAllNotifications();

            // Если количество не поменялось, нам тут делать нечего
            if (reqCount.equals(currentCountOfRequest)
                && personalNotifCount.equals(currentCountOfPersonalNotifications)
                && allNotifCount.equals(currentCountOfAllNotifications)) {
                return;
            }

            // Если пришло оповещение можно и музончик накатить
            if (reqCount > currentCountOfRequest
                || personalNotifCount > currentCountOfPersonalNotifications
                || allNotifCount > currentCountOfAllNotifications) {
                auNote.play();
                Clients.evalJavaScript("sendNotification('Необработанных запросов (" + reqCount + personalNotifCount + allNotifCount + ")',{ body: 'Новый запрос!', dir: 'auto', icon: '../imgs/calendar.png' })");
            }
            // Фиксируем в сессии и вызываем HTML5 нотификацию
            currentCountOfRequest = reqCount;
            currentCountOfAllNotifications = allNotifCount;
            currentCountOfPersonalNotifications = personalNotifCount;

            setCurrentCountOfRequest(currentCountOfRequest);
            setCurrentCountOfAllNotifications(currentCountOfAllNotifications);
            setCurrentCountOfPersonalNotifications(currentCountOfPersonalNotifications);

            lbReqCount.setValue(Integer.toString(currentCountOfPersonalNotifications + currentCountOfRequest + currentCountOfAllNotifications));
        }
    }

    @Listen("onClick = #divRequest")
    public void showRegister() {

        Popup popupNotifSources = new Popup();
        popupNotifSources.setParent(divRequest);

        Vbox vNotifSources = new Vbox();
        vNotifSources.setParent(popupNotifSources);

        List<NotificationLinkModel> sources = new ArrayList<>();

        if(currentCountOfRequest != 0) {
            sources.add(new NotificationLinkModel(currentCountOfRequest,"Заявки на открытие пересдач", "/register/index.zul"));
        }

        if(currentCountOfPersonalNotifications != 0){
            sources.add(new NotificationLinkModel(currentCountOfPersonalNotifications,"Уведомления", "/personalNotifications/index.zul"));
        }

        if(currentCountOfAllNotifications != 0){
            sources.add(new NotificationLinkModel(currentCountOfAllNotifications,"Центр уведомлений", "/notificationCenter/index.zul"));
        }

        //Если источник уведомлений только один, нет смысла показывать попап поэтому сразу переходим к источнику
        if(sources.size() == 1){
            Executions.sendRedirect(sources.get(0).getUri());
            return;
        }

        constructSourcePopup(vNotifSources, sources);

        popupNotifSources.open(divRequest, "after_end");
    }

    private void constructSourcePopup(Vbox parent, List<NotificationLinkModel> sources){
        Hbox hSource = new Hbox();
        hSource.setParent(parent);

        Vbox vName = new Vbox();
        vName.setParent(hSource);
        Vbox vCount = new Vbox();
        vCount.setParent(hSource);

        for(NotificationLinkModel notificationLinkModel : sources){
            Label lSourceName = new Label(notificationLinkModel.getName());
            lSourceName.setParent(vName);
            lSourceName.setStyle("text-decoration: underline; color: #E66E24; cursor: pointer;");
            lSourceName.addEventListener(Events.ON_CLICK, event -> Executions.sendRedirect(notificationLinkModel.getUri()));

            Separator separator = new Separator();
            separator.setHeight("5px");
            separator.setParent(vName);

            Label lCount = new Label(Integer.toString(notificationLinkModel.getCount()));
            lCount.setParent(vCount);
            lCount.setStyle("background: red; color: white; border-radius: 10px; padding: 3px; float: right");
        }
    }

    @Listen("onClick = #divProfile")
    public void showProfile() {
        Executions.sendRedirect("/");
    }

    @Listen("onClick = #divOverlay")
    public void clickOnDivOverlay() {
        divSidebar.setSclass("sidebar close");
    }

    @Listen("onClick = #spanShowNav")
    public void showNav() {
        if (divSidebar.getSclass().equals("sidebar close")) {
            divSidebar.setSclass("sidebar");
            spanShowNav.setSclass("z-icon-angle-double-up");
        } else {
            divSidebar.setSclass("sidebar close");
            spanShowNav.setSclass("z-icon-angle-double-down");
        }
    }

    @Listen("onClick = #spanCogs")
    public void clickOnCogs() {
        if (!getCurrentUser().isStudent()) {
            hbTemplateNotification.setVisible(false);
        }
        popupTemplateCogs.open(spanCogs);
    }

    @Listen("onClick = #hbTemplateQuestion")
    public void showMessageBoxQuestion() {
        Messagebox.show(
                "На данный момент Вы можете сообщить о проблеме, отправив ее описание по адресу dec.developers@gmail.com"
                , "Внимание!", Messagebox.OK, org.zkoss.zul.Messagebox.INFORMATION);
    }
    @Listen("onClick = #hbTemplateDownloadGuide")
    public void downloadUsersManual() {
        Messagebox.show(
                "Загрузить руководство пользователя с подробным изложением функционала АСУ ИКИТ?"
                , "Внимание!", Messagebox.YES | org.zkoss.zul.Messagebox.NO, org.zkoss.zul.Messagebox.INFORMATION, (EventListener) event -> {
                    if (event.getName().equals("onYes")) {
                        String path = "C:/FileServer/users guide/Руководство Пользователя АСУ ИКИТ.pdf";
                        String contentType = "application/pdf";
                        Filedownload.save(new File(path), contentType);
                    }
                });
    }

    @Listen("onClick = #hbTemplateNotification")
    public void goToNotification() {
        PopupUtil.showError("Модуль пока не доступен");
        //Executions.sendRedirect("/msg");
    }

    @Listen("onClick = #hbTemplateExit")
    public void exit() {
        Executions.getCurrent().getSession().setAttribute(CURRENT_USER, null);
        Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, null);
        Executions.getCurrent().getSession().setAttribute(AuthInit.FROM_PAGE, null);
        Executions.sendRedirect("/login.zul");
    }

    private void generateDiv() {
        if (getCurrentModule() != null) {
            userService.changeSelected(getCurrentModule(), getCurrentUser());
        }

        lCurrentUser.setValue(getCurrentUser().getShortFIO());
        Ul ul = new Ul();
        ul.setParent(divSidebar);
        ul.setSclass("side-nav");
        for (final RoleModel role : userModel.getRoles()) {
            if (!role.isShow()) {
                continue;
            }

            Li li = new Li();
            li.setParent(ul);
            if (role.isSingle()) {
                li.setSclass("single");
                final ModuleModel module = role.getModules().get(0);
                A a = new A(module.getName());
                a.setParent(li);
                a.setSclass(role.isSelected() ? "b" : "");
                a.setStyle("padding-left: 18px");
                a.addEventListener(Events.ON_CLICK, event -> {
                    Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                    Executions.sendRedirect(module.getUrl());
                });
                continue;
            }
            final A aRole = new A(role.getName());
            aRole.setParent(li);
            aRole.setStyle("border-left: 8px solid #E66E24;");
            aRole.setSclass(role.isSelected() ? "b" : "");

            final Div divCategory = new Div();
            divCategory.setParent(li);
            divCategory.setSclass("side-nav-cat");
            divCategory.setStyle(role.isOpenTree() ? "display: block;" : "display: none;");

            Ul ulRole = new Ul();
            ulRole.setParent(divCategory);

            for (final ModuleModel module : role.getModules()) {
                Li liModule = new Li();
                liModule.setParent(ulRole);

                A aModule = new A(module.getName());
                aModule.setParent(liModule);
                aModule.setSclass(module.isSelected() ? "waves-effect b" : "waves-effect");
                aModule.addEventListener(Events.ON_CLICK, event -> {
                    Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                    Executions.sendRedirect(module.getUrl());
                });
            }
            aRole.addEventListener(Events.ON_CLICK, event -> {
                if (role.isOpenTree()) {
                    role.setOpenTree(false);
                    divCategory.setStyle("display: none;");
                } else {
                    role.setOpenTree(true);
                    divCategory.setStyle("display: block;");
                }
            });
        }
    }

    private void changeCurrentPageName(Page page, String name) {
        page.setTitle(name);
        lPageName = (Label) Executions.getCurrent().getDesktop().getFirstPage().getFellowIfAny("lPageName");
        lPageName.setValue(name);
    }

    public boolean checkModuleByRoleAndPath(String path) {
        path = path.replace("index.zul", "");
        if (path.length() > 1 && path.substring(path.length() - 1, path.length()).equals("/")) {
            path = path.substring(0, path.length() - 1);
        }
        ModuleModel module;
        if (getCurrentModule() != null) {
            if (getCurrentModule().getUrl().equals(path)) {
                return true;
            }
            module = userService.getModuleByRoleAndPath(path, getCurrentModule().getRole());
            if (module != null) {
                return true;
            } else {
               return checkModuleByUserAndPath(path);
            }
        } else {
            return checkModuleByUserAndPath(path);
        }
    }

    private boolean checkModuleByUserAndPath(String path) {
        ModuleModel module = userService.getModuleByUserAndPath(path, getCurrentUser());
        if (module != null) {
            if (module.getUrl().equals(path)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    public void checkModuleByRole(String path, Page page) {
        path = path.replace("index.zul", "");
        if (path.length() > 1 && path.substring(path.length() - 1).equals("/")) {
            path = path.substring(0, path.length() - 1);
        }
        ModuleModel module;
        if (getCurrentModule() != null) {
            if (getCurrentModule().getUrl().equals(path)) {
                changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                        ? getCurrentModule().getName()
                        : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName()
                );
                return;
            }
            module = userService.getModuleByRoleAndPath(path, getCurrentModule().getRole());
            if (module != null) {
                Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
                changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                        ? getCurrentModule().getName()
                        : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName());
                Executions.sendRedirect(module.getUrl());
            } else {
                checkModuleByUser(path, page);
            }
        } else {
            checkModuleByUser(path, page);
        }
    }

    private void checkModuleByUser(String path, Page page) {
        ModuleModel module = userService.getModuleByUserAndPath(path, getCurrentUser());
        if (module != null) {
            Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, module);
            changeCurrentPageName(page, getCurrentModule().getRole().isSingle()
                    ? getCurrentModule().getName()
                    : getCurrentModule().getRole().getName() + " / " + getCurrentModule().getName());
            if (!module.getUrl().equals(path)) {
                Executions.sendRedirect(module.getUrl());
            }
        } else {
            Executions.getCurrent().getSession().setAttribute(CURRENT_MODULE, null);
            Executions.sendRedirect("/");
        }
    }

    public boolean checkUserRightsForModule(String path) {
        path = path.replace("index.zul", "");
        if (path.length() > 1 && path.substring(path.length() - 1).equals("/")) {
            path = path.substring(0, path.length() - 1);
        }
        ModuleModel module = userService.getModuleByUserAndPath(path, getCurrentUser());
        return module != null;
    }

    public void setVisitedModuleByHum(Long idHum, Long idModule) {
        userService.setVisitedModuleByHum(idHum, idModule);
    }

    public ModuleModel getCurrentModule() {
        return (ModuleModel) Executions.getCurrent().getSession().getAttribute(CURRENT_MODULE);
    }

    public UserModel getCurrentUser() {
        return Executions.getCurrent() == null
                ? null
                : (UserModel) Executions.getCurrent().getSession().getAttribute(CURRENT_USER);
    }

    public Integer getCurrentCountOfRequest() {
        return Executions.getCurrent() == null
                ? null
                : (Integer) Executions.getCurrent().getSession().getAttribute(CURRENT_COUNT_REQUEST);
    }

    public void setCurrentCountOfRequest(Integer count) {
       if (Executions.getCurrent() != null) {
           Executions.getCurrent().getSession().setAttribute(CURRENT_COUNT_REQUEST, count);
       }
    }

    public Integer getCurrentCountOfPersonalNotifications() {
        return Executions.getCurrent() == null
               ? null
               : (Integer) Executions.getCurrent().getSession().getAttribute(CURRENT_COUNT_PERSONAL_NOTIFICATIONS);
    }

    public void setCurrentCountOfPersonalNotifications(Integer count) {
        if (Executions.getCurrent() != null) {
            Executions.getCurrent().getSession().setAttribute(CURRENT_COUNT_PERSONAL_NOTIFICATIONS, count);
        }
    }

    public Integer getCurrentCountOfAllNotifications() {
        return Executions.getCurrent() == null
               ? null
               : (Integer) Executions.getCurrent().getSession().getAttribute(CURRENT_COUNT_ALL_NOTIFICATIONS);
    }

    public void setCurrentCountOfAllNotifications(Integer count) {
        if (Executions.getCurrent() != null) {
            Executions.getCurrent().getSession().setAttribute(CURRENT_COUNT_ALL_NOTIFICATIONS, count);
        }
    }

}