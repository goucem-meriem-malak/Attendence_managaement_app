package com.example.attendence_managaement_app.classes;

import android.os.Parcelable;

import java.util.List;

public class user {
    private String uid, first_name, last_name, email, role, department, faculty, major, degree, speciality, group;
    private List<course> courses;
    private List<notification> notifications;
    private List<attendance> attendances;

    public user(String uid, String first_name, String last_name, String email, String role, String department, String faculty, String major, String degree, String speciality, String group, List<course> courses, List<notification> notifications, List<attendance> attendances) {
        this.uid = uid;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.role = role;
        this.department = department;
        this.faculty = faculty;
        this.major = major;
        this.degree = degree;
        this.speciality = speciality;
        this.group = group;
        this.courses = courses;
        this.notifications = notifications;
        this.attendances = attendances;
    }

    public user() {
    }

    public <T extends Parcelable> user(Object user) {
    }

    public String getUid() {
        return uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public List<course> getCourses() {
        return courses;
    }

    public void setCourses(List<course> courses) {
        this.courses = courses;
    }

    public List<notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<notification> notifications) {
        this.notifications = notifications;
    }

    public List<attendance> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<attendance> attendances) {
        this.attendances = attendances;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
