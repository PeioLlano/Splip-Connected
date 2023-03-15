package com.example.primerproyecto.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoGasto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_gasto);

        //Recibimos el objeto Gasto que queremos mostrar
        Gasto gasto = (Gasto) getIntent().getSerializableExtra("gasto");

        //Inicializamos el valor de los campos
        EditText eNombre = findViewById(R.id.eNombre);
        eNombre.setText(gasto.getTitulo());
        TextView tCantidad = findViewById(R.id.tCantidad);
        tCantidad.setText(String.valueOf(gasto.getCantidad()));
        TextView tAutor = findViewById(R.id.tAutor);
        tAutor.setText(gasto.getAutor().getNombre());
        TextView tFecha = findViewById(R.id.tFecha);
        tFecha.setText(String.valueOf(gasto.getFecha()));
    }
}
