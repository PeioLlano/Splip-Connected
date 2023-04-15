package com.example.primerproyecto.Clases;

import android.util.Log;

import java.io.Console;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Grupo implements Serializable {

    private String titulo;
    private String divisa;
    private ArrayList<Persona> personas;
    private ArrayList<Gasto> gastos;
    private ArrayList<Pago> pagos;

    public Grupo(String titulo, String divisa, ArrayList<Persona> personas, ArrayList<Gasto> gastos, ArrayList<Pago> pagos) {
        this.titulo = titulo;
        this.divisa = divisa;
        this.personas = personas;
        this.gastos = gastos;
        this.pagos = pagos;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDivisa() {
        return divisa;
    }

    public ArrayList<Gasto> getGastos() {
        return gastos;
    }

    public ArrayList<Pago> getPagos() {
        return pagos;
    }

    public void addGasto(Gasto gasto, Persona p) {
        gastos.add(gasto);
        p.incGasto();
        actualizarBalances();
    }

    public void addPago(Pago pago, Persona p) {
        pagos.add(pago);
        p.incPago();
        actualizarBalances();
    }

    public void addPersona(Persona p) {
        personas.add(p);
    }

    public ArrayList<Persona> getPersonas() {
        return personas;
    }

    public void actualizarBalances() {
        HashMap<Persona, Float> gastosXPersona = new HashMap();
        Float GastoAcumulado = 0.0f;
        Float GastoMedio = 0.0f;

        for (Persona p:personas) gastosXPersona.put(p, 0f);
        for (Gasto g: gastos) {
            gastosXPersona.put(g.getAutor(), gastosXPersona.get(g.getAutor()) - g.getCantidad());
            GastoAcumulado += g.getCantidad();
        }
        for (Pago p: pagos) {
            gastosXPersona.put(p.getAutor(), gastosXPersona.get(p.getAutor()) - p.getCantidad());
            gastosXPersona.put(p.getDestinatario(), gastosXPersona.get(p.getDestinatario()) + p.getCantidad());
        }

        GastoMedio = GastoAcumulado/personas.size();

        for (Persona p:personas) {
            gastosXPersona.put(p, gastosXPersona.get(p) + GastoMedio);
        }

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        for (Persona p:personas) p.setBalance(Float.valueOf(df.format(gastosXPersona.get(p)).replace(',', '.')));
    }

    public ArrayList<Pago> ajustarCuentas(){

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        if ((gastos.size()==0) && (pagos.size()==0)) return new ArrayList<>();
        ArrayList<Pago> ajuste = new ArrayList<>();
        ArrayList<Persona> personasAjustadas = new ArrayList<>();
        boolean ajustado = true;

        for (Persona p: personas) {
            personasAjustadas.add(new Persona(p.getNombre(),p.getBalance(),0,0, null));
        }

        for (Persona p: personasAjustadas) {
            if ((-0.05f < p.getBalance() ) && (p.getBalance() > 0.05f)) {
                ajustado = false;
                break;
            }
        }

        while (!ajustado){
            Persona pMaxDeudor = maxDeudor(personasAjustadas);
            Persona pMaxAcreedor = maxAcreedor(personasAjustadas);
            Float cantidadPago = pMaxDeudor.getBalance();

            if (cantidadPago > -pMaxAcreedor.getBalance()) {
                cantidadPago = -pMaxAcreedor.getBalance();
            }

            pMaxDeudor.setBalance(pMaxDeudor.getBalance() - cantidadPago);
            pMaxAcreedor.setBalance(pMaxAcreedor.getBalance() + cantidadPago);

            ajuste.add(new Pago(-1, Float.valueOf(df.format(cantidadPago).replace(',', '.')) , pMaxDeudor,pMaxAcreedor, null));

            ajustado = true;
            for (Persona p: personasAjustadas) {
                if ((-0.05f < p.getBalance() ) && (p.getBalance() > 0.05f)) {
                    ajustado = false;
                    break;
                }
            }
        }

        return ajuste;
    }

    private Persona maxDeudor(ArrayList<Persona> personasAjustadas){
        Persona pResultado = null;
        Float maxDeudorF = 0.5f;
        for (Persona p: personasAjustadas) {
            if (p.getBalance() >= maxDeudorF){
                maxDeudorF = p.getBalance();
                pResultado = p;
            }
        }
        return pResultado;
    }

    private Persona maxAcreedor(ArrayList<Persona> personasAjustadas){
        Persona pResultado = null;
        Float maxAcreedorF = -0.5f;
        for (Persona p: personasAjustadas) {
            if (p.getBalance() <= maxAcreedorF){
                maxAcreedorF = p.getBalance();
                pResultado = p;
            }
        }
        return pResultado;
    }

    public Persona getPersonaByName(String name){
        Persona p = new Persona("",0f,0,0, null);

        for (Persona pl: personas) {
            if (pl.getNombre().equals(name)) {
                p = pl;
                break;
            }
        }

        return p;
    }

    public String getIntegrantes() {
        String result = "";
        for (Persona p: personas) result += p.getNombre() + ", ";
        if (result.length() > 0) return result.substring(0, result.length()-2);
        else return "NoTengo";
    }

    public boolean tienePersona(String username) {
        Boolean result = false;
        for (Persona p:personas) {
            if (username == p.getNombre()){
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean gastoMetido(Integer codigo) {
        Boolean result = false;
        for (Gasto g: gastos) {
            if (g.getCodigo() == codigo){
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean pagoMetido(Integer codigo) {
        Boolean result = false;
        for (Pago p: pagos) {
            if (p.getCodigo() == codigo){
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean tieneNombre(String nombre){
        Boolean result = false;
        for (Persona p: personas) {
            if (p.getNombre().equals(nombre)){
                result = true;
                break;
            }
        }
        return result;
    }

    public void retirarGastosyPagos(Persona persona) {
        for (Gasto g: gastos) if (g.getAutor().equals(persona)) gastos.remove(g);
        for (Pago p: pagos) if (p.getAutor().equals(persona) || p.getDestinatario().equals(persona)) pagos.remove(p);
    }

    public void actualizarGastosyPagos(Persona persona) {
        for (Gasto g: gastos) if (g.getAutor().equals(persona)) gastos.remove(g);
        for (Pago p: pagos) if (p.getAutor().equals(persona) || p.getDestinatario().equals(persona)) pagos.remove(p);
    }
}
