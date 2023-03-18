package com.example.primerproyecto.Fragments;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.primerproyecto.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InfoGastoF extends Fragment {

    private TextView tFecha, tCantidad, tAutor;
    private EditText eNombre;
    private String nombre, autor;
    private Float cantidad;
    private Date fecha;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.info_gasto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("AlumnInfoFragment", "onViewCreated");
        eNombre = (EditText) view.findViewById(R.id.eNombre);
        eNombre.setText(nombre);
        tCantidad = (TextView) view.findViewById(R.id.tCantidad);
        if (cantidad != null) {
            tCantidad.setText(String.valueOf(cantidad));
        }
        else{
            tCantidad.setText("");
        }
        tAutor = (TextView) view.findViewById(R.id.tAutor);
        tAutor.setText(autor);
        tFecha = (TextView) view.findViewById(R.id.tFecha);
        if (fecha != null) {
            tFecha.setText(fecha.toString());
        }
        else{
            tFecha.setText("");
        }
    }

    public void actualizarDatos(String nombre, String autor, Float cantidad, Date fecha) {
        eNombre.setText(nombre);
        tAutor.setText(autor);

        tFecha.setText(fecha.toString());

        tCantidad.setText(String.valueOf(cantidad));
    }
}