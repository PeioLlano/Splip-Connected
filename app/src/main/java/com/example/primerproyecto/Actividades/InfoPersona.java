package com.example.primerproyecto.Actividades;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;

public class InfoPersona extends AppCompatActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private ImageView fotoPerfil;

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

                    Intent intent = new Intent();

                    if (!persona.getNombre().equals(eNombre.getText().toString())) intent.putExtra("nuevoNombre", eNombre.getText().toString());
                    else intent.putExtra("nuevoNombre", persona.getNombre());
                    intent.putExtra("persona", persona);

                    fotoPerfil.setDrawingCacheEnabled(true);
                    fotoPerfil.buildDrawingCache();
                    Bitmap bitmap = fotoPerfil.getDrawingCache();

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    System.out.println(byteArray.length);
                    String fotoStr = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    intent.putExtra("foto", fotoStr);

                    setResult(RESULT_OK, intent);
                    finish();
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

        fotoPerfil = findViewById(R.id.imageView);
        fotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permisosCamara();
            }
        });

        String fotoStr = persona.getFoto();
        if (!(fotoStr == null || fotoStr.equals("null"))){
            byte[] decodedByteArray = Base64.decode(fotoStr, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
            fotoPerfil.setImageBitmap(bitmap);
        }

    }

    private void permisosCamara() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camara, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (ContextCompat.checkSelfPermission(InfoPersona.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
            else {
                Toast.makeText(this, "Se necesitan permisos de camara para ejecutar esta accion", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUEST_CODE){
            if (data != null) {
                Bitmap imagen = (Bitmap) data.getExtras().get("data");
                fotoPerfil.setImageBitmap(imagen);
            }
        }
    }
}
