package com.example.primerproyecto.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.primerproyecto.R;
import com.example.primerproyecto.Clases.Persona;

import java.util.ArrayList;

public class PersonaAdapter extends BaseAdapter {

    private ArrayList<Persona> personas;
    private String divisa;
    private LayoutInflater inflater;

    public PersonaAdapter(Context context, ArrayList<Persona> pPersonas, String pDivisa) {
        personas = pPersonas;
        divisa = pDivisa;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return personas.size();
    }

    @Override
    public Object getItem(int i) {
        return personas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position,@NonNull View view,@NonNull ViewGroup viewGroup){

        view = inflater.inflate(R.layout.persona_item, null);

        TextView tNombre = view.findViewById(R.id.tNombre);
        TextView tCantidad = view.findViewById(R.id.tCantidad);

        Persona persona = personas.get(position);

        tNombre.setText(getNombre(persona));
        tCantidad.setText(getBalance(persona).toString() + " " + divisa);

        return view;
    }

    public String getNombre(Persona persona){
        return persona.getNombre();
    }

    public Float getBalance(Persona persona){
        return persona.getBalance();
    }
}
