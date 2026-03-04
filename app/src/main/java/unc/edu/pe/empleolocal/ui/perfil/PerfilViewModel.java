package unc.edu.pe.empleolocal.ui.perfil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.data.repository.FirebaseRepository;

public class PerfilViewModel extends ViewModel {
    private final FirebaseRepository repository;
    private final MutableLiveData<User> userProfile = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> updateSuccess = new MutableLiveData<>();

    public PerfilViewModel() {
        this.repository = new FirebaseRepository();
    }

    public LiveData<User> getUserProfile() { return userProfile; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<Boolean> getUpdateSuccess() { return updateSuccess; }

    public void loadUserProfile() {
        String uid = repository.getCurrentUserUid();
        if (uid == null) {
            error.setValue("Sesión expirada. Por favor, inicie sesión.");
            return;
        }

        isLoading.setValue(true);
        repository.getUserProfile(uid).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    try {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            userProfile.setValue(user);
                        } else {
                            error.setValue("Error al convertir los datos del perfil.");
                        }
                    } catch (Exception e) {
                        error.setValue("Formato de datos incompatible: " + e.getMessage());
                    }
                } else {
                    error.setValue("Perfil no encontrado. Complete su registro.");
                }
            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error de conexión";
                error.setValue("Error en el servidor: " + errorMsg);
            }
        });
    }

    public void updateProfile(String nombre, String apellido, String telefono, List<String> sectores) {
        String uid = repository.getCurrentUserUid();
        if (uid == null) return;

        isLoading.setValue(true);
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombre", nombre);
        updates.put("apellido", apellido);
        updates.put("telefono", telefono);
        updates.put("sectores", sectores);

        repository.updateUserProfile(uid, updates).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                updateSuccess.setValue(true);
                loadUserProfile(); // Recargar datos locales en el ViewModel compartido
            } else {
                error.setValue("No se pudo actualizar el perfil.");
            }
        });
    }

    public void resetUpdateStatus() {
        updateSuccess.setValue(null);
    }
}
