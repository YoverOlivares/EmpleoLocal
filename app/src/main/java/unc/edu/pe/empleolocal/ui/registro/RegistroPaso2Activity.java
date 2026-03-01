package unc.edu.pe.empleolocal.ui.registro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.databinding.ActivityRegistroPaso2Binding;
import unc.edu.pe.empleolocal.ui.auth.AuthViewModel;
import unc.edu.pe.empleolocal.ui.main.MainActivity;
import unc.edu.pe.empleolocal.utils.NotificationHelper;
import unc.edu.pe.empleolocal.utils.ViewUtils;

public class RegistroPaso2Activity extends AppCompatActivity {

    private ActivityRegistroPaso2Binding binding;
    private AuthViewModel authViewModel;
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

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        user = (User) getIntent().getSerializableExtra("user_data");
        password = getIntent().getStringExtra("password");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Limpiar campos
        binding.layoutLocation.etRegion.setText("");
        binding.layoutLocation.etProvincia.setText("");
        binding.layoutLocation.etDistrito.setText("");
        
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
                ViewUtils.showSnackbar(this, "Debe activar su ubicación para finalizar", ViewUtils.MsgType.WARNING);
                return;
            }

            String region = binding.layoutLocation.etRegion.getText().toString().trim();
            String provincia = binding.layoutLocation.etProvincia.getText().toString().trim();
            String distrito = binding.layoutLocation.etDistrito.getText().toString().trim();

            if (region.isEmpty() || provincia.isEmpty() || distrito.isEmpty()) {
                ViewUtils.showSnackbar(this, "Información de ubicación incompleta", ViewUtils.MsgType.WARNING);
                return;
            }

            // Completar objeto usuario
            user.setLatitud(currentLat);
            user.setLongitud(currentLng);
            user.setRadioBusqueda((int) binding.layoutLocation.sliderRadius.getValue());
            user.setDireccion(distrito + ", " + provincia + ", " + region);

            // Solicitar permiso de notificaciones antes de registrar (Android 13+)
            checkNotificationPermissionAndRegister();
        });

        setupObservers();
    }

    private void checkNotificationPermissionAndRegister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                authViewModel.register(user, password);
            }
        } else {
            authViewModel.register(user, password);
        }
    }

    private void setupObservers() {
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                NotificationHelper.showWelcomeNotification(this, user.getNombre());
                ViewUtils.showSnackbar(this, "¡Bienvenido! Registro completado", ViewUtils.MsgType.SUCCESS);
                
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                ViewUtils.showSnackbar(this, error, ViewUtils.MsgType.ERROR);
            }
        });

        authViewModel.getIsLoading().observe(this, loading -> {
            binding.btnContinueStep2.setEnabled(!loading);
            binding.btnContinueStep2.setText(loading ? "Procesando..." : "Finalizar Registro");
        });
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        ViewUtils.showSnackbar(this, "Detectando ubicación...", ViewUtils.MsgType.INFO);

        CurrentLocationRequest request = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationClient.getCurrentLocation(request, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                fillLocationData(currentLat, currentLng);
            } else {
                ViewUtils.showSnackbar(this, "Active su GPS para obtener la ubicación.", ViewUtils.MsgType.ERROR);
            }
        });
    }

    private void fillLocationData(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, new Locale("es", "PE"));
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String countryCode = address.getCountryCode();
                
                if (countryCode != null && countryCode.equalsIgnoreCase("PE")) {
                    binding.layoutLocation.etRegion.setText(address.getAdminArea());
                    binding.layoutLocation.etProvincia.setText(address.getSubAdminArea());
                    String dist = address.getLocality() != null ? address.getLocality() : address.getSubLocality();
                    binding.layoutLocation.etDistrito.setText(dist);
                    ViewUtils.showSnackbar(this, "Ubicación en Perú detectada", ViewUtils.MsgType.SUCCESS);
                } else {
                    resetLocationFields();
                    ViewUtils.showSnackbar(this, "Solo disponible en Perú", ViewUtils.MsgType.ERROR);
                }
            }
        } catch (IOException e) {
            ViewUtils.showSnackbar(this, "Error de red al detectar ubicación", ViewUtils.MsgType.ERROR);
        }
    }

    private void resetLocationFields() {
        currentLat = 0;
        currentLng = 0;
        binding.layoutLocation.etRegion.setText("");
        binding.layoutLocation.etProvincia.setText("");
        binding.layoutLocation.etDistrito.setText("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 || (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            if (requestCode == 100) getCurrentLocation();
            else authViewModel.register(user, password);
        }
    }
}
