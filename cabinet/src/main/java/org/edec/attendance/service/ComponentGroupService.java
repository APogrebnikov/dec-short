package org.edec.attendance.service;


import org.zkoss.zul.Combobox;

public interface ComponentGroupService {

    void fillCmbGroupsWithSemester (Combobox cmbGroup, Long idSem, String courses, String levels);

}
