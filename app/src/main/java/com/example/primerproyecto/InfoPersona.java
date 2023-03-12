package com.example.primerproyecto;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class InfoPersona extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_persona);

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

        tPagos.setText(String.valueOf(persona.getPagos()));
        tGastos.setText(String.valueOf(persona.getGastos()));
        tBalance.setText(String.valueOf(persona.getBalance()) + " " + grupo.getDivisa());
        eNombre.setText(persona.getNombre());

        FloatingActionButton bGuardar = (FloatingActionButton) findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!grupo.tieneNombre(eNombre.getText().toString()) || persona.getNombre().equals(eNombre.getText().toString())){
                    Intent intent = new Intent();

                    if (!persona.getNombre().equals(eNombre.getText().toString())) intent.putExtra("nuevoNombre", eNombre.getText().toString());
                    else intent.putExtra("nuevoNombre", "no cambio");
                    intent.putExtra("persona", persona);

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoCambio = Toast.makeText(view.getContext(), getString(R.string.persona_existe), tiempoToast);
                    avisoCambio.show();
                }
            }
        });

        FloatingActionButton bAtras = (FloatingActionButton) findViewById(R.id.bAtras);
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
