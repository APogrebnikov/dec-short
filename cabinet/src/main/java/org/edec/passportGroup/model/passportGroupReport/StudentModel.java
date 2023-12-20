package org.edec.passportGroup.model.passportGroupReport;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by dmmax on 04.03.16.
 */
public class StudentModel
{
    private String fio;
    private String government;
    private String prolongation;

    private String result;

    private List<StudentResult> results;

    private Long id_sss;

    private int countSubject;
    private int passCountSubject;

    private List<SubjectModel> exams = new ArrayList<>();
    private List<SubjectModel> pass = new ArrayList<>();
    private List<SubjectModel> cp = new ArrayList<>();
    private List<SubjectModel> cw = new ArrayList<>();
    private List<SubjectModel> practices = new ArrayList<>();
    private List<SubjectModel> difPass = new ArrayList<>();


    public StudentModel(String fio, String government, String prolongation, Long id_sss)
    {
        this.fio = fio;
        this.government = government;
        this.prolongation = prolongation;
        this.id_sss = id_sss;
        this.countSubject = 0;
        this.passCountSubject = 0;
        this.results = new ArrayList<>();
    }


    public Long getId_sss()
    {
        return id_sss;
    }

    public void setId_sss(Long id_sss)
    {
        this.id_sss = id_sss;
    }

    public int getCountSubject()
    {
        return countSubject;
    }

    public void setCountSubject(int countSubject)
    {
        this.countSubject = countSubject;
    }

    public int getPassCountSubject()
    {
        return passCountSubject;
    }

    public void setPassCountSubject(int passCountSubject)
    {
        this.passCountSubject = passCountSubject;
    }

    public String getFio()
    {
        return fio;
    }

    public void setFio(String fio)
    {
        this.fio = fio;
    }

    public String getGovernment()
    {
        return government;
    }

    public void setGovernment(String government)
    {
        this.government = government;
    }

    public String getProlongation()
    {
        return prolongation;
    }

    public void setProlongation(String prolongation)
    {
        this.prolongation = prolongation;
    }

    public List<SubjectModel> getExams()
    {
        return exams;
    }

    public void setExams(List<SubjectModel> exams)
    {
        this.exams = exams;
    }

    public List<SubjectModel> getPass()
    {
        return pass;
    }

    public void setPass(List<SubjectModel> pass)
    {
        this.pass = pass;
    }

    public List<SubjectModel> getCp()
    {
        return cp;
    }

    public void setCp(List<SubjectModel> cp)
    {
        this.cp = cp;
    }

    public List<SubjectModel> getPractices()
    {
        return practices;
    }

    public void setPractices(List<SubjectModel> practices)
    {
        this.practices = practices;
    }

    public List<SubjectModel> getCw()
    {
        return cw;
    }

    public void setCw(List<SubjectModel> cw)
    {
        this.cw = cw;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public List<StudentResult> getResults()
    {
        return results;
    }

    public void setResults(List<StudentResult> results)
    {
        this.results = results;
    }

    public List<SubjectModel> getDifPass() {
        return difPass;
    }

    public void setDifPass(List<SubjectModel> difPass) {
        this.difPass = difPass;
    }
}
