package unc.edu.pe.empleolocal.ui.perfil;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;

import unc.edu.pe.empleolocal.R;
import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.databinding.ActivityPerfilBinding;
import unc.edu.pe.empleolocal.ui.auth.iniciar_sesion;
import unc.edu.pe.empleolocal.utils.ViewUtils;

public class PerfilActivity extends AppCompatActivity {

    private ActivityPerfilBinding binding;
    private PerfilViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();
        setupObservers();
        
        viewModel.loadUserProfile();
    }

    private void setupListeners() {
        binding.btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, iniciar_sesion.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupObservers() {
        viewModel.getUserProfile().observe(this, this::updateUI);

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                ViewUtils.showSnackbar(this, error, ViewUtils.MsgType.ERROR);
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) return;

        binding.tvUserName.setText(user.getNombre() + " " + user.getApellido());
        
        String initials = "";
        if (user.getNombre() != null && !user.getNombre().isEmpty()) initials += user.getNombre().charAt(0);
        if (user.getApellido() != null && !user.getApellido().isEmpty()) initials += user.getApellido().charAt(0);
        binding.tvUserInitials.setText(initials.toUpperCase());

        binding.tvUserLocation.setText(user.getDireccion());
        binding.tvLocationAddress.setText(user.getDireccion());
        binding.tvLocationRadio.setText(user.getRadioBusqueda() + " km");

        // sectores
        binding.cgSectores.removeAllViews();
        if (user.getSectores() != null) {
            for (String sector : user.getSectores()) {
                Chip chip = new Chip(this);
                chip.setText(sector);
                chip.setChipBackgroundColorResource(android.R.color.transparent);
                chip.setChipStrokeWidth(1f);
                chip.setChipStrokeColorResource(R.color.login_blue_text);
                chip.setTextColor(ContextCompat.getColor(this, R.color.login_blue_text));
                binding.cgSectores.addView(chip);
            }
        }
    }
}
