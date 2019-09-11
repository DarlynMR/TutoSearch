package com.rd.dmmr.tutosearch;

/**
 * Created by Owner on 11/3/2018.
 */

public class ModelTutorias {

    public String foto;
    public String idTuto, idProf, materias, titulo, descripcion, profesores, fecha, hora, lugar, tiemporestante;

    public ModelTutorias(){

    }

    public  ModelTutorias(String idTuto, String idProf, String foto, String materias, String titulo, String descripcion, String profesores, String fecha, String hora, String lugar, String tiemporestante) {
        this.idTuto=idTuto;
        this.idProf=idProf;
        this.foto = foto;
        this.materias = materias;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.profesores = profesores;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
        this.tiemporestante = tiemporestante;
    }


}