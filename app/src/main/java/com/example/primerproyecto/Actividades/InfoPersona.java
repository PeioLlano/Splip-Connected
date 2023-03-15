package com.example.primerproyecto.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class InfoPersona extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_persona);

        //Recibimos el objeto Grupo y el objeto Persona que queremos mostrar
        Bundle extras = getIntent().getExtras();
        Grupo grupo = (Grupo) getIntent().getSerializableExtra("grupo");
        Persona persona = (Persona) getIntent().getSerializableExtra("persona");

        LinearLayout linearVertical = findViewById(R.id.linearVertical);
        LinearLayout linearHbalance = linearVertical.findViewById(R.id.linearHbalance);
        LinearLayout linearHgastos = linearVertical.findViewById(R.id.linearHgastos);
        LinearLayout linearHpagos = linearVertical.findViewById(R.id.linearHpagos);

        TextView tPagos = linearHpagos.findViewById(R.id.tPagosInt);
        TextView tBalance = linearHbalance.findViewById(R.id.tBalance);
        TextView tGastos = linearHgastos.findViewById(R.id.tGastos);
        EditText eNombre = findViewById(R.id.eNombre);

        //Inicializamos los campos
        tPagos.setText(String.valueOf(persona.getPagos()));
        tGastos.setText(String.valueOf(persona.getGastos()));
        tBalance.setText(String.valueOf(persona.getBalance()) + " " + grupo.getDivisa());
        eNombre.setText(persona.getNombre());

        //Boton que utilizaremos para guardar la persona despues de cambiarla
        FloatingActionButton bGuardar = findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Si el nombre no está ya en el grupo mandamos los datos de la persona cambiada y el nuevo nombre a Main grupo para que lo gestione.
                if (!grupo.tieneNombre(eNombre.getText().toString())){
                    Intent intent = new Intent();

                    if (!persona.getNombre().equals(eNombre.getText().toString())) intent.putExtra("nuevoNombre", eNombre.getText().toString());
                    else intent.putExtra("nuevoNombre", "no cambio");
                    intent.putExtra("persona", persona);

                    setResult(RESULT_OK, intent);
                    finish();
                }
                //En caso contrario mostramos un toast
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoCambio = Toast.makeText(view.getContext(), getString(R.string.persona_existe), tiempoToast);
                    avisoCambio.show();
                }
            }
        });

        //En caso de pulsar el boton para retroceder, pondremos el flag que indique que no se a completado correctamente lo que se deberia de haber hecho.
        FloatingActionButton bAtras = findViewById(R.id.bAtras);
        bAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
    }
}
