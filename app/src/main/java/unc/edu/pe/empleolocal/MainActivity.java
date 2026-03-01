package unc.edu.pe.empleolocal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class MainActivity extends AppCompatActivity {

    private TextView tvToolbarTitle;
    private View flNotifications;
    private View ivEditProfile;
    private View ivBackButton;
    private View tvSaveButton;
    private View tvMarkReadButton;
    private View ivBookmark;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        flNotifications = findViewById(R.id.fl_notifications);
        ivEditProfile = findViewById(R.id.iv_edit_profile);
        ivBackButton = findViewById(R.id.iv_back_button);
        tvSaveButton = findViewById(R.id.tv_save_button);
        tvMarkReadButton = findViewById(R.id.tv_mark_read_button);
        ivBookmark = findViewById(R.id.iv_bookmark);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Manejo de Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar navegación
        bottomNavigation.setOnItemSelectedListener(item -> {
            bottomNavigation.getMenu().setGroupCheckable(0, true, true);
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
        flNotifications.setOnClickListener(v -> {
            updateToolbar("Notificaciones", false, false, true, false, true, false);
            deselectBottomNav();
            loadFragment(new NotificacionesFragment());
        });

        // Clic en el lápiz para editar perfil
        ivEditProfile.setOnClickListener(v -> {
            updateToolbar("Editar Perfil", false, false, true, true, false, false);
            deselectBottomNav();
            loadFragment(new EditarPerfilFragment());
        });

        // Clic en botón atrás
        ivBackButton.setOnClickListener(v -> {
            bottomNavigation.getMenu().setGroupCheckable(0, true, true);
            bottomNavigation.setSelectedItemId(bottomNavigation.getSelectedItemId());
        });

        // Cargar pantalla de inicio por defecto
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
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
        bottomNavigation.getMenu().setGroupCheckable(0, false, true);
        for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
            bottomNavigation.getMenu().getItem(i).setChecked(false);
        }
    }

    private void updateToolbar(String title, boolean showNotif, boolean showEdit, boolean showBack, boolean showSave, boolean showMarkRead, boolean showBookmark) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(title);
        if (flNotifications != null) flNotifications.setVisibility(showNotif ? View.VISIBLE : View.GONE);
        if (ivEditProfile != null) ivEditProfile.setVisibility(showEdit ? View.VISIBLE : View.GONE);
        if (ivBackButton != null) ivBackButton.setVisibility(showBack ? View.VISIBLE : View.GONE);
        if (tvSaveButton != null) tvSaveButton.setVisibility(showSave ? View.VISIBLE : View.GONE);
        if (tvMarkReadButton != null) tvMarkReadButton.setVisibility(showMarkRead ? View.VISIBLE : View.GONE);
        if (ivBookmark != null) ivBookmark.setVisibility(showBookmark ? View.VISIBLE : View.GONE);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // --- FRAGMENTS ---

    public static class InicioFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            View view = i.inflate(R.layout.activity_inicio, c, false);
            
            // Botón "Cambiar" ubicación
            view.findViewById(R.id.tv_change).setOnClickListener(v -> {
                showLocationDialog();
            });

            RecyclerView rv = view.findViewById(R.id.rv_inicio_ofertas);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
            rv.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            return view;
        }

        private void showLocationDialog() {
            BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.layout_location_selection, null);
            dialog.setContentView(dialogView);
            
            // Botón Aplicar dentro de la tarjeta
            dialogView.findViewById(R.id.btn_apply_location).setOnClickListener(v -> {
                dialog.dismiss();
            });
            
            dialog.show();
        }
    }

    public static class MapaFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            View view = i.inflate(R.layout.activity_mapa_empleos, c, false);
            View bottomSheet = view.findViewById(R.id.bottom_sheet);
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
            view.findViewById(R.id.view_list_btn_container).setOnClickListener(v -> {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
            RecyclerView rv = view.findViewById(R.id.list_container_rv);
            if (rv != null) {
                rv.setLayoutManager(new LinearLayoutManager(getContext()));
                rv.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            }
            view.findViewById(R.id.card_selected_job).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openJobDetail();
            });
            view.findViewById(R.id.fab_my_location).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            view.findViewById(R.id.fab_filter).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openFilters();
            });
            return view;
        }
    }

    public static class PostulacionesFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            View view = i.inflate(R.layout.activity_postulaciones, c, false);
            view.findViewById(R.id.item_revision).findViewById(R.id.btn_ver_detalle).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            view.findViewById(R.id.item_entrevista).findViewById(R.id.btn_ver_detalle_entrevista).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            view.findViewById(R.id.item_rechazada).findViewById(R.id.btn_ver_detalle_rechazada).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openTracking();
            });
            return view;
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
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            View view = i.inflate(R.layout.activity_detalle_oferta, i.inflate(R.layout.activity_detalle_oferta, c, false) instanceof ViewGroup ? (ViewGroup) i.inflate(R.layout.activity_detalle_oferta, c, false) : c, false);
            view.findViewById(R.id.cv_company_header).setOnClickListener(v -> {
                ((MainActivity) getActivity()).openCompanyProfile();
            });
            return view;
        }
        @Override public View getView() { return super.getView(); }
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
            return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.item_oferta, p, false));
        }

        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            if (pos == 0) {
                h.tvJobTitle.setText("Técnico de Operaciones Mineras");
                h.tvCompanyName.setText("Yanacocha");
                h.tvDistance.setText("1.2 km");
                h.tvAddress.setText("Jr. del Comercio");
                h.tvSalary.setText("S/. 3,500/mes");
            } else if (pos == 1) {
                h.tvJobTitle.setText("Enfermero/a Asistencial");
                h.tvCompanyName.setText("Hospital Regional Cajamarca");
                h.tvDistance.setText("0.8 km");
                h.tvAddress.setText("Av. Atahualpa");
                h.tvSalary.setText("S/. 2,100/mes");
            } else {
                h.tvJobTitle.setText("Puesto de Trabajo " + (pos + 1));
                h.tvCompanyName.setText("Empresa Local SAC");
                h.tvDistance.setText((1 + pos) + ".0 km");
                h.tvAddress.setText("Centro de Cajamarca");
                h.tvSalary.setText("S/. 2,500/mes");
            }
            h.itemView.setOnClickListener(v -> activity.openJobDetail());
        }

        @Override public int getItemCount() { return 10; }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvJobTitle, tvCompanyName, tvDistance, tvAddress, tvSalary;
            public ViewHolder(@NonNull View itemView) { 
                super(itemView);
                tvJobTitle = itemView.findViewById(R.id.tv_job_title);
                tvCompanyName = itemView.findViewById(R.id.tv_company_name);
                tvDistance = itemView.findViewById(R.id.tv_distance);
                tvAddress = itemView.findViewById(R.id.tv_address);
                tvSalary = itemView.findViewById(R.id.tv_salary);
            }
        }
    }
}