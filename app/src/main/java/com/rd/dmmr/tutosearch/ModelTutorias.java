package com.rd.dmmr.tutosearch;

/**
 * Created by Owner on 11/3/2018.
 */

public class ModelTutorias {

    public String url_imagePortada,url_thumbsPortada;
    public String idTuto, idProf, materia, titulo, descripcion, nombreProf, fecha, hora, lugar, tipo_tuto, fecha_pub, hora_pub;

    public ModelTutorias(){

    }

    public ModelTutorias(String url_fotoPortada, String url_thumbsPortada, String idTuto, String idProf, String materia, String titulo, String descripcion, String nombreProf, String fecha, String hora, String lugar, String tipo_tuto, String fecha_pub, String hora_pub) {
        this.url_imagePortada = url_fotoPortada;
        this.url_thumbsPortada = url_thumbsPortada;
        this.idTuto = idTuto;
        this.idProf = idProf;
        this.materia = materia;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.nombreProf = nombreProf;
        this.fecha = fecha;
        this.hora = hora;
        this.lugar = lugar;
        this.tipo_tuto = tipo_tuto;
        this.fecha_pub = fecha_pub;
        this.hora_pub = hora_pub;
    }
}