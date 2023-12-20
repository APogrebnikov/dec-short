package org.edec.admin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DepartmentModel {
    private Long idDepartment;

    private String departmentTitle;
    private String instituteTitle;
}
