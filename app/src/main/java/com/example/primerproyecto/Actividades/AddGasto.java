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

public class AddGasto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gasto);

        EditText eCantidad = findViewById(R.id.eCantidad);
        EditText eNombre = findViewById(R.id.eNombre);

        Spinner sAutor = findViewById(R.id.sAutor);

        //Recibimos los posibles autores del gasto para poder ponerlos en el spinner
        Bundle extras = getIntent().getExtras();
        ArrayList<String> posiblesAutores = null;
        if (extras != null) {
            posiblesAutores = extras.getStringArrayList("posiblesAutores");
        }

        //Motamos el spinner con la lista de posibles autores
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_texto, posiblesAutores);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sAutor.setAdapter(adapter);

        //Boton que usaremos para guardar los gastos
        FloatingActionButton bGuardar = findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Si todos los campos estan rellenos devolvemos los datos que se han rellenado a MainGrupo para que los gestione
                if (todo_relleno(eNombre.getText().toString(), eCantidad.getText().toString(), sAutor.getSelectedItem().toString())) {
                    Intent intent = new Intent();

                    intent.putExtra("nombre", eNombre.getText().toString());
                    intent.putExtra("cantidad", eCantidad.getText().toString());
                    intent.putExtra("autor", sAutor.getSelectedItem().toString());

                    setResult(RESULT_OK, intent);
                    finish();
                }
                //En caso contrario se muestra un Toast.
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoGasto = Toast.makeText(view.getContext(), getString(R.string.fill_fields), tiempoToast);
                    avisoGasto.show();
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
    private boolean todo_relleno(String nombre, String cantidad, String persona) {
        return ((!nombre.equals("")) && (!cantidad.equals("")) && (!persona.equals("")));
    }
}
