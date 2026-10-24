package unc.edu.pe.empleolocal.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import unc.edu.pe.empleolocal.R;
import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.databinding.ActivityFiltrarOfertasBinding;
import unc.edu.pe.empleolocal.databinding.ActivityMainBinding;
import unc.edu.pe.empleolocal.databinding.ActivityInicioBinding;
import unc.edu.pe.empleolocal.databinding.ActivityMapaEmpleosBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPostulacionesBinding;
import unc.edu.pe.empleolocal.databinding.ActivityDetalleOfertaBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPerfilBinding;
import unc.edu.pe.empleolocal.databinding.ActivityEditarPerfilBinding;
import unc.edu.pe.empleolocal.databinding.ActivityNotificacionesBinding;
import unc.edu.pe.empleolocal.databinding.ActivitySeguimientoPostulacionBinding;
import unc.edu.pe.empleolocal.databinding.ActivityPerfilEmpresaBinding;
import unc.edu.pe.empleolocal.databinding.ItemOfertaBinding;
import unc.edu.pe.empleolocal.ui.auth.AuthViewModel;
import unc.edu.pe.empleolocal.ui.auth.iniciar_sesion;
import unc.edu.pe.empleolocal.ui.perfil.PerfilViewModel;
import unc.edu.pe.empleolocal.utils.ViewUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    
    private final InicioFragment inicioFragment = new InicioFragment();
    private final MapaFragment mapaFragment = new MapaFragment();
    private final PostulacionesFragment postulacionesFragment = new PostulacionesFragment();
    private final PerfilFragment perfilFragment = new PerfilFragment();
    private Fragment activeFragment = inicioFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupNavigation();
        setupToolbarActions();
        handleBackPress();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, perfilFragment, "4").hide(perfilFragment)
                    .add(R.id.fragment_container, postulacionesFragment, "3").hide(postulacionesFragment)
                    .add(R.id.fragment_container, mapaFragment, "2").hide(mapaFragment)
                    .add(R.id.fragment_container, inicioFragment, "1")
                    .commit();
            updateToolbar("EmpleoLocal", true, false, false, false, false, false);
        }
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                updateToolbar("EmpleoLocal", true, false, false, false, false, false);
                showFragment(inicioFragment);
                return true;
            } else if (id == R.id.nav_map) {
                updateToolbar("Mapa de Empleos", true, false, false, false, false, false);
                showFragment(mapaFragment);
                return true;
            } else if (id == R.id.nav_apply) {
                updateToolbar("Mis Postulaciones", true, false, false, false, false, false);
                showFragment(postulacionesFragment);
                return true;
            } else if (id == R.id.nav_profile) {
                updateToolbar("Mi Perfil", false, true, false, false, false, false);
                showFragment(perfilFragment);
                return true;
            }
            return false;
        });
    }

    private void setupToolbarActions() {
        binding.ivBackButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        
        binding.flNotifications.setOnClickListener(v -> {
            updateToolbar("Notificaciones", false, false, true, false, true, false);
            loadSubFragment(new NotificacionesFragment());
        });

        binding.ivEditProfile.setOnClickListener(v -> {
            updateToolbar("Editar Perfil", false, false, true, false, false, false);
            loadSubFragment(new EditarPerfilFragment());
        });
    }

    private void handleBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    syncUIAfterBack();
                } else {
                    finish();
                }
            }
        });
    }

    private void showFragment(Fragment fragment) {
        if (fragment == activeFragment) return;
        getSupportFragmentManager().beginTransaction().hide(activeFragment).show(fragment).commit();
        activeFragment = fragment;
    }

    private void loadSubFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .hide(activeFragment)
                .addToBackStack(null)
                .commit();
        activeFragment = fragment;
    }

    public void syncUIAfterBack() {
        binding.getRoot().postDelayed(() -> {
            Fragment current = null;
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment f : fragments) {
                if (f != null && f.isVisible()) {
                    current = f;
                    break;
                }
            }

            if (current instanceof InicioFragment) {
                updateToolbar("EmpleoLocal", true, false, false, false, false, false);
                binding.bottomNavigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                activeFragment = inicioFragment;
            } else if (current instanceof MapaFragment) {
                updateToolbar("Mapa de Empleos", true, false, false, false, false, false);
                binding.bottomNavigation.getMenu().findItem(R.id.nav_map).setChecked(true);
                activeFragment = mapaFragment;
            } else if (current instanceof PostulacionesFragment) {
                updateToolbar("Mis Postulaciones", true, false, false, false, false, false);
                binding.bottomNavigation.getMenu().findItem(R.id.nav_apply).setChecked(true);
                activeFragment = postulacionesFragment;
            } else if (current instanceof PerfilFragment) {
                updateToolbar("Mi Perfil", false, true, false, false, false, false);
                binding.bottomNavigation.getMenu().findItem(R.id.nav_profile).setChecked(true);
                activeFragment = perfilFragment;
            }
        }, 100);
    }

    public void openJobDetail() {
        updateToolbar("Detalle de Oferta", false, false, true, false, false, true);
        loadSubFragment(new DetalleOfertaFragment());
    }

    public void openCompanyProfile() {
        updateToolbar("Perfil de Empresa", false, false, true, false, false, false);
        loadSubFragment(new PerfilEmpresaFragment());
    }

    public void openTracking() {
        updateToolbar("Seguimiento", false, false, true, false, false, false);
        loadSubFragment(new SeguimientoFragment());
    }

    public void openFilters() {
        updateToolbar("Filtrar Ofertas", false, false, true, false, false, false);
        loadSubFragment(new FiltrarOfertasFragment());
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

    // --- FRAGMENTS ---

    public static class InicioFragment extends Fragment {
        private ActivityInicioBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            if (b != null) return b.getRoot();
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

    public static class MapaFragment extends Fragment implements OnMapReadyCallback {
        private ActivityMapaEmpleosBinding b;
        private GoogleMap mMap;
        private PerfilViewModel viewModel;
        private Circle mCircle;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            if (b != null) return b.getRoot();
            b = ActivityMapaEmpleosBinding.inflate(i, c, false);
            viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);

            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(b.bottomSheet);
            b.viewListBtnContainer.setOnClickListener(v -> behavior.setState(BottomSheetBehavior.STATE_EXPANDED));
            if (b.listContainerRv != null) {
                b.listContainerRv.setLayoutManager(new LinearLayoutManager(getContext()));
                b.listContainerRv.setAdapter(new SimpleOfertaAdapter((MainActivity) getActivity()));
            }
            b.cardSelectedJob.setOnClickListener(v -> ((MainActivity) getActivity()).openJobDetail());
            
            b.fabMyLocation.setOnClickListener(v -> centerMapOnUser());
            b.fabFilter.setOnClickListener(v -> ((MainActivity) getActivity()).openFilters());
            
            return b.getRoot();
        }

        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mMap = googleMap;
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
            setupObservers();
            viewModel.loadUserProfile();
        }

        private void setupObservers() {
            viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
                if (user != null && mMap != null) {
                    LatLng userLatLng = new LatLng(user.getLatitud(), user.getLongitud());
                    drawSearchCircle(userLatLng, user.getRadioBusqueda());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 12));
                }
            });
        }

        private void drawSearchCircle(LatLng center, int radiusKm) {
            if (mCircle != null) mCircle.remove();
            mCircle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radiusKm * 1000)
                    .strokeWidth(2)
                    .strokeColor(Color.parseColor("#441F89E5"))
                    .fillColor(Color.parseColor("#221F89E5")));
        }

        private void centerMapOnUser() {
            User user = viewModel.getUserProfile().getValue();
            if (user != null && mMap != null) {
                LatLng userLatLng = new LatLng(user.getLatitud(), user.getLongitud());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
            }
        }
    }

    public static class PostulacionesFragment extends Fragment {
        private ActivityPostulacionesBinding b;
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            if (b != null) return b.getRoot();
            b = ActivityPostulacionesBinding.inflate(i, c, false);
            b.itemRevision.btnVerDetalle.setOnClickListener(v -> ((MainActivity) getActivity()).openTracking());
            b.itemEntrevista.btnVerDetalleEntrevista.setOnClickListener(v -> ((MainActivity) getActivity()).openTracking());
            b.itemRechazada.btnVerDetalleRechazada.setOnClickListener(v -> ((MainActivity) getActivity()).openTracking());
            return b.getRoot();
        }
    }

    public static class PerfilFragment extends Fragment {
        private ActivityPerfilBinding b;
        private PerfilViewModel viewModel;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            if (b != null) return b.getRoot();
            b = ActivityPerfilBinding.inflate(i, c, false);
            viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
            
            setupObservers();
            setupInitialStats();
            setupNotificationSwitch();
            viewModel.loadUserProfile();

            b.btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), iniciar_sesion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            });

            return b.getRoot();
        }

        private void setupNotificationSwitch() {
            b.swNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                            b.swNotifications.setChecked(false);
                        }
                    }
                }
            });
        }

        private void setupInitialStats() {
            b.statPostulaciones.tvStatLabel.setText("POSTULACIONES");
            b.statPostulaciones.tvStatCount.setText("0");
            b.statEntrevistas.tvStatLabel.setText("ENTREVISTAS");
            b.statEntrevistas.tvStatCount.setText("0");
            b.statGuardados.tvStatLabel.setText("GUARDADOS");
            b.statGuardados.tvStatCount.setText("0");
        }

        private void setupObservers() {
            viewModel.getUserProfile().observe(getViewLifecycleOwner(), this::updateUI);
            viewModel.getError().observe(getViewLifecycleOwner(), error -> {
                if (error != null) ViewUtils.showSnackbar(getActivity(), error, ViewUtils.MsgType.ERROR);
            });
        }

        private void updateUI(User user) {
            if (user == null || b == null) return;
            b.tvUserName.setText(user.getNombre() + " " + user.getApellido());
            String initials = "";
            if (user.getNombre() != null && !user.getNombre().isEmpty()) initials += user.getNombre().charAt(0);
            if (user.getApellido() != null && !user.getApellido().isEmpty()) initials += user.getApellido().charAt(0);
            b.tvUserInitials.setText(initials.toUpperCase());
            b.tvUserLocation.setText(user.getDireccion());
            b.tvLocationAddress.setText(user.getDireccion());
            b.tvLocationRadio.setText(user.getRadioBusqueda() + " km");

            b.cgSectores.removeAllViews();
            if (user.getSectores() != null) {
                for (String sector : user.getSectores()) {
                    Chip chip = new Chip(getContext());
                    chip.setText(sector);
                    chip.setChipBackgroundColorResource(android.R.color.transparent);
                    chip.setChipStrokeWidth(1f);
                    chip.setChipStrokeColorResource(R.color.login_blue_text);
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.login_blue_text));
                    b.cgSectores.addView(chip);
                }
            }
        }
    }

    public static class FiltrarOfertasFragment extends Fragment {
        private ActivityFiltrarOfertasBinding b;
        private PerfilViewModel viewModel;
        private AuthViewModel authViewModel;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityFiltrarOfertasBinding.inflate(i, c, false);
            viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
            authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

            viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
                if (user != null && user.getDireccion() != null) {
                    String city = user.getDireccion().split(",")[0];
                    b.tvSearchRadiusTitle.setText("Radio de búsqueda en " + city);
                }
            });
            viewModel.loadUserProfile();

            // CARGA DINÁMICA DE SECTORES EN FILTROS
            authViewModel.getSectorsLiveData().observe(getViewLifecycleOwner(), sectors -> {
                b.cgSectors.removeAllViews();
                for (String sector : sectors) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(sector);
                    chip.setCheckable(true);
                    chip.setChipBackgroundColorResource(R.color.selector_chip_background);
                    chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.selector_chip_text));
                    b.cgSectors.addView(chip);
                }
            });
            authViewModel.fetchSectors();

            b.sliderRadius.addOnChangeListener((slider, value, fromUser) -> b.tvRadiusVal.setText((int)value + " km"));
            b.sliderSalary.addOnChangeListener((slider, value, fromUser) -> {
                List<Float> values = slider.getValues();
                b.tvMinSalary.setText("S/. " + String.format(Locale.getDefault(), "%,.0f", values.get(0)));
                b.tvMaxSalary.setText("S/. " + String.format(Locale.getDefault(), "%,.0f", values.get(1)));
            });
            b.btnApply.setOnClickListener(v -> getActivity().getOnBackPressedDispatcher().onBackPressed());
            b.btnClearFilters.setOnClickListener(v -> {
                b.sliderRadius.setValue(5);
                b.sliderSalary.setValues(1025f, 10000f);
            });
            return b.getRoot();
        }
    }

    public static class EditarPerfilFragment extends Fragment {
        private ActivityEditarPerfilBinding b;
        private PerfilViewModel viewModel;
        private AuthViewModel authViewModel;

        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            b = ActivityEditarPerfilBinding.inflate(i, c, false);
            viewModel = new ViewModelProvider(requireActivity()).get(PerfilViewModel.class);
            authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

            setupObservers();
            viewModel.loadUserProfile();
            authViewModel.fetchSectors();

            b.btnSaveChanges.setOnClickListener(v -> saveChanges());
            b.btnUploadCv.setOnClickListener(v -> ViewUtils.showSnackbar(getActivity(), "Funcionalidad de CV próximamente", ViewUtils.MsgType.INFO));

            return b.getRoot();
        }

        private void setupObservers() {
            viewModel.getUserProfile().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    b.etNombre.setText(user.getNombre());
                    b.etApellido.setText(user.getApellido());
                    b.etTelefono.setText(user.getTelefono());
                    b.etCorreo.setText(user.getCorreo());
                    
                    String initials = "";
                    if (user.getNombre() != null && !user.getNombre().isEmpty()) initials += user.getNombre().charAt(0);
                    if (user.getApellido() != null && !user.getApellido().isEmpty()) initials += user.getApellido().charAt(0);
                    b.tvUserInitials.setText(initials.toUpperCase());

                    if (user.getCvUrl() == null || user.getCvUrl().isEmpty()) {
                        b.tvCvName.setText("No se ha subido ningún CV");
                    } else {
                        b.tvCvName.setText("CV Cargado");
                    }
                    
                    // Actualizar marcado de sectores si ya cargaron
                    if (authViewModel.getSectorsLiveData().getValue() != null) {
                        markUserSectors(authViewModel.getSectorsLiveData().getValue(), user.getSectores());
                    }
                }
            });

            authViewModel.getSectorsLiveData().observe(getViewLifecycleOwner(), sectors -> {
                List<String> userSectors = viewModel.getUserProfile().getValue() != null ? 
                        viewModel.getUserProfile().getValue().getSectores() : new ArrayList<>();
                markUserSectors(sectors, userSectors);
            });

            viewModel.getUpdateSuccess().observe(getViewLifecycleOwner(), success -> {
                if (success != null && success) {
                    ViewUtils.showSnackbar(getActivity(), "Perfil actualizado con éxito", ViewUtils.MsgType.SUCCESS);
                    viewModel.resetUpdateStatus();
                    // Regresar al perfil inmediatamente
                    getActivity().getSupportFragmentManager().popBackStack();
                    // Forzar sincronización de UI
                    ((MainActivity)getActivity()).syncUIAfterBack();
                }
            });
        }

        private void markUserSectors(List<String> allSectors, List<String> selectedSectors) {
            b.cgSectores.removeAllViews();
            for (String sector : allSectors) {
                Chip chip = new Chip(requireContext());
                chip.setText(sector);
                chip.setCheckable(true);
                chip.setChipBackgroundColorResource(R.color.selector_chip_background);
                chip.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.selector_chip_text));
                
                if (selectedSectors != null && selectedSectors.contains(sector)) {
                    chip.setChecked(true);
                }
                
                b.cgSectores.addView(chip);
            }
        }

        private void saveChanges() {
            String nombre = b.etNombre.getText().toString().trim();
            String apellido = b.etApellido.getText().toString().trim();
            String telefono = b.etTelefono.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
                ViewUtils.showSnackbar(getActivity(), "Complete todos los campos obligatorios", ViewUtils.MsgType.WARNING);
                return;
            }

            if (telefono.length() != 9 || !telefono.startsWith("9")) {
                ViewUtils.showSnackbar(getActivity(), "Teléfono inválido (9 dígitos e iniciar con 9)", ViewUtils.MsgType.WARNING);
                return;
            }

            List<String> selectedSectores = new ArrayList<>();
            for (int i = 0; i < b.cgSectores.getChildCount(); i++) {
                Chip chip = (Chip) b.cgSectores.getChildAt(i);
                if (chip.isChecked()) {
                    selectedSectores.add(chip.getText().toString());
                }
            }

            viewModel.updateProfile(nombre, apellido, telefono, selectedSectores);
        }
    }

    public static class NotificacionesFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            ActivityNotificacionesBinding b = ActivityNotificacionesBinding.inflate(i, c, false);
            return b.getRoot();
        }
    }

    public static class DetalleOfertaFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            ActivityDetalleOfertaBinding b = ActivityDetalleOfertaBinding.inflate(i, c, false);
            b.cvCompanyHeader.setOnClickListener(v -> ((MainActivity) getActivity()).openCompanyProfile());
            return b.getRoot();
        }
    }

    public static class PerfilEmpresaFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            ActivityPerfilEmpresaBinding b = ActivityPerfilEmpresaBinding.inflate(i, c, false);
            return b.getRoot();
        }
    }

    public static class SeguimientoFragment extends Fragment {
        @Nullable @Override public View onCreateView(@NonNull LayoutInflater i, @Nullable ViewGroup c, @Nullable Bundle s) {
            ActivitySeguimientoPostulacionBinding b = ActivitySeguimientoPostulacionBinding.inflate(i, c, false);
            return b.getRoot();
        }
    }

    public static class SimpleOfertaAdapter extends RecyclerView.Adapter<SimpleOfertaAdapter.ViewHolder> {
        private MainActivity activity;
        public SimpleOfertaAdapter(MainActivity activity) { this.activity = activity; }
        @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int t) {
            return new ViewHolder(ItemOfertaBinding.inflate(LayoutInflater.from(p.getContext()), p, false));
        }
        @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            h.itemView.setOnClickListener(v -> activity.openJobDetail());
        }
        @Override public int getItemCount() { return 10; }
        public static class ViewHolder extends RecyclerView.ViewHolder {
            ItemOfertaBinding b;
            public ViewHolder(ItemOfertaBinding binding) { super(binding.getRoot()); this.b = binding; }
        }
    }
}
