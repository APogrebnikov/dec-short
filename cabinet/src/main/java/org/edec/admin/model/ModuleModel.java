package org.edec.admin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ModuleModel {
    private Boolean readonly;

    private Integer formofstudy;

    private Long idModule;
    private Long idModuleRoleDep;

    private String departmentTitle;
    private String instituteTitle;
    private String name;
}
