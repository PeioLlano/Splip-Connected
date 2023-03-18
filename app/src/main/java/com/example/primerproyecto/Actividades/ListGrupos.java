package com.example.primerproyecto.Actividades;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.primerproyecto.BBDD.BBDD;
import com.example.primerproyecto.Dialogs.AddGroupDialog;
import com.example.primerproyecto.Dialogs.EstiloDialog;
import com.example.primerproyecto.Dialogs.IdiomaDialog;
import com.example.primerproyecto.ListAdapters.GroupAdapter;
import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Pago;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Locale;

public class ListGrupos extends AppCompatActivity implements IdiomaDialog.ListenerdelDialogoIdioma, EstiloDialog.ListenerdelDialogoEstilo, AddGroupDialog.AddGroupDialogListener {

    ArrayList<Grupo> arraydedatos= new ArrayList<>();
    private GroupAdapter pAdapter;
    String username;
    SQLiteDatabase bbdd;
    ListView lGrupos;
    LinearLayout lVacia;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bbdd.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        for (Grupo gru: arraydedatos) {
            Cursor c2 = bbdd.rawQuery("SELECT * FROM Personas WHERE Grupo = ? AND Usuario = ?", new String[]{gru.getTitulo(), username});

            if (c2.moveToFirst()) {
                do {
                    String Nombre = c2.getString(2);

                    if (!gru.tieneNombre(Nombre)) gru.getPersonas().add(new Persona(Nombre, 0f, 0, 0));
                } while (c2.moveToNext());
            }
        }


        pAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_grupos);

        //Obtenemos el usuario necesario para obtener los demas datos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("usuario");
        }

        //Abrimos la conexion con la base de datos
        BBDD gestorBBDD = new BBDD(this, "SpliP", null, 1);
        bbdd = gestorBBDD.getWritableDatabase();

        //Pedimos todos los Grupos que tenga el usuario que hemos recibido
        Cursor c = bbdd.rawQuery("SELECT * FROM Grupos WHERE Usuario = ?", new String[]{username});

        //Por cada grupo buscaremos todas las personas y añadiremos ambos objetos a sus repectivas listas
        if (c.moveToFirst()){
            do{
                String Titulo = c.getString(1);
                String Divisa = c.getString(2);

                Cursor c2 = bbdd.rawQuery("SELECT * FROM Personas WHERE Grupo = ? AND Usuario = ?", new String[]{Titulo, username});
                ArrayList<Persona> personasGrupo = new ArrayList<>();

                if (c2.moveToFirst()) {
                    do {
                        String Nombre = c2.getString(2);

                        personasGrupo.add(new Persona(Nombre, 0f, 0, 0));
                    } while (c2.moveToNext());
                }

                arraydedatos.add(new Grupo(Titulo, Divisa, personasGrupo, new ArrayList<Gasto>(), new ArrayList<Pago>()));

            }while(c.moveToNext());
        }


        //Inicializamos la lisat de grupos
        lGrupos = (ListView) findViewById(R.id.lPersonas);
        lVacia = findViewById(R.id.lVacia);

        pAdapter = new GroupAdapter(getApplicationContext(), arraydedatos);

        lGrupos.setAdapter(pAdapter);
        //Si clicamos un grupo nos lleve a MainGrupo
        lGrupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent = new Intent(ListGrupos.this, MainGrupo.class);
                intent.putExtra("grupo", arraydedatos.get(position));
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        ///Gestionar que se ve dependiendo si la lista esta vacia o llena
        actualizarVacioLleno(arraydedatos);

        final Integer[] posAborrar = {-1};

        //Generar el dialogo que se deberia de ver si pulsamos por un tiempo prolongado un grupo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.delete_group));
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Borramos el grupo de todos los lados y se lo notificamos al adaptador
                        bbdd.delete("Grupos", "Titulo = ? AND Usuario = ?", new String[]{arraydedatos.get(posAborrar[0]).getTitulo(), username});
                        arraydedatos.remove(((int)posAborrar[0]));
                        pAdapter.notifyDataSetChanged();
                        actualizarVacioLleno(arraydedatos);
                        posAborrar[0] = -1;
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialogBorrar = builder.create();



        //Si pulsamos por un tiempo prolongado un grupo mostramos el dialogo
        lGrupos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                posAborrar[0] = pos;
                dialogBorrar.show();
                return true;
            }
        });

        FloatingActionButton bLanguage = findViewById(R.id.bLanguage);
        //Boton para gestionar el idioma, se muestra el dialogo que es para ello.
        bLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IdiomaDialog dialog = new IdiomaDialog();
                dialog.show(getSupportFragmentManager(), "DialogoIdioma");
            }
        });

        FloatingActionButton bStyle =  findViewById(R.id.bStyle);
        //Boton para gestionar el estilo, se muestra el dialogo que es para ello.
        bStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EstiloDialog dialog = new EstiloDialog();
                dialog.show(getSupportFragmentManager(), "DialogoEstilo");
            }
        });

        //Boton para cerrar sesion
        FloatingActionButton bLogOut = findViewById(R.id.bLogOut);
        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Quitamos la preferencia del usuario loggeado y abrimo la actividad de login
                SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("loged_user", "");
                editor.commit();

                Intent intent = new Intent(ListGrupos.this, Login.class);
                startActivity(intent);
                ListGrupos.this.finish();
            }
        });

        TextView tPosibleLanguages = findViewById(R.id.tPosibleLanguages);
        TextView tChangeLanguages = findViewById(R.id.tChangeLanguages);

        TextView tPosibleStyles = findViewById(R.id.tPosibleStyles);
        TextView tChangeStyle = findViewById(R.id.tChangeStyle);

        TextView tLogOut = findViewById(R.id.tLogOut);
        TextView tLogOutAcount = findViewById(R.id.tLogOutAcount);

        final Boolean[] clicadoAjustes = {false};

        TextView tPlusGrupo = findViewById(R.id.tPlusGrupo);
        TextView tGrupoDeGastos = findViewById(R.id.tGrupoDeGastos);

        FloatingActionButton bPlusGrupo = findViewById(R.id.bPlusGrupo);

        //Gesrionamos mediante un dialogo añadir un grupo al pulsar el boton
        bPlusGrupo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGroupDialog addGroupDialog = new AddGroupDialog();
                addGroupDialog.show(getSupportFragmentManager(), "add group dialog");
            }
        });

        final Boolean[] clicadoOpciones = {false};

        FloatingActionButton bOtions = findViewById(R.id.bOtions);

        FloatingActionButton bSettings = findViewById(R.id.bMail);

        //Si pulsamos el boton de ajustes deplegamos o recogemos botones dependiendo del caso
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicadoAjustes[0]) {
                    bLanguage.setVisibility(View.VISIBLE);
                    bLanguage.setClickable(true);

                    bStyle.setVisibility(View.VISIBLE);
                    bStyle.setClickable(true);

                    bLogOut.setVisibility(View.VISIBLE);
                    bLogOut.setClickable(true);

                    tChangeLanguages.setVisibility(View.VISIBLE);
                    tPosibleLanguages.setVisibility(View.VISIBLE);

                    tChangeStyle.setVisibility(View.VISIBLE);
                    tPosibleStyles.setVisibility(View.VISIBLE);

                    tLogOut.setVisibility(View.VISIBLE);
                    tLogOutAcount.setVisibility(View.VISIBLE);

                    bOtions.setImageResource(R.drawable.left);

                    bPlusGrupo.setVisibility(View.INVISIBLE);
                    bPlusGrupo.setClickable(false);

                    tPlusGrupo.setVisibility(View.INVISIBLE);
                    tGrupoDeGastos.setVisibility(View.INVISIBLE);

                    clicadoOpciones[0] = false;
                }
                else {
                    bLanguage.setVisibility(View.INVISIBLE);
                    bLanguage.setClickable(false);

                    bStyle.setVisibility(View.INVISIBLE);
                    bStyle.setClickable(false);

                    bLogOut.setVisibility(View.INVISIBLE);
                    bLogOut.setClickable(true);

                    tChangeLanguages.setVisibility(View.INVISIBLE);
                    tPosibleLanguages.setVisibility(View.INVISIBLE);

                    tChangeStyle.setVisibility(View.INVISIBLE);
                    tPosibleStyles.setVisibility(View.INVISIBLE);

                    tLogOut.setVisibility(View.INVISIBLE);
                    tLogOutAcount.setVisibility(View.INVISIBLE);
                }
                clicadoAjustes[0] = !clicadoAjustes[0];
            }
        });

        //Si pulsamos el boton de opciones deplegamos o recogemos botones dependiendo del caso
        bOtions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clicadoOpciones[0]) {
                    bOtions.setImageResource(R.drawable.top);

                    bPlusGrupo.setVisibility(View.VISIBLE);
                    bPlusGrupo.setClickable(true);

                    tPlusGrupo.setVisibility(View.VISIBLE);
                    tGrupoDeGastos.setVisibility(View.VISIBLE);

                    bLanguage.setVisibility(View.INVISIBLE);
                    bLanguage.setClickable(false);

                    bStyle.setVisibility(View.INVISIBLE);
                    bStyle.setClickable(false);

                    bLogOut.setVisibility(View.INVISIBLE);
                    bLogOut.setClickable(true);

                    tChangeLanguages.setVisibility(View.INVISIBLE);
                    tPosibleLanguages.setVisibility(View.INVISIBLE);

                    tChangeStyle.setVisibility(View.INVISIBLE);
                    tPosibleStyles.setVisibility(View.INVISIBLE);

                    tLogOut.setVisibility(View.INVISIBLE);
                    tLogOutAcount.setVisibility(View.INVISIBLE);

                    clicadoAjustes[0] = false;
                }
                else {
                    bOtions.setImageResource(R.drawable.left);

                    bPlusGrupo.setVisibility(View.INVISIBLE);
                    bPlusGrupo.setClickable(false);

                    tPlusGrupo.setVisibility(View.INVISIBLE);
                    tGrupoDeGastos.setVisibility(View.INVISIBLE);
                }
                clicadoOpciones[0] = !clicadoOpciones[0];
            }
        });
    }

    //Al tratar de cerrar la app preguntamos si esta seguro mediante dialogo
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.really_exit)
                .setMessage(R.string.really_exit_long)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ListGrupos.super.onBackPressed();
                    }
                }).create().show();
    }

    //Implementamos el metodo del dailogo de añadir grupo.
    @Override
    public void añadirGrupo(String Name, String currency) {

        //Hacemos try de insertar el grupo para mostrar un toast en caso de que no se pueda insertar
        try{
            ContentValues contentValues = new ContentValues();
            contentValues.put("Usuario", username);
            contentValues.put("Titulo", Name);
            contentValues.put("Divisa", currency);

            bbdd.insertOrThrow("Grupos", null, contentValues);

            arraydedatos.add(new Grupo(Name, currency, new ArrayList<Persona>(), new ArrayList<Gasto>(), new ArrayList<Pago>()));

            pAdapter.notifyDataSetChanged();

            actualizarVacioLleno(arraydedatos);
        }
        catch (SQLiteConstraintException e) {
            e.printStackTrace();
            Toast aviso = Toast.makeText(getApplicationContext(), getResources().getString(R.string.grupo_existe), Toast.LENGTH_SHORT);
            aviso.show();
        }

    }

    //Implementamos el metodo del dailogo de elegir idioma.
    @Override
    public void alElegirIdioma(int i) {
        int tiempoToast= Toast.LENGTH_SHORT;
        CharSequence[] opciones = {getString(R.string.English), getString(R.string.Spanish), getString(R.string.Euskera)};
        Toast avisoIdiomaCambiado = Toast.makeText(this, getString(R.string.language_changed_to) + opciones[i], tiempoToast);

        guardarPreferenciaIdioma((String) opciones[i]);

        switch (i){
            case 0:
                Locale nuevaloc = new Locale("en");
                Locale.setDefault(nuevaloc);
                Configuration configuration = getBaseContext().getResources().getConfiguration();
                configuration.setLocale(nuevaloc);
                configuration.setLayoutDirection(nuevaloc);

                Context context = getBaseContext().createConfigurationContext(configuration);
                getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

                finish();
                startActivity(getIntent());
                break;
            case 1:
                Locale nuevalocEs = new Locale("es");
                Locale.setDefault(nuevalocEs);
                Configuration configurationEs = getBaseContext().getResources().getConfiguration();
                configurationEs.setLocale(nuevalocEs);
                configurationEs.setLayoutDirection(nuevalocEs);

                Context contextEs = getBaseContext().createConfigurationContext(configurationEs);
                getBaseContext().getResources().updateConfiguration(configurationEs, contextEs.getResources().getDisplayMetrics());

                finish();
                startActivity(getIntent());
                break;
            case 2:
                Locale nuevalocEu = new Locale("eu");
                Locale.setDefault(nuevalocEu);
                Configuration configurationEu = getBaseContext().getResources().getConfiguration();
                configurationEu.setLocale(nuevalocEu);
                configurationEu.setLayoutDirection(nuevalocEu);

                Context contextEu = getBaseContext().createConfigurationContext(configurationEu);
                getBaseContext().getResources().updateConfiguration(configurationEu, contextEu.getResources().getDisplayMetrics());

                finish();
                startActivity(getIntent());
                break;
        }
        avisoIdiomaCambiado.show();
    }

    //Implementamos el metodo del dailogo de elegir estilo
    @Override
    public void alElegirEstilo(int i) {
        int tiempoToast= Toast.LENGTH_SHORT;
        CharSequence[] opciones = {"Dark", getString(R.string.normal)};
        Toast avisoEstiloCambiado = Toast.makeText(this, getString(R.string.style_changed_to) + opciones[i], tiempoToast);
        avisoEstiloCambiado.show();

        guardarPreferenciaEstilo((String) opciones[i]);

        switch (i) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
        }
    }

    //Guardar las preferencias de estilo
    public void guardarPreferenciaEstilo(String tema){
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("tema", tema);
        editor.commit();
    }

    //Guardar las preferencias de idioma
    public void guardarPreferenciaIdioma(String idioma){
        SharedPreferences preferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("idioma", idioma);
        editor.commit();
    }

    //Actualizar lo que se ve dependiendo del tamaño de la lista.
    private void actualizarVacioLleno(ArrayList<Grupo> grupos) {

        if(grupos.size() > 0) {
            lVacia.setVisibility(View.GONE);
            lGrupos.setVisibility(View.VISIBLE);
        } else {
            lVacia.setVisibility(View.VISIBLE);
            lGrupos.setVisibility(View.GONE);
        }
    }

}