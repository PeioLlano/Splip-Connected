package com.example.primerproyecto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.primerproyecto.BBDD.BBDD;
import com.example.primerproyecto.Dialogs.AddPersonDialog;
import com.example.primerproyecto.ListAdapters.PersonaAdapter;
import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Clases.Persona;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainGrupo extends AppCompatActivity implements AddPersonDialog.AddPersonDialogListener {

    private PersonaAdapter pAdapter;
    Grupo grupo;
    ArrayList<String> posiblesPersonasNombre;
    SQLiteDatabase bbdd;
    LinearLayout lVacia;
    ListView lPersonas;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_grupo);
        
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        if (extras != null) {
            //grupo = (Grupo) getIntent().getSerializableExtra("grupo");
            username = getIntent().getStringExtra("username");
        }

        grupo.actualizarBalances();

        TextView tGrupo = (TextView) findViewById(R.id.tGrupo);
        tGrupo.setText(grupo.getTitulo());

        BBDD gestorBBDD = new BBDD(this, "SpliP", null, 1);
        bbdd = gestorBBDD.getWritableDatabase();
        Cursor c = bbdd.rawQuery("SELECT * FROM Gastos WHERE Grupo = ? AND Usuario = ?", new String[]{grupo.getTitulo(), username});

        if (c.moveToFirst()){
            do{
                Integer Codigo = c.getInt(0);
                String Persona = c.getString(3);
                String Titulo = c.getString(4);
                Float Cantidad = c.getFloat(5);

                Date Fecha;
                try {
                    Fecha = new SimpleDateFormat("YYYY-MM-DD").parse(c.getString(5));
                } catch (ParseException e) {
                    Fecha = null;
                }

                if(!grupo.gastoMetido(Codigo)){
                    grupo.addGasto(new Gasto(Codigo, Titulo, Cantidad, getPersonaByName(Persona), Fecha), getPersonaByName(Persona));
                }
            }while(c.moveToNext());
        }

        c = bbdd.rawQuery("SELECT * FROM Pagos WHERE Grupo = ? AND Usuario = ?", new String[]{grupo.getTitulo(), username});

        if (c.moveToFirst()){
            do{
                Integer Codigo = c.getInt(0);
                String PersonaA = c.getString(3);
                String PersonaD = c.getString(4);
                Float Cantidad = c.getFloat(5);

                Date Fecha;
                try {
                    Fecha = new SimpleDateFormat("YYYY-MM-DD").parse(c.getString(5));
                } catch (ParseException e) {
                    Fecha = null;
                }

                if(!grupo.pagoMetido(Codigo)) {
                    grupo.addPago(new Pago(Codigo, Cantidad, getPersonaByName(PersonaA), getPersonaByName(PersonaD), Fecha), getPersonaByName(PersonaA));
                }

            }while(c.moveToNext());
        }

        /*grupo.addPersona(new Persona("Persona1", (float) 0, 0, 0));
        grupo.addPersona(new Persona("Persona2", (float) 0, 0, 0));
        grupo.addPersona(new Persona("Persona3", (float) 0, 0, 0));
        grupo.addPersona(new Persona("Persona4", (float) 0, 0, 0));
        grupo.addPersona(new Persona("Persona5", (float) 0, 0, 0));

        grupo.addGasto(new Gasto("Gasto1", 123.2f, getPersonaByName("Persona2"), null), getPersonaByName("Persona2"));
        grupo.addGasto(new Gasto("Gasto1", 3.5f, getPersonaByName("Persona3"), null), getPersonaByName("Persona3"));

        grupo.addPago(new Pago(10.0f, getPersonaByName("Persona5"),getPersonaByName("Persona3"), null),getPersonaByName("Persona5"));
        grupo.addPago(new Pago(10.0f, getPersonaByName("Persona1"),getPersonaByName("Persona2"), null),getPersonaByName("Persona1"));
*/
        //--------------------------------------------------------------------------------------------------------------

        //                                           LISTA

        //--------------------------------------------------------------------------------------------------------------

        lPersonas = (ListView) findViewById(R.id.lPersonas);
        pAdapter = new PersonaAdapter(getApplicationContext(), grupo.getPersonas(), grupo.getDivisa());
        lPersonas.setAdapter(pAdapter);
        /*if (pAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < pAdapter.getCount(); i++) {
                View listItem = pAdapter.getView(i, null, lPersonas);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = lPersonas.getLayoutParams();
            params.height = totalHeight + (lPersonas.getDividerHeight() * (lPersonas.getCount() - 1));
            lPersonas.setLayoutParams(params);
            lPersonas.requestLayout();
        }*/

        ActivityResultLauncher<Intent> startActivityIntent_InfoPersona = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            String nuevoNombre = result.getData().getStringExtra("nuevoNombre");
                            if (!nuevoNombre.equals("no cambio")) {
                                Persona personaCambio = (Persona) result.getData().getSerializableExtra("persona");

                                ContentValues contentValues = new ContentValues();
                                contentValues.put("Nombre", nuevoNombre);

                                bbdd.update("Personas", contentValues, "Grupo = ? AND Nombre = ? AND Usuario = ?", new String[]{grupo.getTitulo(),personaCambio.getNombre(), username});

                                grupo.getPersonaByName(personaCambio.getNombre()).setNombre(nuevoNombre);
                                lPersonas.setAdapter(pAdapter);
                            }
                        }
                    }
                }
        );

        lPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intentInfo = new Intent(MainGrupo.this, InfoPersona.class);
                intentInfo.putExtra("grupo", grupo);
                intentInfo.putExtra("persona", grupo.getPersonas().get(position));
                startActivityIntent_InfoPersona.launch(intentInfo);
            }
        });

        lVacia = findViewById(R.id.lVacia);
        actualizarVacioLleno(grupo.getPersonas());

        final Integer[] posAborrar = {-1};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.delete_person));
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bbdd.delete("Personas", "Grupo = ? AND Nombre = ? AND Usuario = ?", new String[]{grupo.getTitulo(), grupo.getPersonas().get(posAborrar[0]).getNombre(), username});
                        grupo.retirarGastosyPagos(grupo.getPersonas().get((int)posAborrar[0]));
                        grupo.getPersonas().remove(((int)posAborrar[0]));
                        posiblesPersonasNombre.remove(((int)posAborrar[0]));
                        grupo.actualizarBalances();
                        pAdapter.notifyDataSetChanged();
                        actualizarVacioLleno(grupo.getPersonas());
                        posAborrar[0] = -1;
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialogBorrar = builder.create();

        lPersonas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                posAborrar[0] = pos;
                dialogBorrar.show();
                return true;
            }
        });

        posiblesPersonasNombre = new ArrayList<>();
        for (Persona p: grupo.getPersonas()) posiblesPersonasNombre.add(p.getNombre());

        //--------------------------------------------------------------------------------------------------------------

        //                                           BOTONES

        //--------------------------------------------------------------------------------------------------------------

        TextView tPlusPersona = (TextView) findViewById(R.id.tPlusPersona);

        FloatingActionButton bPlusPerson = (FloatingActionButton) findViewById(R.id.bPlusPerson);
        bPlusPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPersonDialog addPersonDialog = new AddPersonDialog();
                addPersonDialog.show(getSupportFragmentManager(), "add person dialog");
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //                                           GASTO

        //--------------------------------------------------------------------------------------------------------------

        TextView tAddGasto = (TextView) findViewById(R.id.tAddGasto);

        FloatingActionButton bAddGasto = (FloatingActionButton) findViewById(R.id.bAddGasto);

        int tiempoToast= Toast.LENGTH_SHORT;
        Toast avisoGasto = Toast.makeText(this, "Gasto añadido", tiempoToast);

        ActivityResultLauncher<Intent> startActivityIntent_AddGasto = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            String nombre = result.getData().getStringExtra("nombre");
                            String cantidad = result.getData().getStringExtra("cantidad");
                            String autor = result.getData().getStringExtra("autor");

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("Grupo", grupo.getTitulo());
                            contentValues.put("Persona", autor);
                            contentValues.put("Titulo", nombre);
                            contentValues.put("Cantidad", cantidad);
                            contentValues.put("Usuario", username);

                            Date date = new Date();

                            contentValues.put("Fecha", date.toString());

                            long row = bbdd.insert("Gastos", null, contentValues);

                            Cursor c = bbdd.rawQuery("SELECT rowid, codigo FROM Gastos WHERE Grupo = ? AND Persona = ? AND Titulo = ? AND Usuario = ?", new String[]{grupo.getTitulo(), grupo.getTitulo(), autor, username});
                            Integer codigo = null;

                            if (c.moveToFirst()){
                                do{
                                    if (c.getInt(0) == row){
                                        codigo = c.getInt(1);
                                    }
                                }while(c.moveToNext());
                            }

                            addGasto(new Gasto(codigo, nombre, Float.parseFloat(cantidad), getPersonaByName(autor), date), getPersonaByName(autor));

                            lPersonas.setAdapter(pAdapter);

                            avisoGasto.show();
                        }
                    }
                }
        );

        bAddGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGrupo.this, AddGasto.class);
                intent.putExtra("posiblesAutores", posiblesPersonasNombre);
                startActivityIntent_AddGasto.launch(intent);
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //                                           PAGO

        //--------------------------------------------------------------------------------------------------------------

        TextView tAddPago = (TextView) findViewById(R.id.tAddPago);

        FloatingActionButton bAddPago = (FloatingActionButton) findViewById(R.id.bAddPago);

        Toast avisoPago = Toast.makeText(this, "Pago añadido", tiempoToast);

        ActivityResultLauncher<Intent> startActivityIntent_AddPago = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            String cantidad = result.getData().getStringExtra("cantidad");
                            String autor = result.getData().getStringExtra("autor");
                            String destinatario = result.getData().getStringExtra("destinatario");

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("Grupo", grupo.getTitulo());
                            contentValues.put("PersonaAutora", autor);
                            contentValues.put("PersonaDestinataria", destinatario);
                            contentValues.put("Cantidad", cantidad);
                            contentValues.put("Usuario", username);

                            DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                            Date date = new Date();

                            contentValues.put("Fecha", dateFormat.format(date));

                            long row = bbdd.insert("Pagos", null, contentValues);

                            Cursor c = bbdd.rawQuery("SELECT rowid, codigo FROM Pagos WHERE Grupo = ? AND PersonaAutora = ? AND PersonaDestinataria = ? AND Usuario = ?", new String[]{grupo.getTitulo(), autor, destinatario, username});
                            Integer codigo = null;

                            if (c.moveToFirst()){
                                do{
                                    if (c.getInt(0) == row){
                                        codigo = c.getInt(1);
                                    }
                                }while(c.moveToNext());
                            }

                            addPago(new Pago(codigo, Float.parseFloat(cantidad), getPersonaByName(autor), getPersonaByName(destinatario), date), getPersonaByName(autor));

                            lPersonas.setAdapter(pAdapter);

                            avisoPago.show();
                        }
                    }
                }
        );

        bAddPago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainGrupo.this, AddPago.class);
                intent.putExtra("posiblesPersonas", posiblesPersonasNombre);
                startActivityIntent_AddPago.launch(intent);
            }
        });

        TextView tExpAddPago = (TextView) findViewById(R.id.tExpAddPago);
        TextView tExpAddPersona = (TextView) findViewById(R.id.tExpAddPersona);
        TextView tExpAddGasto = (TextView) findViewById(R.id.tExpAddGasto);


        //--------------------------------------------------------------------------------------------------------------

        //                                           VER PAGOS

        //--------------------------------------------------------------------------------------------------------------


        FloatingActionButton bVerPagos = (FloatingActionButton) findViewById(R.id.bVerPagos);

        bVerPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPagos = new Intent(MainGrupo.this, ListPagos.class);
                intentPagos.putExtra("grupo", grupo);
                startActivity(intentPagos);
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //                                          VER GASTOS

        //--------------------------------------------------------------------------------------------------------------


        FloatingActionButton bVerGastos = (FloatingActionButton) findViewById(R.id.bVerGastos);

        bVerGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGastos = new Intent(MainGrupo.this, ListGastos.class);
                intentGastos.putExtra("grupo", grupo);
                startActivity(intentGastos);
            }
        });

        TextView tListPagos = (TextView) findViewById(R.id.tListPagos);
        TextView tExpListPagos = (TextView) findViewById(R.id.tExpListPagos);

        TextView tListGastos = (TextView) findViewById(R.id.tListGastos);
        TextView tExpListGastos = (TextView) findViewById(R.id.tExpListGastos);

        //--------------------------------------------------------------------------------------------------------------

        //                                           OPCIONES

        //--------------------------------------------------------------------------------------------------------------

        final Boolean[] clicadoOpciones = {false};
        final Boolean[] clicadoVer = {false};

        FloatingActionButton bOtions = (FloatingActionButton) findViewById(R.id.bOtions);
        bOtions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicadoOpciones[0]) {
                    //Poner los de la derecha

                    bOtions.setImageResource(R.drawable.top);

                    bPlusPerson.setVisibility(View.VISIBLE);
                    bPlusPerson.setClickable(true);

                    tPlusPersona.setVisibility(View.VISIBLE);

                    bAddGasto.setVisibility(View.VISIBLE);
                    bAddGasto.setClickable(true);

                    tAddGasto.setVisibility(View.VISIBLE);

                    tAddPago.setVisibility(View.VISIBLE);

                    bAddPago.setVisibility(View.VISIBLE);
                    bAddPago.setClickable(true);

                    tExpAddPago.setVisibility(View.VISIBLE);
                    tExpAddPersona.setVisibility(View.VISIBLE);
                    tExpAddGasto.setVisibility(View.VISIBLE);

                    //Quitar los de la izquierda

                    bVerGastos.setVisibility(View.INVISIBLE);
                    bVerGastos.setClickable(false);

                    bVerPagos.setVisibility(View.INVISIBLE);
                    bVerPagos.setClickable(false);

                    tListPagos.setVisibility(View.INVISIBLE);
                    tExpListPagos.setVisibility(View.INVISIBLE);

                    tListGastos.setVisibility(View.INVISIBLE);
                    tExpListGastos.setVisibility(View.INVISIBLE);

                    clicadoVer[0] = false;
                }
                else {
                    bOtions.setImageResource(R.drawable.left);

                    bPlusPerson.setVisibility(View.INVISIBLE);
                    bPlusPerson.setClickable(false);

                    tPlusPersona.setVisibility(View.INVISIBLE);

                    bAddGasto.setVisibility(View.INVISIBLE);
                    bAddGasto.setClickable(false);

                    tAddPago.setVisibility(View.INVISIBLE);

                    bAddPago.setVisibility(View.INVISIBLE);
                    bAddPago.setClickable(false);

                    tAddGasto.setVisibility(View.INVISIBLE);

                    tExpAddPago.setVisibility(View.INVISIBLE);
                    tExpAddPersona.setVisibility(View.INVISIBLE);
                    tExpAddGasto.setVisibility(View.INVISIBLE);
                }
                clicadoOpciones[0] = !clicadoOpciones[0];
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //                                           VER

        //--------------------------------------------------------------------------------------------------------------

        FloatingActionButton bVer = (FloatingActionButton) findViewById(R.id.bVer);
        bVer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicadoVer[0]) {
                    //Poner los de la izquierda

                    bVerGastos.setVisibility(View.VISIBLE);
                    bVerGastos.setClickable(true);

                    bVerPagos.setVisibility(View.VISIBLE);
                    bVerPagos.setClickable(true);

                    tListPagos.setVisibility(View.VISIBLE);
                    tExpListPagos.setVisibility(View.VISIBLE);

                    tListGastos.setVisibility(View.VISIBLE);
                    tExpListGastos.setVisibility(View.VISIBLE);

                    //Quitar los de la derecha

                    bOtions.setImageResource(R.drawable.left);

                    bPlusPerson.setVisibility(View.INVISIBLE);
                    bPlusPerson.setClickable(false);

                    tPlusPersona.setVisibility(View.INVISIBLE);

                    bAddGasto.setVisibility(View.INVISIBLE);
                    bAddGasto.setClickable(false);

                    tAddPago.setVisibility(View.INVISIBLE);

                    bAddPago.setVisibility(View.INVISIBLE);
                    bAddPago.setClickable(false);

                    tAddGasto.setVisibility(View.INVISIBLE);

                    tExpAddPago.setVisibility(View.INVISIBLE);
                    tExpAddPersona.setVisibility(View.INVISIBLE);
                    tExpAddGasto.setVisibility(View.INVISIBLE);

                    clicadoOpciones[0] = false;
                }
                else {
                    bVerGastos.setVisibility(View.INVISIBLE);
                    bVerGastos.setClickable(false);

                    bVerPagos.setVisibility(View.INVISIBLE);
                    bVerPagos.setClickable(false);

                    tListPagos.setVisibility(View.INVISIBLE);
                    tExpListPagos.setVisibility(View.INVISIBLE);

                    tListGastos.setVisibility(View.INVISIBLE);
                    tExpListGastos.setVisibility(View.INVISIBLE);
                }
                clicadoVer[0] = !clicadoVer[0];
            }
        });

        //--------------------------------------------------------------------------------------------------------------

        //                                           AJUSTAR CUENTAS

        //--------------------------------------------------------------------------------------------------------------

        ActivityResultLauncher<Intent> startActivityIntent_AjustarCuentas = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            ArrayList<Pago> pagosSeleccionados = (ArrayList<Pago>) result.getData().getSerializableExtra("pagosSeleccionados");

                            for (Pago pago: pagosSeleccionados) {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("Grupo", grupo.getTitulo());
                                String autor = pago.getAutor().getNombre();
                                contentValues.put("PersonaAutora", autor);
                                String destinatario = pago.getDestinatario().getNombre();
                                contentValues.put("PersonaDestinataria", destinatario);
                                String cantidad = pago.getCantidad().toString();
                                contentValues.put("Cantidad", cantidad);
                                contentValues.put("Usuario", username);


                                DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD");
                                Date date = new Date();

                                contentValues.put("Fecha", dateFormat.format(date));

                                long row = bbdd.insert("Pagos", null, contentValues);

                                Cursor c = bbdd.rawQuery("SELECT rowid, codigo FROM Pagos WHERE Grupo = ? AND PersonaAutora = ? AND PersonaDestinataria = ? AND Usuario = ?", new String[]{grupo.getTitulo(), autor, destinatario, username});
                                Integer codigo = null;

                                if (c.moveToFirst()){
                                    do{
                                        if (c.getInt(0) == row){
                                            codigo = c.getInt(1);
                                        }
                                    }while(c.moveToNext());
                                }

                                addPago(new Pago(codigo, Float.parseFloat(cantidad), getPersonaByName(autor), getPersonaByName(destinatario), date), getPersonaByName(autor));
                            }
                            lPersonas.setAdapter(pAdapter);
                        }
                    }
                }
        );

        Button bAjustarCuentas = (Button) findViewById(R.id.bAjustarCuentas);
        bAjustarCuentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PagosAjuste.class);
                i.putExtra("grupo", grupo);
                ArrayList<Pago> pagosAjuste = grupo.ajustarCuentas();
                //i.putExtra("pagosAjuste", pagosAjuste);
                startActivityIntent_AjustarCuentas.launch(i);

                /*PendingIntent intentEnNot = PendingIntent.getActivity(getApplicationContext(), PendingIntent.FLAG_UPDATE_CURRENT, i, PendingIntent.FLAG_IMMUTABLE);

                NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getApplicationContext(), "IdCanal");
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(getString(R.string.titulo_noti_ajuste))
                        .setContentText(getString(R.string.cuerpo_noti_ajuste, pagosAjuste.size()))
                        .addAction(0, getString(R.string.desplegar_ajustes), intentEnNot)
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    elManager.createNotificationChannel(elCanal);
                }

                elManager.notify(1, elBuilder.build());*/
            }
        });
    }


    @Override
    public void añadirPersona(String Username) {

        if (!grupo.tienePersona(Username)) {
            grupo.getPersonas().add(new Persona(Username, (float) 0, 0, 0));
            posiblesPersonasNombre.add(Username);
            grupo.actualizarBalances();
            pAdapter.notifyDataSetChanged();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Grupo", grupo.getTitulo());
            contentValues.put("Nombre", Username);
            contentValues.put("Usuario", username);

            bbdd.insertOrThrow("Personas", null, contentValues);

            actualizarVacioLleno(grupo.getPersonas());
        }
        else {
            Toast aviso = Toast.makeText(getApplicationContext(), getResources().getString(R.string.persona_existe), Toast.LENGTH_SHORT);
            aviso.setGravity(Gravity.CENTER, 0, 0);
            aviso.show();
        }
    }

    public void addGasto(Gasto g,Persona p){
        grupo.addGasto(g, p);
    }

    public void addPago(Pago p,Persona pe){
        grupo.addPago(p, pe);
    }

    public Persona getPersonaByName(String name){
        return grupo.getPersonaByName(name);
    }

    private void actualizarVacioLleno(ArrayList<Persona> personas) {

        if(personas.size() > 0) {
            lVacia.setVisibility(View.GONE);
            lPersonas.setVisibility(View.VISIBLE);
        } else {
            lVacia.setVisibility(View.VISIBLE);
            lPersonas.setVisibility(View.GONE);
        }
    }
}