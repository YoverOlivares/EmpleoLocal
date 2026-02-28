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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView tvToolbarTitle;
    private View flNotifications;
    private View ivEditProfile;
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
        bottomNavigation = findViewById(R.id.bottom_navigation);

        // Manejo de Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar navegación
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                updateToolbar("EmpleoLocal", true, false);
                loadFragment(new InicioFragment());
                return true;
            } else if (id == R.id.nav_map) {
                updateToolbar("Mapa de Empleos", true, false);
                loadFragment(new MapaFragment());
                return true;
            } else if (id == R.id.nav_apply) {
                updateToolbar("Mis Postulaciones", true, false);
                loadFragment(new PostulacionesFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                updateToolbar("Mi Perfil", false, true);
                loadFragment(new PerfilFragment());
                return true;
            }
            return false;
        });

        // Cargar pantalla de inicio por defecto
        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        }
    }

    private void updateToolbar(String title, boolean showNotifications, boolean showEdit) {
        if (tvToolbarTitle != null) {
            tvToolbarTitle.setText(title);
        }
        if (flNotifications != null) {
            flNotifications.setVisibility(showNotifications ? View.VISIBLE : View.GONE);
        }
        if (ivEditProfile != null) {
            ivEditProfile.setVisibility(showEdit ? View.VISIBLE : View.GONE);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    // Fragments internos
    public static class InicioFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_inicio, container, false);
        }
    }

    public static class MapaFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_mapa_empleos, container, false);
        }
    }

    public static class PostulacionesFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_postulaciones, container, false);
        }
    }

    public static class PerfilFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_perfil, container, false);
        }
    }
}