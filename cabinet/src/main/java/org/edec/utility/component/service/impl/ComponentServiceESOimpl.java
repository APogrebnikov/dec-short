package org.edec.utility.component.service.impl;

import org.edec.main.model.DepartmentModel;
import org.edec.utility.component.manager.ComponentManager;
import org.edec.utility.component.renderer.*;
import org.edec.utility.constants.FormOfStudy;
import org.edec.utility.component.model.GroupModel;
import org.edec.utility.component.model.InstituteModel;
import org.edec.utility.component.service.ComponentService;
import org.edec.utility.constants.GovFinancedConst;
import org.edec.utility.constants.LevelConst;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

import java.util.ArrayList;
import java.util.List;


public class ComponentServiceESOimpl implements ComponentService {
    private ComponentManager componentManager = new ComponentManager();

    @Override
    public Listcell createListcell(Listitem li, String value, String lStyle, String lSclass, String lcStyle) {
        Listcell lc = new Listcell();
        lc.setParent(li);
        lc.setStyle(lcStyle);
        Label l = new Label(value);
        l.setStyle(lStyle);
        l.setSclass(lSclass);
        l.setParent(lc);
        return lc;
    }

    @Override
    public Listheader getListheader(String label, String width, String hflex, String align) {
        Listheader lhr = new Listheader();
        Label lLhr = new Label(label);
        lLhr.setParent(lhr);
        lLhr.setSclass("cwf-listheader-label");
        lhr.setWidth(width);
        //lhr.setHflex(hflex);
        lhr.setAlign(align);
        return lhr;
    }

    @Override
    public InstituteModel fillCmbInst(final Combobox cmbInst, Component component, List<DepartmentModel> departments) {
        List<InstituteModel> institutes = new ArrayList<>();
        for (DepartmentModel department : departments) {
            if (department.getInstitute() == null) {
                institutes = new ArrayList<>();
                institutes.add(new InstituteModel(null, null, null, "Все"));
                institutes.addAll(componentManager.getAll());
                break;
            }
            institutes.add(new InstituteModel(department.getIdInstitute(), department.getIdInstituteMine(), department.getFulltitle(),
                    department.getShorttitle()
            ));
        }
        cmbInst.setModel(new ListModelList<>(institutes));
        cmbInst.setItemRenderer(new InstituteRenderer());
        cmbInst.addEventListener("onAfterRender", event -> {
            if (cmbInst.getItemCount() > 0) {
                cmbInst.setSelectedIndex(0);
            }
        });

        if (institutes.size() == 1) {
            component.setVisible(false);
        }

        return institutes.get(0);
    }

    @Override
    public InstituteModel fillCmbInst(Combobox cmbInst, Component component, List<DepartmentModel> departments, boolean isNeedAll) {
        // TODO переосмыслить эту функцию и функцию сверху. Сразу хорошего решения подобрать не смог

        List<InstituteModel> institutes = new ArrayList<>();
        for (DepartmentModel department : departments) {
            if (department.getInstitute() == null) {
                institutes = new ArrayList<>();
                institutes.add(new InstituteModel(null, null, null, "Все"));
                institutes.addAll(componentManager.getAll());
                break;
            }
            institutes.add(new InstituteModel(department.getIdInstitute(), department.getIdInstituteMine(), department.getFulltitle(),
                    department.getShorttitle()
            ));
        }

        if (!isNeedAll && institutes.get(0).getShorttitle() != null && institutes.get(0).getShorttitle().equals("Все")) {
            institutes.remove(0);
        }

        cmbInst.setModel(new ListModelList<>(institutes));
        cmbInst.setItemRenderer(new InstituteRenderer());
        cmbInst.addEventListener("onAfterRender", event -> {
            if (cmbInst.getItemCount() > 0) {
                cmbInst.setSelectedIndex(0);
            }
        });
        if (institutes.size() == 1) {
            component.setVisible(false);
        }

        return institutes.get(0);
    }

