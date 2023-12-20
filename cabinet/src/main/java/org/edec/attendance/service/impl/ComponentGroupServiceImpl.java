package org.edec.attendance.service.impl;

import org.edec.attendance.service.ComponentGroupService;
import org.edec.attendance.service.JournalOfAttendanceService;
import org.edec.attendance.ctrl.renderer.GroupRenderer;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;


public class ComponentGroupServiceImpl implements ComponentGroupService {

    private JournalOfAttendanceService journalOfAttendanceService = new JournalOfAttendanceServiceImpl();

    @Override
    public void fillCmbGroupsWithSemester (Combobox cmbGroup, Long idSemester, String courses, String levels)
    {
        cmbGroup.setItemRenderer(new GroupRenderer());
        cmbGroup.setModel(new ListModelList<>(journalOfAttendanceService.getGroupModelBySem(idSemester, levels, courses)));
    }
}
