package com.example.primerproyecto.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.BBDD.BBDD;
import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Fragments.InfoGastoF;
import com.example.primerproyecto.Fragments.ListGastosF;
import com.example.primerproyecto.R;

import java.util.ArrayList;
import java.util.Date;

public class ListGastosCF extends AppCompatActivity implements ListGastosF.ListenerDelFragment {

    private Grupo grupo;
    ArrayList<Integer> borrados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_gastos_cf);

        //Obtenemos el grupo necesario para generar la lista
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) getIntent().getSerializableExtra("grupo");

        borrados = new ArrayList<>();
    }

    @Override
    public ArrayList<Gasto> cargarElementos() {
        return grupo.getGastos();
    }

    @Override
    public String cargarDivisa() {
        return grupo.getDivisa();
    }

    @Override
    public void seleccionarElemento(int pos) {
        String nombre = grupo.getGastos().get(pos).getTitulo();
        String autor = grupo.getGastos().get(pos).getAutor().getNombre();
        Float cantidad = grupo.getGastos().get(pos).getCantidad();
        Date fecha = grupo.getGastos().get(pos).getFecha();

        //Si la orientacion es normal crearemos un intent a la actiidad de InfoGasto,
        //si es tumbado actualizaremos los datos del fragment de gasto con los del elemento seleccionado.
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            InfoGastoF infoGastoF = (InfoGastoF) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView2);
            infoGastoF.actualizarDatos(nombre, autor, cantidad, fecha);
        } else {
            Intent i = new Intent(this, InfoGasto.class);
            i.putExtra("gasto", grupo.getGastos().get(pos));
            startActivity(i);
        }
    }

    @Override
    public void borrarGasto(Gasto gasto) {
        grupo.getGastos().remove(gasto);
        grupo.actualizarBalances();

        borrados.add(gasto.getCodigo());

        ListGastosF listGastosF = (ListGastosF) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView1);
        listGastosF.actualizarListaVacia();
    }

    //Al ir atras llevar los borrados a main
    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        intent.putIntegerArrayListExtra("gastosBorrados", borrados);
        setResult(RESULT_OK, intent);
        finish();
    }

}
