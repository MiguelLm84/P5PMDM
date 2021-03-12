package com.miguel_lm.newapptodo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.miguel_lm.newapptodo.R;

public class ActivityEscogerPosicion extends AppCompatActivity implements OnMapReadyCallback {

    private Location ultimaPosicion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escoger_posicion);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        ultimaPosicion = (Location) getIntent().getExtras().getParcelable("POSICION");

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng posicion = new LatLng(ultimaPosicion.getLatitude(), ultimaPosicion.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(posicion)
                .title("Posición escogida"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 12));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Intent intent = new Intent();
                intent.putExtra("POSICION", latLng);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}