package com.rd.dmmr.tutosearch;

/**
 * Created by Owner on 11/3/2018.
 */

public class ModelTutorias {

    public String url_imagePortada,url_thumbsPortada;
    public String idTuto, idProf, materia, titulo, descripcion, nombreProf, timestampI, timestampF,timestampPub, lugar, tipo_tuto;

    public ModelTutorias(){

    }

    public ModelTutorias(String url_imagePortada, String url_thumbsPortada, String idTuto, String idProf, String materia, String titulo, String descripcion, String nombreProf, String timestampI, String timestampF, String timestampPub, String lugar, String tipo_tuto) {
        this.url_imagePortada = url_imagePortada;
        this.url_thumbsPortada = url_thumbsPortada;
        this.idTuto = idTuto;
        this.idProf = idProf;
        this.materia = materia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreProf = nombreProf;
        this.timestampI = timestampI;
        this.timestampF = timestampF;
        this.timestampPub = timestampPub;
        this.lugar = lugar;
        this.tipo_tuto = tipo_tuto;
    }
}