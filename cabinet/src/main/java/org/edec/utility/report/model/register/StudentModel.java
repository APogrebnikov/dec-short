package org.edec.utility.report.model.register;


public class StudentModel {
    private Integer rating;

    private String fio;
    private String mark;
    private String recordBook;
    private String themeOfWork;

    public StudentModel () {
    }

    public String getFio () {
        return fio;
    }

    public void setFio (String fio) {
        this.fio = fio;
    }

    public String getRecordBook () {
        return recordBook;
    }

    public void setRecordBook (String recordBook) {
        this.recordBook = recordBook;
    }

    public String getThemeOfWork () {
        return themeOfWork;
    }

    public void setThemeOfWork (String themeOfWork) {
        this.themeOfWork = themeOfWork;
    }

    public Integer getRating () {
        return rating;
    }

    public void setRating (Integer rating) {
        this.rating = rating;
    }

    public String getMark () {
        String result = "";
        if (rating == 5) {
            result = "отлично";
        } else if (rating == 4) {
            result = "хорошо";
        } else if (rating == 3) {
            result = "удовл.";
        } else if (rating == 2) {
            result = "неудовл.";
        } else if (rating == 1) {
            result = "зачтено";
        } else if (rating == -2) {
            result = "не зачтено";
        } else if (rating == -3) {
            result = "не явился";
        }
        return result;
    }

    public void setMark (String mark) {
        this.mark = mark;
    }
}
