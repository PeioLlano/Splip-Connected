package com.example.primerproyecto.Clases;

import java.io.Serializable;
import java.util.Date;

public class Pago implements Serializable {

    private Integer codigo;
    private Float cantidad;
    private Persona autor;
    private Persona destinatario;
    private Date fecha;

    public Pago(Integer codigo, Float cantidad, Persona autor, Persona destinatario, Date fecha) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.autor = autor;
        this.destinatario = destinatario;
        this.fecha = fecha;
    }

    public Integer getCodigo() {
        return codigo;
    }
    public Float getCantidad() {
        return cantidad;
    }

    public Persona getAutor() {
        return autor;
    }

    public Persona getDestinatario() {
        return destinatario;
    }

    public Date getFecha() {
        return fecha;
    }
}
