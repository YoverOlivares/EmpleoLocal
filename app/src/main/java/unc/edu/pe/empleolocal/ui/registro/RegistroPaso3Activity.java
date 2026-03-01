package unc.edu.pe.empleolocal.ui.registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
                Toast.makeText(this, "Por favor, selecciona al menos una habilidad", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setHabilidades(selectedSkills);
            authViewModel.register(user, password);
        });

        setupObservers();
    }

    private void setupObservers() {
        authViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroPaso3Activity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
        
        authViewModel.getIsLoading().observe(this, loading -> binding.btnFinish.setEnabled(!loading));
    }
}
