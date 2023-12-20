package org.edec.signEditor.model;

public class EmployeeModel {
    private long IdEmp;
    private String fio;

    public long getIdEmp () {
        return IdEmp;
    }

    public void setIdEmp (long idEmp) {
        IdEmp = idEmp;
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    @Override
    public boolean equals (Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof EmployeeModel) {
            if (((EmployeeModel) object).IdEmp == this.IdEmp && ((EmployeeModel) object).fio.equals(this.fio)) {
                sameSame = true;
            }
        }

        return sameSame;
    }
}
