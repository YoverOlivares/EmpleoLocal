package unc.edu.pe.empleolocal.ui.auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;

import unc.edu.pe.empleolocal.data.model.User;
import unc.edu.pe.empleolocal.data.repository.FirebaseRepository;

public class AuthViewModel extends ViewModel {
    private final FirebaseRepository repository;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public AuthViewModel() {
        this.repository = new FirebaseRepository();
    }

    public LiveData<FirebaseUser> getUserLiveData() { return userLiveData; }
    public LiveData<String> getErrorLiveData() { return errorLiveData; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void login(String email, String password) {
        isLoading.setValue(true);
        repository.login(email, password).addOnCompleteListener(task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                userLiveData.setValue(task.getResult().getUser());
            } else {
                errorLiveData.setValue(task.getException() != null ? task.getException().getMessage() : "Error en login");
            }
        });
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
                        errorLiveData.setValue(saveTask.getException() != null ? saveTask.getException().getMessage() : "Error al guardar perfil");
                    }
                });
            } else {
                isLoading.setValue(false);
                errorLiveData.setValue(task.getException() != null ? task.getException().getMessage() : "Error en registro");
            }
        });
    }
}
