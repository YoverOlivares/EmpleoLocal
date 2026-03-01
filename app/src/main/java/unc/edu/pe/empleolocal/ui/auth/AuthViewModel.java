package unc.edu.pe.empleolocal.ui.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.data.repository.FirebaseRepository;

public class AuthViewModel extends ViewModel {
    private final FirebaseRepository repository;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isEmailAvailable = new MutableLiveData<>();

    public AuthViewModel() {
        this.repository = new FirebaseRepository();
    }

    public LiveData<FirebaseUser> getUserLiveData() { return userLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getSuccessMessage() { return successMessage; }
    public LiveData<Boolean> getIsEmailAvailable() { return isEmailAvailable; }

    public void login(String email, String password) {
        isLoading.setValue(true);
        repository.login(email, password).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                userLiveData.setValue(task.getResult().getUser());
            } else {
                errorLiveData.setValue("Correo o contraseña incorrectos");
            }
        });
    }

    public void checkEmailAvailability(String email) {
        isLoading.setValue(true);
        repository.checkEmailRegistered(email).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                // Si la lista de métodos no está vacía, el correo YA está registrado
                boolean exists = task.getResult().getSignInMethods() != null && 
                                !task.getResult().getSignInMethods().isEmpty();
                if (exists) {
                    errorLiveData.setValue("Este correo electrónico ya está registrado");
                    isEmailAvailable.setValue(false);
                } else {
                    isEmailAvailable.setValue(true);
                }
            } else {
                // Por seguridad de Firebase, a veces falla la consulta. 
                // Si no podemos verificar, permitimos pasar y que el error salte en el registro final.
                isEmailAvailable.setValue(true); 
            }
        });
    }

    public void resetEmailAvailableState() {
        isEmailAvailable.setValue(null);
    }

    public void register(User userData, String password) {
        isLoading.setValue(true);
        repository.register(userData.getCorreo(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String uid = task.getResult().getUser().getUid();
                userData.setUid(uid);
                repository.saveUser(userData).addOnCompleteListener(saveTask -> {
                    isLoading.setValue(false);
                    if (saveTask.isSuccessful()) {
                        userLiveData.setValue(task.getResult().getUser());
                    } else {
                        errorLiveData.setValue("Error al crear el perfil de usuario");
                    }
                });
            } else {
                isLoading.setValue(false);
                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    errorLiveData.setValue("Este correo electrónico ya está registrado");
                } else {
                    errorLiveData.setValue("No se pudo completar el registro. Intente nuevamente.");
                }
            }
        });
    }

    public void resetPassword(String email) {
        if (email == null || email.trim().isEmpty()) {
            errorLiveData.setValue("Ingrese su correo electrónico");
            return;
        }

        String cleanEmail = email.trim().toLowerCase();
        
        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            errorLiveData.setValue("El formato del correo no es válido");
            return;
        }

        isLoading.setValue(true);
        repository.sendPasswordResetEmail(cleanEmail).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                successMessage.setValue("Enlace de recuperación enviado. Revise su bandeja de entrada.");
            } else {
                errorLiveData.setValue("No se pudo enviar el correo de recuperación");
            }
        });
    }
}
