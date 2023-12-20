package org.edec.logs.model;

import java.time.LocalDate;

public class LogModel {

    private LocalDate date;
    private String time;
    private String level;
    private String text;

    public LogModel(LocalDate date, String time, String level, String text) {
        this.date = date;
        this.time = time;
        this.level = level;
        this. text = text;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

