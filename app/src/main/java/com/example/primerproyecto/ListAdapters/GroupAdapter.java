package com.example.primerproyecto.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.primerproyecto.R;
import com.example.primerproyecto.Clases.Grupo;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {

    private ArrayList<Grupo> grupos;
    private LayoutInflater inflater;

    public GroupAdapter(Context context, ArrayList<Grupo> pGrupo) {
        grupos = pGrupo;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return grupos.size();
    }

    @Override
    public Object getItem(int i) {
        return grupos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position,@NonNull View view,@NonNull ViewGroup viewGroup){

        view = inflater.inflate(R.layout.grupo_item, null);

        TextView tTitulo = view.findViewById(R.id.tTituloPago);
        TextView tParticipantes = view.findViewById(R.id.tParticipantes);
        ImageView iGrupo =(ImageView) view.findViewById(R.id.iGrupo);

        Grupo grupo = grupos.get(position);

        tTitulo.setText(getTitulo(grupo));
        String participantes = getIntegrantes(grupo);
        if (participantes == "NoTengo") {
            participantes = view.getResources().getString(R.string.no_integrantes);
        }
        tParticipantes.setText(participantes);
        iGrupo.setImageResource(R.drawable.grupo);

        return view;
    }

    public String getTitulo(Grupo grupo){
        return grupo.getTitulo();
    }

    public String getIntegrantes(Grupo grupo){
        return grupo.getIntegrantes();
    }
}
