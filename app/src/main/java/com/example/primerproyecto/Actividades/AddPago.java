package com.example.primerproyecto.Actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;

public class AddPago extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pago);

        //Recibimos los posibles autores del pago para poder ponerlos en el spinner
        Bundle extras = getIntent().getExtras();
        ArrayList<String> posiblesPersonas = null;
        if (extras != null) {
            posiblesPersonas = extras.getStringArrayList("posiblesPersonas");
        }

        EditText eCantidad = findViewById(R.id.eCantidad);

        //Motamos los spinner con la lista de posibles autores
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_texto, posiblesPersonas);
        adapter.setDropDownViewResource(R.layout.spinner_drop);

        Spinner sAutor = findViewById(R.id.sAutor);
        sAutor.setAdapter(adapter);

        Spinner sDestinatario = findViewById(R.id.sDestinatario);
        sDestinatario.setAdapter(adapter);

        //Boton que usaremos para guardar los pagos
        FloatingActionButton bGuardar = findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Si todos los campos estan rellenos devolvemos los datos que se han rellenado a MainGrupo para que los gestione
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
                //En caso contrario se muestra un Toast.
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoPago = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                    avisoPago.show();
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

    //Comprobar si todos los campos son rellenos.
    private boolean todo_relleno(String cantidad, String persona, String persona2) {
        return ((!cantidad.equals("")) && (!persona.equals("")) && (!persona2.equals("")));
    }
}
