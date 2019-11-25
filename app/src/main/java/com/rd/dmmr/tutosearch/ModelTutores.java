package com.rd.dmmr.tutosearch;

import java.util.ArrayList;

public class ModelTutores {

    String UIDProf, nombres, apellidos, about_me, provincia, url_pic, url_thumb_pic;
    ArrayList<String> Materias;

    public ModelTutores(){

    }

    public ModelTutores(String UIDProf, String nombres, String apellidos, String about_me, String provincia, String url_pic, String url_thumb_pic, ArrayList<String> materias) {
        this.UIDProf = UIDProf;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.about_me = about_me;
        this.provincia = provincia;
        this.url_pic = url_pic;
        this.url_thumb_pic = url_thumb_pic;
        Materias = materias;
    }

    public String getUIDProf() {
        return UIDProf;
    }

    public void setUIDProf(String UIDProf) {
        this.UIDProf = UIDProf;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getAbout_me() {
        return about_me;
    }

    public void setAbout_me(String about_me) {
        this.about_me = about_me;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getUrl_pic() {
        return url_pic;
    }

    public void setUrl_pic(String url_pic) {
        this.url_pic = url_pic;
    }

    public String getUrl_thumb_pic() {
        return url_thumb_pic;
    }

    public void setUrl_thumb_pic(String url_thumb_pic) {
        this.url_thumb_pic = url_thumb_pic;
    }

    public ArrayList<String> getMaterias() {
        return Materias;
    }

    public void setMaterias(ArrayList<String> materias) {
        Materias = materias;
    }
}
