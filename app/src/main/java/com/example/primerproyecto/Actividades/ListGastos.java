package com.example.primerproyecto.Actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.ListAdapters.GastoAdapter;
import com.example.primerproyecto.R;

import java.util.ArrayList;

public class ListGastos extends AppCompatActivity {

    ArrayList<Gasto> gastos = new ArrayList<>();
    private GastoAdapter pAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gastos);

        String divisa = "";

        //Obtenemos el grupo necesario para generar la lista
        Bundle extras = getIntent().getExtras();
        Grupo grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        divisa = grupo.getDivisa();
        gastos = grupo.getGastos();

        //Inicializamos la lista con la lista de gastos
        ListView lGastos = (ListView) findViewById(R.id.lGastos);
        LinearLayout lVacia = findViewById(R.id.lVacia);
        pAdapter = new GastoAdapter(getApplicationContext(), gastos, divisa);
        lGastos.setAdapter(pAdapter);

        //Si la lista es vacia mostraremos el layout de lista vacia.
        if (gastos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lGastos.setVisibility(View.GONE);
        }
    }
}
