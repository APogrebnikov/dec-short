package org.edec.notification.model;

import org.edec.model.HumanfaceModel;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private String department;

    private List<HumanfaceModel> humans = new ArrayList<>();
    private List<HumanfaceModel> selectedHumans = new ArrayList<>();

    public Department () {
    }

    public String getDepartment () {
        return department;
    }

    public void setDepartment (String department) {
        this.department = department;
    }

    public List<HumanfaceModel> getHumans () {
        return humans;
    }

    public void setHumans (List<HumanfaceModel> humans) {
        this.humans = humans;
    }

    public List<HumanfaceModel> getSelectedHumans () {
        return selectedHumans;
    }

    public void setSelectedHumans (List<HumanfaceModel> selectedHumans) {
        this.selectedHumans = selectedHumans;
    }
}
