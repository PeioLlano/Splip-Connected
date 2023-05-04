package com.example.primerproyecto.Actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.primerproyecto.Clases.Gasto;
import com.example.primerproyecto.Clases.Grupo;
import com.example.primerproyecto.Clases.Persona;
import com.example.primerproyecto.R;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaGastos extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap elmapa; // variable para guardar la instancia del objeto GoogleMap
    private Grupo grupo; // variable para guardar un objeto de tipo Grupo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa); // se carga el layout de la actividad

        SupportMapFragment elfragmento =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentoMapa); // se busca el fragmento donde se colocará el mapa

        elfragmento.getMapAsync(this); // se inicia la carga del mapa

        grupo = (Grupo) getIntent().getSerializableExtra("grupo"); // se recupera el objeto Grupo enviado desde la actividad anterior
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        elmapa = googleMap; // se asigna la instancia del objeto GoogleMap recibido como parámetro a la variable elmapa
        elmapa.setMapType(GoogleMap.MAP_TYPE_NORMAL); // se define el tipo de mapa a mostrar


        Float totalLon = 0.0f; // variable para guardar la suma de longitudes
        Float totalLat = 0.0f; // variable para guardar la suma de latitudes
        Integer cantPos = 0; // variable para guardar la cantidad de posiciones de los gastos del grupo

        for (Gasto g: grupo.getGastos()) { // se recorre la lista de gastos del grupo
            if(g.getLatitud() != null && g.getLongitud() != null) { // si el gasto tiene latitud y longitud
                elmapa.addMarker(new MarkerOptions()
                        .position(new LatLng(g.getLatitud(), g.getLongitud()))
                        .title(g.getTitulo())); // se agrega un marcador en el mapa en la posición del gasto

                totalLon += g.getLongitud(); // se suma la longitud del gasto actual
                totalLat += g.getLatitud(); // se suma la latitud del gasto actual
                cantPos += 1; // se aumenta el contador de posiciones
            }
        }

        if (cantPos != 0) {
            CameraPosition Poscam = new CameraPosition.Builder()
                    .target(new LatLng(totalLat/cantPos, totalLon/cantPos))
                    .zoom(8f)
                    .build();
            CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
            elmapa.animateCamera(otravista);
        }
        else {
            CameraPosition Poscam = new CameraPosition.Builder()
                    .target(new LatLng(43.9785280, 15.3833720))
                    .zoom(15.5f)
                    .build();
            CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
            elmapa.animateCamera(otravista);
        }
    }
}
