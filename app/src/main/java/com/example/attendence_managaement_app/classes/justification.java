package com.example.attendence_managaement_app.classes;

import java.sql.Timestamp;

public class justification {
    private String id, idStudent, idAttendance;
    private boolean stat;
    private Timestamp time;

    public justification() {
    }

    public justification(String id, String idStudent, String idAttendance, boolean stat, Timestamp time) {
        this.id = id;
        this.idStudent = idStudent;
        this.idAttendance = idAttendance;
        this.stat = stat;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getIdAttendance() {
        return idAttendance;
    }

    public void setIdAttendance(String idAttendance) {
        this.idAttendance = idAttendance;
    }

    public boolean isStat() {
        return stat;
    }

    public void setStat(boolean stat) {
        this.stat = stat;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
