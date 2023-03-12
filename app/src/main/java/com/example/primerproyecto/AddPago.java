package com.example.primerproyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class AddPago extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pago);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> posiblesPersonas = null;
        if (extras != null) {
            posiblesPersonas = extras.getStringArrayList("posiblesPersonas");
        }

        EditText eCantidad = (EditText) findViewById(R.id.eCantidad);

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_texto, posiblesPersonas);
        adapter.setDropDownViewResource(R.layout.spinner_drop);

        Spinner sAutor = (Spinner) findViewById(R.id.sAutor);
        sAutor.setAdapter(adapter);

        Spinner sDestinatario = (Spinner) findViewById(R.id.sDestinatario);
        sDestinatario.setAdapter(adapter);

        FloatingActionButton bGuardar = (FloatingActionButton) findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todo_relleno(eCantidad.getText().toString(), sAutor.getSelectedItem().toString(), sDestinatario.getSelectedItem().toString())) {
                    Date today = new Date();
                    today.setHours(0);

                    Intent intent = new Intent();

                    intent.putExtra("cantidad", eCantidad.getText().toString());
                    intent.putExtra("autor", sAutor.getSelectedItem().toString());
                    intent.putExtra("destinatario", sDestinatario.getSelectedItem().toString());
                    intent.putExtra("fecha", today);

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoPago = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                    avisoPago.show();
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

    private boolean todo_relleno(String cantidad, String persona, String persona2) {
        return ((!cantidad.equals("")) && (!persona.equals("")) && (!persona2.equals("")));
    }
}
