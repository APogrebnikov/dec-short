package org.edec.utility.component.service;

import org.edec.main.model.DepartmentModel;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.constants.LevelConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

import java.util.List;


public interface ComponentService {
    FormOfStudy fillCmbFormOfStudy (Combobox cmbFormOfStudy, Component component, Integer fosInt);
    FormOfStudy fillCmbFormOfStudy (Combobox cmbFormOfStudy, Component component, Integer fosInt, boolean isNeedAll);
    Hbox createHboxForFillWeekDay ();
    Hbox createHboxWithLabel (String label, String sclass);

    /**
     * Создает hbox с hflex и vflex 1
     */
    Hbox createHboxWithFlex ();
    Listheader getListheader (String label, String width, String hflex, String align);
    InstituteModel fillCmbInst (Combobox cmbInst, Component component, List<DepartmentModel> departments);
    InstituteModel fillCmbInst (Combobox cmbInst, Component component, List<DepartmentModel> departments, boolean isNeedAll);
    Listcell createListcell (Listitem li, String value, String lStyle, String lSclass, String lcStyle);
    void fillCmbDepartment (Combobox cmbDepartment, Component component, List<DepartmentModel> departments);
    void fillListboxGroup (Listbox lbGroup, boolean multiple, Long idSem, String qualification);
    void fillCmbSem (Combobox cmbSem, Long idInst, Integer formOfStudy, Integer season);
    void fillCmbYear (Combobox cmbYear);

    GovFinancedConst fillCmbGovFinanced (Combobox cmbGovFinanced);

    LevelConst fillCmbLevel (Combobox cmbLevel);

    void fillCmbChair (Combobox cmbChair);
    void fillCmbGroups (Combobox cmbGroup, Long idSem);
    void fillCmbGroups (Combobox cmbGroup, Long idSem, String courses, String levels);
    void fillCmbGroupsWithEmpty (Combobox cmbGroup, Long idSemester, String courses, String levels);
    void searchInListbox (Listbox lbGroup, String groupname, Integer course, Boolean multiple);
}
