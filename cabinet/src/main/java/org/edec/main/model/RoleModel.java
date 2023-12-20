package org.edec.main.model;

import java.util.ArrayList;
import java.util.List;


public class RoleModel {
    private boolean openTree = false;
    private boolean selected;
    private boolean show = true;
    private boolean single;

    private String name;

    private List<ModuleModel> modules = new ArrayList<>();

    public RoleModel () {
    }

    public boolean isSingle () {
        return single;
    }

    public void setSingle (boolean single) {
        this.single = single;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public boolean isOpenTree () {
        return openTree;
    }

    public void setOpenTree (boolean openTree) {
        this.openTree = openTree;
    }

    public List<ModuleModel> getModules () {
        return modules;
    }

    public void setModules (List<ModuleModel> modules) {
        this.modules = modules;
    }

    public boolean isSelected () {
        return selected;
    }

    public void setSelected (boolean selected) {
        this.selected = selected;
    }

    public boolean isShow () {
        return show;
    }

    public void setShow (boolean show) {
        this.show = show;
    }
}
