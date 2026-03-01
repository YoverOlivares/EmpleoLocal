package unc.edu.pe.empleolocal;

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

import unc.edu.pe.empleolocal.databinding.ActivityDetalleOfertaBinding;
import unc.edu.pe.empleolocal.databinding.ActivityEditarPerfilBinding;
import unc.edu.pe.empleolocal.databinding.ActivityFiltrarOfertasBinding;
import unc.edu.pe.empleolocal.databinding.ActivityInicioBinding;
import unc.edu.pe.empleolocal.databinding.ActivityMainBinding;
import unc.edu.pe.empleolocal.databinding.ActivityMapaEmpleosBinding;
import unc.edu.pe.empleolocal.databinding.ActivityNotificacionesBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPerfilBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPerfilEmpresaBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPostulacionesBinding;
import unc.edu.pe.empleolocal.databinding.ActivitySeguimientoPostulacionBinding;
import unc.edu.pe.empleolocal.databinding.ItemOfertaBinding;
import unc.edu.pe.empleolocal.databinding.LayoutLocationSelectionBinding;

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
        private ActivityInicioBinding binding;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            binding = ActivityInicioBinding.inflate(i, c, false);
            
            // Botón "Cambiar" ubicación
            binding.tvChange.setOnClickListener(v -> {
                showLocationDialog();
            });

            binding.rvInicioOfertas.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvInicioOfertas.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            return binding.getRoot();
        }

        private void showLocationDialog() {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            LayoutLocationSelectionBinding dialogBinding = LayoutLocationSelectionBinding.inflate(LayoutInflater.from(getContext()));
            dialog.setContentView(dialogBinding.getRoot());
            
            // Botón Aplicar dentro de la tarjeta
            dialogBinding.btnApplyLocation.setOnClickListener(v -> {
                dialog.dismiss();
            });
            
            dialog.show();
        }

        @Override public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }

    public static class MapaFragment extends Fragment {
        private ActivityMapaEmpleosBinding binding;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            binding = ActivityMapaEmpleosBinding.inflate(i, c, false);
            View bottomSheet = binding.bottomSheet;
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            binding.viewListBtnContainer.setOnClickListener(v -> {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
            
            if (binding.listContainerRv != null) {
                binding.listContainerRv.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.listContainerRv.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            }
            binding.cardSelectedJob.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openJobDetail();
            });
            binding.fabMyLocation.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            binding.fabFilter.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            return binding.getRoot();
        }

        @Override public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }

    public static class PostulacionesFragment extends Fragment {
        private ActivityPostulacionesBinding binding;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            binding = ActivityPostulacionesBinding.inflate(i, c, false);
            
            // Usar camelCase directamente sobre el binding del include
            binding.itemRevision.btnVerDetalle.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            binding.itemEntrevista.btnVerDetalleEntrevista.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            binding.itemRechazada.btnVerDetalleRechazada.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            return binding.getRoot();
        }

        @Override public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }

    public static class PerfilFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivityPerfilBinding.inflate(i, c, false).getRoot();
        }
    }

    public static class EditarPerfilFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivityEditarPerfilBinding.inflate(i, c, false).getRoot();
        }
    }

    public static class NotificacionesFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivityNotificacionesBinding.inflate(i, c, false).getRoot();
        }
    }

    public static class DetalleOfertaFragment extends Fragment {
        private ActivityDetalleOfertaBinding binding;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            binding = ActivityDetalleOfertaBinding.inflate(i, c, false);
            binding.cvCompanyHeader.setOnClickListener(v -> {
                ((MainActivity) getActivity()).openCompanyProfile();
            });
            return binding.getRoot();
        }

        @Override public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }

    public static class PerfilEmpresaFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivityPerfilEmpresaBinding.inflate(i, c, false).getRoot();
        }
    }

    public static class SeguimientoFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivitySeguimientoPostulacionBinding.inflate(i, c, false).getRoot();
        }
    }

    public static class FiltrarOfertasFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            return ActivityFiltrarOfertasBinding.inflate(i, c, false).getRoot();
        }
    }

    // --- ADAPTER SIMPLE PARA PRUEBAS ---
    public static class SimpleOfertaAdapter extends RecyclerView.Adapter<SimpleOfertaAdapter.ViewHolder> {
        private MainActivity activity;
        public SimpleOfertaAdapter(MainActivity activity) { this.activity = activity; }

        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            ItemOfertaBinding binding = ItemOfertaBinding.inflate(LayoutInflater.from(p.getContext()), p, false);
            return new ViewHolder(binding);
        }

        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            if (pos == 0) {
                h.binding.tvJobTitle.setText("Técnico de Operaciones Mineras");
                h.binding.tvCompanyName.setText("Yanacocha");
                h.binding.tvDistance.setText("1.2 km");
                h.binding.tvAddress.setText("Jr. del Comercio");
                h.binding.tvSalary.setText("S/. 3,500/mes");
            } else if (pos == 1) {
                h.binding.tvJobTitle.setText("Enfermero/a Asistencial");
                h.binding.tvCompanyName.setText("Hospital Regional Cajamarca");
                h.binding.tvDistance.setText("0.8 km");
                h.binding.tvAddress.setText("Av. Atahualpa");
                h.binding.tvSalary.setText("S/. 2,100/mes");
            } else {
                h.binding.tvJobTitle.setText("Puesto de Trabajo " + (pos + 1));
                h.binding.tvCompanyName.setText("Empresa Local SAC");
                h.binding.tvDistance.setText((1 + pos) + ".0 km");
                h.binding.tvAddress.setText("Centro de Cajamarca");
                h.binding.tvSalary.setText("S/. 2,500/mes");
            }
            h.itemView.setOnClickListener(v -> activity.openJobDetail());
        }

        @Override public int getItemCount() { return 10; }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            final ItemOfertaBinding binding;
            public ViewHolder(@NonNull ItemOfertaBinding binding) { 
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
