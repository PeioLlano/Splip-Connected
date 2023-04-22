package com.example.primerproyecto.Actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.primerproyecto.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AddGasto extends AppCompatActivity {
    private com.google.android.gms.location.LocationRequest locationRequest;
    private Float latitud = null;
    private Float longitud = null;
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
                    intent.putExtra("latitud", latitud);
                    intent.putExtra("longitud", longitud);

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

        solicitarPermisosUbicacion();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        getUbicacionActual();

    }

    private boolean todo_relleno(String nombre, String cantidad, String persona) {
        // Verificar si todos los campos del formulario están llenos
        return ((!nombre.equals("")) && (!cantidad.equals("")) && (!persona.equals("")));
    }

    private void solicitarPermisosUbicacion() {
        // Verificar si se requieren permisos para obtener la ubicación actual
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permisos si no se han concedido
                ActivityCompat.requestPermissions(this, new
                        String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 11);
            }
        }
    }

    private boolean gpsActivo() {
        LocationManager locationManager = null;
        boolean activo = false;

        if (locationManager == null) {
            // Obtener el servicio de ubicación del sistema si aún no se ha hecho
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }

        // Verificar si el GPS está activado en el dispositivo
        activo = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return activo;
    }

    private void encenderGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // Mostrar un mensaje si el GPS ya está encendido
                    Toast.makeText(AddGasto.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Si es necesario, solicitar al usuario que active el GPS
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(AddGasto.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // El dispositivo no tiene capacidad de ubicación
                            break;
                    }
                }
            }
        });
    }

    private void getUbicacionActual() {

        // Verificar que el SDK mínimo es M o superior (Marshmallow o superior)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Verificar que la aplicación tenga permiso de ACCESS_FINE_LOCATION
            if (ActivityCompat.checkSelfPermission(AddGasto.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                // Verificar que el GPS esté habilitado
                if (gpsActivo()) {

                    // Solicitar actualizaciones de ubicación al proveedor de ubicación fusionado de Google Play Services
                    LocationServices.getFusedLocationProviderClient(AddGasto.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    // Detener las actualizaciones de ubicación
                                    LocationServices.getFusedLocationProviderClient(AddGasto.this)
                                            .removeLocationUpdates(this);

                                    // Obtener la última ubicación
                                    if (locationResult != null && locationResult.getLocations().size() >0){

                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();

                                        // Convertir la latitud y longitud en flotantes y asignarlas a las variables globales latitud y longitud
                                        latitud = (float) latitude;
                                        longitud = (float) longitude;

                                        // Mostrar la ubicación en la consola de depuración
                                        Log.d("ubicacion", "Ubicacion: " + latitud + ", " + longitud);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    // Si el GPS no está habilitado, mostrar un diálogo para que el usuario lo habilite
                    encenderGPS();
                }

            } else {
                // Si la aplicación no tiene permiso de ACCESS_FINE_LOCATION, solicitar el permiso
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

}
