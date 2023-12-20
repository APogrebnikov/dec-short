package org.edec.main.service.impl;

import org.apache.commons.lang3.tuple.Triple;
import org.edec.main.manager.UserManagerDAO;
import org.edec.main.model.DepartmentModel;
import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;
import org.edec.main.model.dao.UserRoleModuleESOmodel;
import org.edec.main.service.RequestService;
import org.edec.main.service.UserService;
import org.edec.register.manager.RegisterRequestManager;
import org.edec.register.model.RegisterRequestModel;
import org.edec.utility.constants.LevelConst;
import org.edec.utility.constants.RegisterRequestStatusConst;
import org.javatuples.Triplet;

import java.util.List;
import java.util.Set;


public class RequestServiceImpl implements RequestService {
    RegisterRequestManager manager = new RegisterRequestManager();

    @Override
    public Integer getRequestCount()
    {
        List<RegisterRequestModel> requestsList = manager.getAllRegisterRequests(1,3, RegisterRequestStatusConst.UNDER_CONSIDERATION);
        if (requestsList != null) {
            return requestsList.size();
        }
        return null;
    }

    @Override
    public Set<Triplet> getAllOpenRequestCount() {
        return manager.getAllOpenCounts();
    }

    @Override
    public int getAllNotificationCount(Long idHumanface) {
        return manager.getAllUnreadNotificationsCount(idHumanface);
    }

    @Override
    public int getPersonalNotificationCount(Long idHumanface) {
        return manager.getUnreadPersonalNotificationsCount(idHumanface);
    }
}