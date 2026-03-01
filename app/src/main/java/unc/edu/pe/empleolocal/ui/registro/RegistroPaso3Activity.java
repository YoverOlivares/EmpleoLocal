package unc.edu.pe.empleolocal.ui.registro;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.databinding.ActivityRegistroPaso3Binding;
import unc.edu.pe.empleolocal.ui.auth.AuthViewModel;
import unc.edu.pe.empleolocal.ui.main.MainActivity;
import unc.edu.pe.empleolocal.utils.NotificationHelper;
import unc.edu.pe.empleolocal.utils.ViewUtils;

public class RegistroPaso3Activity extends AppCompatActivity {

    private AuthViewModel authViewModel;
    private User user;
    private String password;
    private ActivityRegistroPaso3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityRegistroPaso3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        user = (User) getIntent().getSerializableExtra("user_data");
        password = getIntent().getStringExtra("password");

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnFinish.setOnClickListener(v -> {
            List<String> selectedSkills = new ArrayList<>();
            for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
                Chip chip = (Chip) binding.chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    selectedSkills.add(chip.getText().toString());
                }
            }

            if (selectedSkills.isEmpty()) {
                ViewUtils.showSnackbar(this, "Por favor, selecciona al menos una área de experiencia", ViewUtils.MsgType.WARNING);
                return;
            }

            user.setHabilidades(selectedSkills);
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
                ViewUtils.showSnackbar(this, "¡Bienvenido! Registro completado con éxito", ViewUtils.MsgType.SUCCESS);
                
                Intent intent = new Intent(RegistroPaso3Activity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                ViewUtils.showSnackbar(this, error, ViewUtils.MsgType.ERROR);
            }
        });
        
        authViewModel.getIsLoading().observe(this, loading -> binding.btnFinish.setEnabled(!loading));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            authViewModel.register(user, password);
        }
    }
}
