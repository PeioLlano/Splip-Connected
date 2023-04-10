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

        elmapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                elmapa.clear();
                elmapa.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("El marcador"));

            }
        });

        elmapa.addMarker(new MarkerOptions().position(new LatLng(43.2634167, -2.9505733)).title("1"));
        elmapa.addMarker(new MarkerOptions().position(new LatLng(40.2634167, -12.9505733)).title("2"));
        elmapa.addMarker(new MarkerOptions().position(new LatLng(46.2634167, 5.9505733)).title("3"));
        elmapa.addMarker(new MarkerOptions().position(new LatLng(33.2634167, 0.9505733)).title("4"));


        CameraPosition Poscam = new CameraPosition.Builder()
                .target(new LatLng(33.2634167, 0.9505733))
                .zoom(4.5f)
                .build();
        CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);
        elmapa.animateCamera(otravista);

        //for (Gasto g: grupo.getGastos()) {
        //    elmapa.addMarker(new MarkerOptions()
        //            .position(new LatLng(g.getLatitude(), g.getLongitude()))
        //            .title(g.getTitulo()));
        //}
    }
}
