package com.example.attendence_managaement_app.classes;

import com.google.firebase.Timestamp;

public class attendance {
    private String id, idCourse;
    private Boolean stat;
    private Timestamp time;

    public attendance() {
    }

    public attendance(String id, String idCourse, Boolean stat, Timestamp time) {
        this.id = id;
        this.idCourse = idCourse;
        this.stat = stat;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCourse() {
        return idCourse;
    }

    public void setIdCourse(String idCourse) {
        this.idCourse = idCourse;
    }

    public Boolean getStat() {
        return stat;
    }

    public void setStat(Boolean stat) {
        this.stat = stat;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
