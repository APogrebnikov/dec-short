package org.edec.profile.service.impl;

import org.edec.main.model.ModuleModel;
import org.edec.main.model.RoleModel;
import org.edec.main.model.UserModel;
import org.edec.profile.manager.ProfileManager;
import org.edec.profile.model.ProfileModel;
import org.edec.profile.service.ProfileService;

import java.util.*;
import java.util.stream.Collectors;


public class ProfileServiceEsoImpl implements ProfileService {
    private ProfileManager profileManager = new ProfileManager();

    @Override
    public ProfileModel getProfileByHum (Long idHum) {
        return profileManager.getProfileByHumId(idHum);
    }

    @Override
    public boolean updateBirthDay (Long idHum, Date dateBirthDay) {
        return profileManager.updateBirthDay(idHum, dateBirthDay);
    }

    @Override
    public boolean updateEmail (Long idHum, String email) {
        return profileManager.updateEmail(idHum, email);
    }

    @Override
    public boolean updateGetNotification (Long idHum, Boolean getNotification) {
        return profileManager.updateGetNotification(idHum, getNotification);
    }

    @Override
    public List<ModuleModel> getDistinctModulesByUser (UserModel user) {
        List<ModuleModel> modules = new ArrayList<>();
        for (RoleModel role : user.getRoles()) {
            ModuleModel foundModule = role.getModules()
                                          .stream()
                                          .filter(tmpModule -> modules.stream()
                                                                      .filter(module -> module.getName().equals(tmpModule.getName()))
                                                                      .count() == 0)
                                          .findFirst()
                                          .orElse(null);
            if (foundModule != null) {
                modules.add(foundModule);
            }
        }
        modules.sort(Comparator.comparing(ModuleModel::getName));
        return modules;
    }

    @Override
    public boolean isEmployee(Long idHumanface) {
        List<Long> listIdEmployee = profileManager.getIdEmployee(idHumanface);
        if (listIdEmployee.isEmpty()){
            return  false;
        } else {
            return true;
        }
    }

    @Override
    public String getNameStartPage (UserModel user) {
        for (RoleModel role : user.getRoles()) {
            for (ModuleModel module : role.getModules()) {
                if (module.getUrl().equals(user.getStartPage())) {
                    return module.getName();
                }
            }
        }
        return null;
    }

    @Override
    public boolean updateStartPage (String path, Long idHum, Long idParent) {
        if (idParent != null) {
            return profileManager.updateStartPageForParent(path, idParent);
        }
        return profileManager.updateStartPage(path, idHum);
    }
}
