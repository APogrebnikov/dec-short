package org.edec.profile.service;

import org.edec.main.model.ModuleModel;
import org.edec.main.model.UserModel;
import org.edec.profile.model.ProfileModel;

import java.util.Date;
import java.util.List;


public interface ProfileService {
    ProfileModel getProfileByHum (Long idHum);
    boolean updateBirthDay (Long idHum, Date dateBirthDay);
    boolean updateEmail (Long idHum, String email);
    boolean updateGetNotification (Long idHum, Boolean getNotification);
    boolean updateStartPage (String path, Long idHum, Long idParent);
    List<ModuleModel> getDistinctModulesByUser (UserModel user);
    String getNameStartPage (UserModel user);
    boolean isEmployee (Long idHumanface);
}
