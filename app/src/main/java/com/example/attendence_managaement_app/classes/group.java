package com.example.attendence_managaement_app.classes;

import java.util.List;

public class group {
    private String group, idSpeciality;
    private List<String> listOfCourses, listOfStudents;

    public group() {
    }

    public group(String group, String idSpeciality, List<String> listOfCourses, List<String> listOfStudents) {
        this.group = group;
        this.idSpeciality = idSpeciality;
        this.listOfCourses = listOfCourses;
        this.listOfStudents = listOfStudents;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getIdSpeciality() {
        return idSpeciality;
    }

    public void setIdSpeciality(String idSpeciality) {
        this.idSpeciality = idSpeciality;
    }

    public List<String> getListOfCourses() {
        return listOfCourses;
    }

    public void setListOfCourses(List<String> listOfCourses) {
        this.listOfCourses = listOfCourses;
    }

    public List<String> getListOfStudents() {
        return listOfStudents;
    }

    public void setListOfStudents(List<String> listOfStudents) {
        this.listOfStudents = listOfStudents;
    }
}
