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

public class AddGasto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_gasto);

        EditText eCantidad = (EditText) findViewById(R.id.eCantidad);
        EditText eNombre = (EditText) findViewById(R.id.eNombre);

        Spinner sAutor = (Spinner) findViewById(R.id.sAutor);

        Bundle extras = getIntent().getExtras();
        ArrayList<String> posiblesAutores = null;
        if (extras != null) {
            posiblesAutores = extras.getStringArrayList("posiblesAutores");
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_texto, posiblesAutores);
        adapter.setDropDownViewResource(R.layout.spinner_drop);
        sAutor.setAdapter(adapter);

        FloatingActionButton bGuardar = (FloatingActionButton) findViewById(R.id.bGuardar);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (todo_relleno(eNombre.getText().toString(), eCantidad.getText().toString(), sAutor.getSelectedItem().toString())) {
                    Intent intent = new Intent();

                    intent.putExtra("nombre", eNombre.getText().toString());
                    intent.putExtra("cantidad", eCantidad.getText().toString());
                    intent.putExtra("autor", sAutor.getSelectedItem().toString());

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    int tiempoToast= Toast.LENGTH_SHORT;
                    Toast avisoGasto = Toast.makeText(view.getContext(), "Rellene todos los campos.", tiempoToast);
                    avisoGasto.show();
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

    private boolean todo_relleno(String nombre, String cantidad, String persona) {
        return ((!nombre.equals("")) && (!cantidad.equals("")) && (!persona.equals("")));
    }
}
