package com.example.primerproyecto.Clases;

import android.database.sqlite.SQLiteDatabase;

import com.example.primerproyecto.BBDD.BBDD;

import java.io.Serializable;

public class Persona implements Serializable {

    private String nombre;
    private Float balance;
    private Integer gastos;
    private Integer pagos;

    public Persona(String nombre, Float balance, Integer gastos, Integer pagos) {
        this.nombre = nombre;
        this.balance = balance;
        this.gastos = gastos;
        this.pagos = pagos;
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
