package com.example.primerproyecto.Actividades;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.primerproyecto.BBDD.BBDD;
import com.example.primerproyecto.Dialogs.AddPersonDialog;
import com.example.primerproyecto.ListAdapters.PersonaAdapter;
import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        //Obtenemos el usuario y el grupo necesario para obtener los demas datos
        Bundle extras = getIntent().getExtras();
        grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        if (extras != null) {
            //grupo = (Grupo) getIntent().getSerializableExtra("grupo");
            username = getIntent().getStringExtra("username");
        }

        grupo.actualizarBalances();

        TextView tGrupo = (TextView) findViewById(R.id.tGrupo);
        tGrupo.setText(grupo.getTitulo());

        //Abrimos la conexion con la base de datos
        BBDD gestorBBDD = new BBDD(this, "SpliP", null, 1);
        bbdd = gestorBBDD.getWritableDatabase();

        //Pedimos todos los GASTOS que tenga el usuario y grupo que hemos recibido
        Cursor c = bbdd.rawQuery("SELECT * FROM Gastos WHERE Grupo = ? AND Usuario = ?", new String[]{grupo.getTitulo(), username});

        //Por cada registro añadiremos la informacion en la lista de gastos
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

        //Pedimos todos los pagos que tenga el usuario y grupo que hemos recibido
        c = bbdd.rawQuery("SELECT * FROM Pagos WHERE Grupo = ? AND Usuario = ?", new String[]{grupo.getTitulo(), username});

        //Por cada registro añadiremos la informacion en la lista de pagos
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

        //Actualizar los balances antes de mostarlos
        grupo.actualizarBalances();

        //--------------------------------------------------------------------------------------------------------------

        //                                           LISTA

        //--------------------------------------------------------------------------------------------------------------

        lPersonas = (ListView) findViewById(R.id.lPersonas);
        pAdapter = new PersonaAdapter(getApplicationContext(), grupo.getPersonas(), grupo.getDivisa());
        lPersonas.setAdapter(pAdapter);

        //Launcher que recibira la respuesta de la actividad y la introducira en el modelo y en la base de datos
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

        //Si pulsamos en una persona abrir la actiidad que mostara la informacion de dicha persona
        lPersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intentInfo = new Intent(MainGrupo.this, InfoPersona.class);
                intentInfo.putExtra("grupo", grupo);
                intentInfo.putExtra("persona", grupo.getPersonas().get(position));
                startActivityIntent_InfoPersona.launch(intentInfo);
            }
        });

        ///Gestionar que se ve dependiendo si la lista esta vacia o llena
        lVacia = findViewById(R.id.lVacia);
        actualizarVacioLleno(grupo.getPersonas());

        final Integer[] posAborrar = {-1};

        //Generar el dialogo que se deberia de ver si pulsamos por un tiempo prolongado un a persona
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.delete_person));
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Borramos la persona de todos los lados y se lo notificamos al adaptador
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

        //Listener para borrar una persona del grupo.
        lPersonas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                posAborrar[0] = pos;
                dialogBorrar.show();
                return true;
            }
        });

        //inicilizar posibles autores con el nombre de las personas de la lista inicial
        posiblesPersonasNombre = new ArrayList<>();
        for (Persona p: grupo.getPersonas()) posiblesPersonasNombre.add(p.getNombre());

        //--------------------------------------------------------------------------------------------------------------

        //                                           BOTONES

        //--------------------------------------------------------------------------------------------------------------

        //Boton que nos permitira abrir añadir una persona a tarves de un dialogo
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
        Toast avisoGasto = Toast.makeText(this, getString(R.string.added_expense), tiempoToast);

        //Launcher que recibira la respuesta de la actividad y la introducira en el modelo y en la base de datos
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

        //Si pulsamos el botonde añadir gasto, mandamos a la actividad de añadir gasto los posibles autores del gasto
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

        Toast avisoPago = Toast.makeText(this, getString(R.string.added_payment), tiempoToast);

        //Launcher que recibira la respuesta de la actividad y la introducida en el modelo y en la base de datos
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

        //Si pulsamos el botonde añadir pago, mandamos a la actividad de añadir pago los posibles autores del pago
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

        //Launcher que recibira la respuesta de la actividad y la introducida en el modelo y en la base de datos
        ActivityResultLauncher<Intent> startActivityIntent_borrarPagos = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            ArrayList<Integer> borrados = result.getData().getIntegerArrayListExtra("pagosBorrados");

                            for (Integer i: borrados) {
                                for (Pago g: grupo.getPagos()) {
                                    if (g.getCodigo().equals(i)){
                                        grupo.getPagos().remove(g);
                                        grupo.actualizarBalances();

                                        //Borramos el gasto.
                                        bbdd.delete("Pagos","Codigo = ?", new String[]{String.valueOf(i)});

                                        pAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    }
                }
        );

        //Boton que nos mandara mediante un intent a la actividad de ver pagos
        FloatingActionButton bVerPagos = (FloatingActionButton) findViewById(R.id.bVerPagos);

        bVerPagos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentPagos = new Intent(MainGrupo.this, ListPagos.class);
                intentPagos.putExtra("grupo", grupo);
                startActivityIntent_borrarPagos.launch(intentPagos);
            }
        });

        //--------------------------------------------------------------------------------------------------------------
        //                                          VER GASTOS
        //--------------------------------------------------------------------------------------------------------------

        //Launcher que recibira la respuesta de la actividad y la introducida en el modelo y en la base de datos
        ActivityResultLauncher<Intent> startActivityIntent_borrarGastos = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        ArrayList<Integer> borrados = result.getData().getIntegerArrayListExtra("gastosBorrados");

                        for (Integer i: borrados) {
                            for (Gasto g: grupo.getGastos()) {
                                if (g.getCodigo().equals(i)){
                                    grupo.getGastos().remove(g);
                                    grupo.actualizarBalances();

                                    //Borramos el gasto.
                                    bbdd.delete("Gastos","Codigo = ?", new String[]{String.valueOf(i)});

                                    pAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
        );

        //Boton que nos mandara mediante un inten a la actividad de ver gastos
        FloatingActionButton bVerGastos = (FloatingActionButton) findViewById(R.id.bVerGastos);

        bVerGastos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intentGastos = new Intent(MainGrupo.this, ListGastos.class);
                Intent intentGastos = new Intent(MainGrupo.this, ListGastosCF.class);
                intentGastos.putExtra("grupo", grupo);
                startActivityIntent_borrarGastos.launch(intentGastos);
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

        //Si pulsamos el boton de opciones deplegamos o recogemos botones dependiendo del caso
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

        //Si pulsamos el boton de ver deplegamos o recogemos botones dependiendo del caso
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

                            //PendingIntent intentEnNot = PendingIntent.getActivity(getApplicationContext(), PendingIntent.FLAG_UPDATE_CURRENT, i, PendingIntent.FLAG_IMMUTABLE);

                            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getApplicationContext(), "IdCanal");
                            elBuilder.setSmallIcon(R.drawable.noti)
                                    .setContentTitle(getString(R.string.titulo_noti_ajuste))
                                    .setContentText(getString(R.string.cuerpo_noti_ajuste))
                                    //.addAction(0, getString(R.string.desplegar_ajustes), intentEnNot)
                                    .setVibrate(new long[]{0, 1000, 500, 1000})
                                    .setAutoCancel(true);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                elManager.createNotificationChannel(elCanal);
                            }

                            elManager.notify(1, elBuilder.build());
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