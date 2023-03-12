package com.example.primerproyecto.ListAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.R;

import java.util.ArrayList;

public class GastoAdapter extends BaseAdapter {

    private ArrayList<Gasto> gastos;
    private String divisa;
    private LayoutInflater inflater;

    public GastoAdapter(Context context, ArrayList<Gasto> pGastos, String pDivisa) {
        gastos = pGastos;
        divisa = pDivisa;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return gastos.size();
    }

    @Override
    public Object getItem(int i) {
        return gastos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position,@NonNull View view,@NonNull ViewGroup viewGroup){

        view = inflater.inflate(R.layout.gasto_item, null);

        TextView tTituloGasto = view.findViewById(R.id.tTituloPago);
        TextView tAutorGasto = view.findViewById(R.id.tAutorPago);
        TextView tCantidadGasto = view.findViewById(R.id.tCantidadPago);
        ImageView iGrupo =(ImageView) view.findViewById(R.id.iGrupo);

        Gasto gasto = gastos.get(position);

        System.out.println(gasto.getTitulo() + "(" + gasto.getCantidad() + " " + divisa + ")---> De " + gasto.getAutor().getNombre());

        tTituloGasto.setText(gasto.getTitulo());
        tAutorGasto.setText(gasto.getAutor().getNombre());
        tCantidadGasto.setText(gasto.getCantidad() + " " + divisa);
        iGrupo.setImageResource(R.drawable.carro_naranja);

        return view;
    }
}
