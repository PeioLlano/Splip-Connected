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

    private GoogleMap elmapa;
    private Grupo grupo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapa);

        SupportMapFragment elfragmento =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentoMapa);

        elfragmento.getMapAsync(this);

        grupo = (Grupo) getIntent().getSerializableExtra("grupo");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        elmapa = googleMap;
        elmapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        Float totalLon = 0.0f;
        Float totalLat = 0.0f;
        Integer cantPos = 0;

        for (Gasto g: grupo.getGastos()) {
            if(g.getLatitud() != null && g.getLongitud() != null) {
                elmapa.addMarker(new MarkerOptions()
                        .position(new LatLng(g.getLatitud(), g.getLongitud()))
                        .title(g.getTitulo()));

                totalLon += g.getLongitud();
                totalLat += g.getLatitud();
                cantPos += 1;
            }
        }

        if (cantPos != 0) {
            CameraPosition Poscam = new CameraPosition.Builder()
                    .target(new LatLng(totalLat/cantPos, totalLon/cantPos))
                    .zoom(7f)
                    .build();
            CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
            elmapa.animateCamera(otravista);
        }
        else{
            CameraPosition Poscam = new CameraPosition.Builder()
                    .target(new LatLng(33.2634167, 0.9505733))
                    .zoom(4.5f)
                    .build();
            CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
            elmapa.animateCamera(otravista);
        }



    }
}
