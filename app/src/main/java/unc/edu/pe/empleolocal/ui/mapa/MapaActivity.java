package unc.edu.pe.empleolocal.ui.mapa;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import unc.edu.pe.empleolocal.R;

public class MapaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_empleos);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar el botón de mi ubicación (simulado por ahora)
        findViewById(R.id.fab_my_location).setOnClickListener(v -> {
            if (mMap != null) {
                LatLng cajamarca = new LatLng(-7.1561, -78.5147);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cajamarca, 15));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Añadir un marcador de ejemplo en Cajamarca y mover la cámara
        LatLng cajamarca = new LatLng(-7.1561, -78.5147);
        mMap.addMarker(new MarkerOptions().position(cajamarca).title("Cajamarca, Perú"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cajamarca, 13));

        // Configurar el listener para los marcadores si es necesario
        mMap.setOnMarkerClickListener(marker -> {
            findViewById(R.id.card_selected_job).setVisibility(View.VISIBLE);
            return false;
        });

        // Ocultar card al tocar el mapa
        mMap.setOnMapClickListener(latLng -> {
            findViewById(R.id.card_selected_job).setVisibility(View.GONE);
        });
    }
}