    @Override
    public void fillCmbDepartment(final Combobox cmbDepartment, Component component, List<DepartmentModel> departments) {
        cmbDepartment.setItemRenderer(new DepartmentRenderer());
        cmbDepartment.setModel(new ListModelList<>(departments));
        cmbDepartment.addEventListener("onAfterRender", event -> {
            if (cmbDepartment.getItemCount() > 0) {
                cmbDepartment.setSelectedIndex(0);
            }
        });

        if (departments.size() == 1) {
            component.setVisible(false);
        }
    }

    @Override
    public FormOfStudy fillCmbFormOfStudy(final Combobox cmbFormOfStudy, Component component, Integer fosInt) {
        FormOfStudy formOfStudy = FormOfStudy.getFormOfStudyByType(fosInt);
        if (formOfStudy == FormOfStudy.ALL) {
            for (FormOfStudy fos : FormOfStudy.values()) {
                Comboitem ci = new Comboitem(fos.getName());
                ci.setValue(fos);
                ci.setParent(cmbFormOfStudy);
            }
        } else {
            Comboitem ci = new Comboitem(formOfStudy.getName());
            ci.setValue(formOfStudy);
            ci.setParent(cmbFormOfStudy);
            component.setVisible(false);
        }
        cmbFormOfStudy.setSelectedIndex(0);
        return formOfStudy;
    }

    @Override
    public FormOfStudy fillCmbFormOfStudy(Combobox cmbFormOfStudy, Component component, Integer fosInt, boolean isNeedAll) {
        FormOfStudy formOfStudy = fillCmbFormOfStudy(cmbFormOfStudy, component, fosInt);

        if (!isNeedAll && formOfStudy == FormOfStudy.ALL) {
            cmbFormOfStudy.removeItemAt(0);
            cmbFormOfStudy.setSelectedIndex(0);
            formOfStudy = cmbFormOfStudy.getSelectedItem().getValue();
        }

        return formOfStudy;
    }

    @Override
    public Hbox createHboxForFillWeekDay() {
        Hbox hboxWeek = createHboxWithFlex();

        createHboxWithLabel("ПН", "week").setParent(hboxWeek);
        createHboxWithLabel("ВТ", "week").setParent(hboxWeek);
        createHboxWithLabel("СР", "week").setParent(hboxWeek);
        createHboxWithLabel("ЧТ", "week").setParent(hboxWeek);
        createHboxWithLabel("ПТ", "week").setParent(hboxWeek);
        createHboxWithLabel("СБ", "week").setParent(hboxWeek);
        createHboxWithLabel("ВС", "week").setParent(hboxWeek);

        return hboxWeek;
    }

    @Override
    public Hbox createHboxWithLabel(String label, String sclass) {
        Hbox hbox = createHboxWithFlex();
        hbox.setSclass(sclass);
        hbox.setAlign("center");
        hbox.setPack("center");
        new Label(label).setParent(hbox);
        return hbox;
    }

    @Override
    public Hbox createHboxWithFlex() {
        Hbox hbox = new Hbox();
        hbox.setHflex("1");
        hbox.setVflex("1");
        return hbox;
    }

    @Override
    public void fillListboxGroup(final Listbox lbGroup, boolean multiple, Long idSem, String qualification) {
        List<GroupModel> listGroup = new ArrayList<>(componentManager.getGroupBySem(idSem, qualification));
        ListModelList<GroupModel> lmGroup = new ListModelList<>(listGroup);
        lmGroup.setMultiple(multiple);
        lbGroup.setItemRenderer(new GroupRenderer());
        lbGroup.setMultiple(multiple);
        lbGroup.setCheckmark(multiple);
        lbGroup.setAttribute("data", listGroup);
        lbGroup.setModel(lmGroup);
        lbGroup.renderAll();
        Clients.showBusy(lbGroup, "Загрузка данных");
        lbGroup.addEventListener("onFill", event -> Clients.clearBusy(lbGroup));
        Events.echoEvent("onFill", lbGroup, null);
    }

