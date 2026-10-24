package unc.edu.pe.empleolocal.ui.lista;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import unc.edu.pe.empleolocal.databinding.ActivityListaOfertasBinding;

public class ListaOfertasActivity extends AppCompatActivity {

    private ActivityListaOfertasBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityListaOfertasBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.rvOfertas.setLayoutManager(new LinearLayoutManager(this));
        // Aquí se conectaría el adaptador real de ofertas
    }
}
