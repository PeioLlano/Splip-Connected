package com.example.primerproyecto.Clases;

import java.io.Serializable;
import java.util.Date;

public class Gasto implements Serializable {

    private  Integer codigo;
    private  String titulo;
    private Float cantidad;
    private Persona autor;
    private Date fecha;
    private Float latitud;
    private Float longitud;

    public Gasto(Integer codigo, String titulo, Float cantidad, Persona autor, Date fecha, Float latitud, Float longitud) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.cantidad = cantidad;
        this.autor = autor;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public Float getCantidad() {
        return cantidad;
    }

    public Persona getAutor() {
        return autor;
    }

    public Date getFecha() {
        return fecha;
    }

    public Float getLatitud() {
        return latitud;
    }

    public Float getLongitud() {
        return longitud;
    }
}
