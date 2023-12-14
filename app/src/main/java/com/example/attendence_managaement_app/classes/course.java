package com.example.attendence_managaement_app.classes;

public class course {
    private String id, name, idProf;

    public course(String id, String name, String idProf) {
        this.id = id;
        this.name = name;
        this.idProf = idProf;
    }

    public course() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdProf() {
        return idProf;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdProf(String idProf) {
        this.idProf = idProf;
    }

}
