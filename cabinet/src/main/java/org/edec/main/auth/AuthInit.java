package org.edec.main.auth;

import org.edec.main.ctrl.TemplatePageCtrl;
import org.edec.main.model.UserModel;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

import java.util.Map;


public class AuthInit implements Initiator {
    public static final String FROM_PAGE = "from_page";

    @Override
    public void doInit (Page page, Map<String, Object> map) throws Exception {
        UserModel currentUser = new TemplatePageCtrl().getCurrentUser();

        if (currentUser == null) {
            Executions.getCurrent().getDesktop().getSession().setAttribute(FROM_PAGE, page.getRequestPath());
            Executions.sendRedirect("/login.zul");
        }
    }
}