package org.edec.main.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class ModuleModel {

    private boolean readonly, selected;

    private Integer formofstudy;
    private Integer type;

    private Long idModule;

    private String imagePath;
    private String name;
    private String url;

    private RoleModel role;
    private List<DepartmentModel> departments = new ArrayList<>();

    public Long getIdInstituteByFirstDepartment() {
        if (departments.size() == 0) {
            return null;
        }
        return departments.get(0).getIdInstitute();
    }
}
