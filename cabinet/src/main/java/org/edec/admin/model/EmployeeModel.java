package org.edec.admin.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class EmployeeModel {
    private Long idEmp;
    private String fio;

    @Override
    public String toString () {
        return fio;
    }
}
