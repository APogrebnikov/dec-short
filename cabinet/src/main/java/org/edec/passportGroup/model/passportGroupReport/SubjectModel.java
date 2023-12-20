package org.edec.passportGroup.model.passportGroupReport;

/**
 * Created by dmmax
 */
public class SubjectModel {
    private String formofcontrol;
    private String subject;
    private String rating;
    private String statistic;

    private int passCount;
    private int count;
    private Double hoursCount;

    public SubjectModel() {
        passCount = 0;
        count = 0;
    }

    public SubjectModel(String formofcontrol, String subject, String rating, Double hoursCount) {
        this.formofcontrol = formofcontrol;
        this.subject = subject;
        this.rating = rating;
        this.hoursCount = hoursCount;
    }

    public Double getHoursCount(){
        return  hoursCount;
    }

    public void setHoursCount(Double hoursCount) {
        this.hoursCount = hoursCount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFormofcontrol() {
        return formofcontrol;
    }

    public void setFormofcontrol(String formofcontrol) {
        this.formofcontrol = formofcontrol;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getStatistic() {
        return statistic;
    }

    public void setStatistic(String statistic) {
        this.statistic = statistic;
    }
}