    @Override
    public void fillCmbSem(Combobox cmbSem, Long idInst, Integer formOfStudy, Integer season) {
        cmbSem.setItemRenderer(new SemesterRenderer());
        cmbSem.setModel(new ListModelList<>(componentManager.getSemester(idInst, formOfStudy, season)));
    }

    @Override
    public void fillCmbYear(Combobox cmbYear) {
        cmbYear.setItemRenderer(new YearRenderer());
        cmbYear.setModel(new ListModelList<>(componentManager.getYears()));
    }

    @Override
    public GovFinancedConst fillCmbGovFinanced(Combobox cmbGovFinanced) {
        List<GovFinancedConst> list = new ArrayList<GovFinancedConst>() {{
            add(GovFinancedConst.ALL);
            add(GovFinancedConst.GOV_FINANCED);
            add(GovFinancedConst.CONTRACT);
        }};

        for (GovFinancedConst govFinancedConst : list) {
            Comboitem ci = new Comboitem(govFinancedConst.getName());
            ci.setValue(govFinancedConst);
            ci.setParent(cmbGovFinanced);
        }

        cmbGovFinanced.setSelectedIndex(0);
        return GovFinancedConst.ALL;
    }

    @Override
    public LevelConst fillCmbLevel(Combobox cmbLevel) {
        List<LevelConst> list = new ArrayList<LevelConst>() {{
            add(LevelConst.ALL);
            add(LevelConst.BACH);
            add(LevelConst.MAGISTR);
            add(LevelConst.SPEC);
        }};

        for (LevelConst levelConst : list) {
            Comboitem ci = new Comboitem(levelConst.getName());
            ci.setValue(levelConst);
            ci.setParent(cmbLevel);
        }

        cmbLevel.setSelectedIndex(0);
        return LevelConst.ALL;
    }

    @Override
    public void fillCmbGroups(Combobox cmbGroup, Long idSemester) {
        fillCmbGroups(cmbGroup, idSemester, null, null);
    }

    @Override
    public void fillCmbGroups(Combobox cmbGroup, Long idSemester, String courses, String levels) {
        cmbGroup.setItemRenderer(new GroupRenderer());
        cmbGroup.setModel(new ListModelList<>(componentManager.getGroupBySem(idSemester, levels, courses)));
    }

    @Override
    public void fillCmbGroupsWithEmpty(Combobox cmbGroup, Long idSemester, String courses, String levels) {
        cmbGroup.setItemRenderer(new GroupRenderer());
        ListModelList groupList = new ListModelList<>();
        GroupModel gm = new GroupModel();
        gm.setGroupname("-");
        gm.setIdDG(null);
        groupList.add(gm);
        groupList.addAll(componentManager.getGroupBySem(idSemester, levels, courses));
        cmbGroup.setModel(groupList);
    }

    @Override
    public void searchInListbox(final Listbox lbGroup, String groupname, Integer course, Boolean multiple) {
        List<GroupModel> listGroup = (List<GroupModel>) lbGroup.getAttribute("data");
        List<GroupModel> result;
        if (groupname == null && course == null) {
            result = listGroup;
        } else {
            result = new ArrayList<>();
            for (GroupModel group : listGroup) {
                if (groupname != null && group.getGroupname().contains(groupname) || course != null && group.getCourse().equals(course)) {
                    result.add(group);
                }
            }
        }
        ListModelList<GroupModel> lmGroup = new ListModelList<>(result);
        lmGroup.setMultiple(multiple);
        lbGroup.setCheckmark(multiple);
        lbGroup.setMultiple(multiple);
        lbGroup.setModel(lmGroup);
        lbGroup.renderAll();
        lbGroup.addEventListener("onFill", event -> Clients.clearBusy(lbGroup));
        Clients.showBusy(lbGroup, "Загрузка данных");
        Events.echoEvent("onFill", lbGroup, null);
    }

    @Override
    public void fillCmbChair(Combobox cmbChair) {
        cmbChair.setItemRenderer(new ChairRenderer());
        cmbChair.setModel(new ListModelList<>(componentManager.getChairs()));
    }
}
