package org.edec.passportGroup.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.ArrayList;
import java.util.List;

public class RegisterCurProgressModel {

    private int attend;
    private int progress;

    private String fio;
    private String subjectname;

    public RegisterCurProgressModel()
    {
    }

    public int getAttend()
    {
        return attend;
    }

    public void setAttend(int attend)
    {
        this.attend = attend;
    }

    public String getFio()
    {
        return fio;
    }

    public void setFio(String fio)
    {
        this.fio = fio;
    }

    public String getSubjectname()
    {
        return subjectname;
    }

    public void setSubjectname(String subjectname)
    {
        this.subjectname = subjectname;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setProgress(int progress)
    {
        this.progress = progress;
    }
}
