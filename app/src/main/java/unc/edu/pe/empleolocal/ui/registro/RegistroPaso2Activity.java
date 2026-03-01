package unc.edu.pe.empleolocal.ui.registro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.databinding.ActivityRegistroPaso2Binding;

public class RegistroPaso2Activity extends AppCompatActivity {

    private ActivityRegistroPaso2Binding binding;
    private User user;
    private String password;
    private FusedLocationProviderClient fusedLocationClient;
    
    private double currentLat = 0;
    private double currentLng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegistroPaso2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = (User) getIntent().getSerializableExtra("user_data");
        password = getIntent().getStringExtra("password");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupDistritos();
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.layoutLocation.btnUseLocation.setOnClickListener(v -> getCurrentLocation());

        binding.layoutLocation.sliderRadius.addOnChangeListener((slider, value, fromUser) -> {
            String radiusText = (int)value + "km";
            binding.layoutLocation.tvRadiusValue.setText(radiusText);
        });

        binding.btnContinueStep2.setOnClickListener(v -> {
            if (currentLat == 0 || currentLng == 0) {
                Toast.makeText(this, "Por favor, selecciona tu ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setLatitud(currentLat);
            user.setLongitud(currentLng);
            user.setRadioBusqueda((int) binding.layoutLocation.sliderRadius.getValue());
            user.setDireccion(binding.layoutLocation.acDistrito.getText().toString());

            Intent intent = new Intent(RegistroPaso2Activity.this, RegistroPaso3Activity.class);
            intent.putExtra("user_data", user);
            intent.putExtra("password", password);
            startActivity(intent);
        });
    }

    private void setupDistritos() {
        String[] distritos = {"Cajamarca", "Baños del Inca", "Llacanora", "Namora", "Jesús", "Encañada"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, distritos);
        binding.layoutLocation.acDistrito.setAdapter(adapter);
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                getAddressFromLocation(currentLat, currentLng);
                Toast.makeText(this, "Ubicación obtenida correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación. Asegúrate de tener el GPS activo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAddressFromLocation(double lat, double lng) {
        if (!Geocoder.isPresent()) return;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                // Opcional: mostrar la dirección
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }
}
