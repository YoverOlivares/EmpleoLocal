package unc.edu.pe.empleolocal.ui.detalle;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import unc.edu.pe.empleolocal.databinding.ActivityDetalleOfertaBinding;
import unc.edu.pe.empleolocal.utils.ViewUtils;

public class DetalleOfertaActivity extends AppCompatActivity {

    private ActivityDetalleOfertaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetalleOfertaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupListeners();
    }

    private void setupListeners() {
        binding.cvCompanyHeader.setOnClickListener(v -> {
            // Lógica para abrir perfil de empresa
            ViewUtils.showSnackbar(this, "Cargando perfil de empresa...", ViewUtils.MsgType.INFO);
        });

        binding.btnAplicarDetalle.setOnClickListener(v -> {
            // Lógica de postulación
            ViewUtils.showSnackbar(this, "¡Postulación enviada con éxito!", ViewUtils.MsgType.SUCCESS);
        });
    }
}
