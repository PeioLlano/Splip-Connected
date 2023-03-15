package com.example.primerproyecto.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.ListAdapters.GastoAdapter;
import com.example.primerproyecto.R;

import java.util.ArrayList;

public class ListGastosF extends Fragment {

    ArrayList<Gasto> gastos = new ArrayList<>();
    private GastoAdapter pAdapter;
    private ListenerDelFragment elListener;
    ListView lGastos;
    LinearLayout lVacia;

    public interface ListenerDelFragment {
        ArrayList<Gasto> cargarElementos();
        String cargarDivisa();
        void seleccionarElemento(int pos);

        void borrarGasto(Gasto gasto);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            elListener = (ListenerDelFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("La clase " + context.toString()
                    + "debe implementar ListenerDelFragment");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.lista_gastos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String divisa = elListener.cargarDivisa();

        gastos = elListener.cargarElementos();

        lGastos = (ListView) view.findViewById(R.id.lGastos);
        lVacia = view.findViewById(R.id.lVacia);
        pAdapter = new GastoAdapter(getContext(), gastos, divisa);
        lGastos.setAdapter(pAdapter);
        lGastos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                elListener.seleccionarElemento(i);
            }
        });

        final Integer[] posAborrar = {-1};

        //Generar el dialogo que se deberia de ver si pulsamos por un tiempo prolongado un a persona
        AlertDialog.Builder builderG = new AlertDialog.Builder(getContext());
        builderG.setCancelable(true);
        builderG.setTitle(getString(R.string.delete_expense));
        builderG.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Borramos la persona de todos los lados y se lo notificamos al adaptador
                        elListener.borrarGasto(gastos.get(posAborrar[0]));
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

        //Listener para borrar un gasto del grupo.
        lGastos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                posAborrar[0] = pos;
                dialogBorrar.show();
                return true;
            }
        });

        if (gastos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lGastos.setVisibility(View.GONE);
        }
    }

    public void actualizarListaVacia() {
        if (gastos.size() == 0){
            lVacia.setVisibility(View.VISIBLE);
            lGastos.setVisibility(View.GONE);
        }
    }
}
