package com.example.primerproyecto;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.ListAdapters.PagoAdapter;

import java.util.ArrayList;

public class ListPagos extends AppCompatActivity {

    ArrayList<Pago> pagos = new ArrayList<>();
    private PagoAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_pagos);

        String divisa = "";

        Bundle extras = getIntent().getExtras();
        Grupo grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        divisa = grupo.getDivisa();
        pagos = grupo.getPagos();

        ListView lPagos = (ListView) findViewById(R.id.lPagos);
        LinearLayout lVacia = findViewById(R.id.lVacia);
        pAdapter = new PagoAdapter(getApplicationContext(), pagos, divisa);
        lPagos.setAdapter(pAdapter);

        if (pagos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lPagos.setVisibility(View.GONE);
        }
    }
}
