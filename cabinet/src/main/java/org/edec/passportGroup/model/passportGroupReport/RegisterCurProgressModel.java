package org.edec.passportGroup.model.passportGroupReport;

/**
 * Created by dmmax
 */
public class RegisterCurProgressModel
{
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
