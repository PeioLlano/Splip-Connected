package com.example.primerproyecto.Clases;

import java.io.Serializable;

public class Persona implements Serializable {

    private String nombre;
    private Float balance;
    private Integer gastos;
    private Integer pagos;
    private String foto;

    public Persona(String nombre, Float balance, Integer gastos, Integer pagos, String foto) {
        this.nombre = nombre;
        this.balance = balance;
        this.gastos = gastos;
        this.pagos = pagos;
        this.foto = foto;
    }

    public String getNombre() {
        return this.nombre;
    }

    public Float getBalance() {
        return balance;
    }

    public Integer getGastos() {
        return gastos;
    }

    public Integer getPagos() {
        return pagos;
    }

    public String getFoto() {
        return foto;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }

    public void incGasto(){
        gastos++;
    }

    public void incPago(){
        pagos++;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
