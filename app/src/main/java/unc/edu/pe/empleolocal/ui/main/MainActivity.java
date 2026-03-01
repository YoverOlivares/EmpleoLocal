package unc.edu.pe.empleolocal.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import unc.edu.pe.empleolocal.R;
import unc.edu.pe.empleolocal.databinding.ActivityMainBinding;
import unc.edu.pe.empleolocal.databinding.ActivityInicioBinding;
import unc.edu.pe.empleolocal.databinding.ActivityMapaEmpleosBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPostulacionesBinding;
import unc.edu.pe.empleolocal.databinding.ActivityDetalleOfertaBinding;
import unc.edu.pe.empleolocal.databinding.ItemOfertaBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Manejo de Insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar navegación
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            binding.bottomNavigation.getMenu().setGroupCheckable(0, true, true);
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                updateToolbar("EmpleoLocal", true, false, false, false, false, false);
                loadFragment(new InicioFragment());
                return true;
            } else if (id == R.id.nav_map) {
                updateToolbar("Mapa de Empleos", true, false, false, false, false, false);
                loadFragment(new MapaFragment());
                return true;
            } else if (id == R.id.nav_apply) {
                updateToolbar("Mis Postulaciones", true, false, false, false, false, false);
                loadFragment(new PostulacionesFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                updateToolbar("Mi Perfil", false, true, false, false, false, false);
                loadFragment(new PerfilFragment());
                return true;
            }
            return false;
        });

        // Clic en la campana de notificaciones
        binding.flNotifications.setOnClickListener(v -> {
            updateToolbar("Notificaciones", false, false, true, false, true, false);
            deselectBottomNav();
            loadFragment(new NotificacionesFragment());
        });

        // Clic en el lápiz para editar perfil
        binding.ivEditProfile.setOnClickListener(v -> {
            updateToolbar("Editar Perfil", false, false, true, true, false, false);
            deselectBottomNav();
            loadFragment(new EditarPerfilFragment());
        });

        // Clic en botón atrás
        binding.ivBackButton.setOnClickListener(v -> {
            binding.bottomNavigation.getMenu().setGroupCheckable(0, true, true);
            binding.bottomNavigation.setSelectedItemId(binding.bottomNavigation.getSelectedItemId());
        });

        // Cargar pantalla de inicio por defecto
        if (savedInstanceState == null) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    public void openJobDetail() {
        updateToolbar("Detalle de Oferta", false, false, true, false, false, true);
        deselectBottomNav();
        loadFragment(new DetalleOfertaFragment());
    }

    public void openCompanyProfile() {
        updateToolbar("Perfil de Empresa", false, false, true, false, false, false);
        loadFragment(new PerfilEmpresaFragment());
    }

    public void openTracking() {
        updateToolbar("Seguimiento", false, false, true, false, false, false);
        deselectBottomNav();
        loadFragment(new SeguimientoFragment());
    }

    public void openFilters() {
        updateToolbar("Filtrar Ofertas", false, false, true, false, false, false);
        deselectBottomNav();
        loadFragment(new FiltrarOfertasFragment());
    }

    private void deselectBottomNav() {
        binding.bottomNavigation.getMenu().setGroupCheckable(0, false, true);
        for (int i = 0; i < binding.bottomNavigation.getMenu().size(); i++) {
            binding.bottomNavigation.getMenu().getItem(i).setChecked(false);
        }
    }

    private void updateToolbar(String title, boolean showNotif, boolean showEdit, boolean showBack, boolean showSave, boolean showMarkRead, boolean showBookmark) {
        binding.tvToolbarTitle.setText(title);
        binding.flNotifications.setVisibility(showNotif ? View.VISIBLE : View.GONE);
        binding.ivEditProfile.setVisibility(showEdit ? View.VISIBLE : View.GONE);
        binding.ivBackButton.setVisibility(showBack ? View.VISIBLE : View.GONE);
        binding.tvSaveButton.setVisibility(showSave ? View.VISIBLE : View.GONE);
        binding.tvMarkReadButton.setVisibility(showMarkRead ? View.VISIBLE : View.GONE);
        binding.ivBookmark.setVisibility(showBookmark ? View.VISIBLE : View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // --- FRAGMENTS ---

    public static class InicioFragment extends Fragment {
        private ActivityInicioBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityInicioBinding.inflate(i, c, false);
            
            b.tvChange.setOnClickListener(v -> showLocationDialog());

            b.rvInicioOfertas.setLayoutManager(new LinearLayoutManager(getContext()));
            b.rvInicioOfertas.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            return b.getRoot();
        }

        private void showLocationDialog() {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_location_selection, null);
            dialog.setContentView(dialogView);
            dialog.show();
        }
    }

    public static class MapaFragment extends Fragment {
        private ActivityMapaEmpleosBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityMapaEmpleosBinding.inflate(i, c, false);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(b.bottomSheet);
            b.viewListBtnContainer.setOnClickListener(v -> {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
            if (b.listContainerRv != null) {
                b.listContainerRv.setLayoutManager(new LinearLayoutManager(getContext()));
                b.listContainerRv.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            }
            b.cardSelectedJob.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openJobDetail();
            });
            b.fabMyLocation.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            b.fabFilter.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            return b.getRoot();
        }
    }

    public static class PostulacionesFragment extends Fragment {
        private ActivityPostulacionesBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityPostulacionesBinding.inflate(i, c, false);
            
            // Acceso mediante binding a los elementos de los layouts incluidos
            b.itemRevision.btnVerDetalle.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            
            b.itemEntrevista.btnVerDetalleEntrevista.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            
            b.itemRechazada.btnVerDetalleRechazada.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });

            return b.getRoot();
        }
    }

    public static class PerfilFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_perfil, c, false);
        }
    }

    public static class EditarPerfilFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_editar_perfil, c, false);
        }
    }

    public static class NotificacionesFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_notificaciones, c, false);
        }
    }

    public static class DetalleOfertaFragment extends Fragment {
        private ActivityDetalleOfertaBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityDetalleOfertaBinding.inflate(i, c, false);
            b.cvCompanyHeader.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openCompanyProfile();
            });
            return b.getRoot();
        }
    }

    public static class PerfilEmpresaFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_perfil_empresa, c, false);
        }
    }

    public static class SeguimientoFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_seguimiento_postulacion, c, false);
        }
    }

    public static class FiltrarOfertasFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return i.inflate(R.layout.activity_filtrar_ofertas, c, false);
        }
    }

    // --- ADAPTER SIMPLE PARA PRUEBAS ---
    public static class SimpleOfertaAdapter extends RecyclerView.Adapter<SimpleOfertaAdapter.ViewHolder> {
        private MainActivity activity;
        public SimpleOfertaAdapter(MainActivity activity) { this.activity = activity; }

        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(ItemOfertaBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
        }

        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            if (pos == 0) {
                h.b.tvJobTitle.setText("Técnico de Operaciones Mineras");
                h.b.tvCompanyName.setText("Yanacocha");
                h.b.tvDistance.setText("1.2 km");
                h.b.tvAddress.setText("Jr. del Comercio");
                h.b.tvSalary.setText("S/. 3,500/mes");
            } else if (pos == 1) {
                h.b.tvJobTitle.setText("Enfermero/a Asistencial");
                h.b.tvCompanyName.setText("Hospital Regional Cajamarca");
                h.b.tvDistance.setText("0.8 km");
                h.b.tvAddress.setText("Av. Atahualpa");
                h.b.tvSalary.setText("S/. 2,100/mes");
            } else {
                h.b.tvJobTitle.setText("Puesto de Trabajo " + (pos + 1));
                h.b.tvCompanyName.setText("Empresa Local SAC");
                h.b.tvDistance.setText((1 + pos) + ".0 km");
                h.b.tvAddress.setText("Centro de Cajamarca");
                h.b.tvSalary.setText("S/. 2,500/mes");
            }
            h.itemView.setOnClickListener(v -> activity.openJobDetail());
        }

        @Override public int getItemCount() { return 10; }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ItemOfertaBinding b;
            public ViewHolder(ItemOfertaBinding binding) { 
                super(binding.getRoot());
                this.b = binding;
            }
        }
    }
}
