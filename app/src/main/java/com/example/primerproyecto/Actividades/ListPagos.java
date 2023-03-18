package com.example.primerproyecto.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Fragments.ListGastosF;
import com.example.primerproyecto.ListAdapters.PagoAdapter;
import com.example.primerproyecto.R;

import java.util.ArrayList;

public class ListPagos extends AppCompatActivity {

    ArrayList<Pago> pagos = new ArrayList<>();
    private PagoAdapter pAdapter;
    Grupo grupo;
    ListView lPagos;
    LinearLayout lVacia;
    ArrayList<Integer> borrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_pagos);

        borrados = new ArrayList<>();

        String divisa = "";

        //Obtenemos el grupo necesario para generar la lista
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        divisa = grupo.getDivisa();
        pagos = grupo.getPagos();

        //Inicializamos la lista con la lista de pagos
        lPagos = (ListView) findViewById(R.id.lPagos);
        lVacia = findViewById(R.id.lVacia);
        pAdapter = new PagoAdapter(getApplicationContext(), pagos, divisa);
        lPagos.setAdapter(pAdapter);

        //Si la lista es vacia mostraremos el layout de lista vacia.
        if (pagos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lPagos.setVisibility(View.GONE);
        }

        final Integer[] posAborrar = {-1};

        //Generar el dialogo que se deberia de ver si pulsamos por un tiempo prolongado un a persona
        AlertDialog.Builder builderG = new AlertDialog.Builder(this);
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_payment));
        builderG.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Borramos la persona de todos los lados y se lo notificamos al adaptador
                        borrarPago(pagos.get(posAborrar[0]));
                        posAborrar[0] = -1;
                        pAdapter.notifyDataSetChanged();
                    }
                });
        builderG.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialogBorrar = builderG.create();

        //Listener para borrar un pago del grupo.
        lPagos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                posAborrar[0] = pos;
                dialogBorrar.show();
                return true;
            }
        });
    }

    public void borrarPago(Pago pago) {
        pagos.remove(pago);
        grupo.actualizarBalances();

        borrados.add(pago.getCodigo());

        if (pagos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lPagos.setVisibility(View.GONE);
        }
    }

    //Al ir atras llevar los borrados a main
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putIntegerArrayListExtra("pagosBorrados", borrados);
        setResult(RESULT_OK, intent);
        finish();
    }

}
