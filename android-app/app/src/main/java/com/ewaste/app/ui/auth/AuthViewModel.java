package com.ewaste.app.ui.auth;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ewaste.app.EWasteApplication;
import com.ewaste.app.data.model.AuthResponse;
import com.ewaste.app.data.model.DropOffPointResponse;
import com.ewaste.app.data.model.RegisterRequest;
import com.ewaste.app.data.repository.AuthRepository;
import com.ewaste.app.ui.common.Resource;

import java.util.List;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<String> validationError = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(EWasteApplication.getInstance().getSessionManager());
    }

    public LiveData<String> getValidationError() {
        return validationError;
    }

    public boolean validateLogin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            validationError.setValue("Email address is required");
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            validationError.setValue("Please enter a valid email address");
            return false;
        }
        if (password == null || password.trim().isEmpty()) {
            validationError.setValue("Password is required");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    public boolean validateRegistration(String fullName, String email, String password, String role) {
        if (fullName == null || fullName.trim().isEmpty()) {
            validationError.setValue("Full name is required");
            return false;
        }
        if (email == null || email.trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            validationError.setValue("Valid email is required");
            return false;
        }
        if (password == null || password.trim().length() < 6) {
            validationError.setValue("Password must be at least 6 characters");
            return false;
        }
        if (role == null || role.trim().isEmpty()) {
            validationError.setValue("Please select an account role");
            return false;
        }
        validationError.setValue(null);
        return true;
    }

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        return authRepository.login(email.trim(), password.trim());
    }

    public LiveData<Resource<AuthResponse>> register(RegisterRequest request) {
        return authRepository.register(request);
    }

    public LiveData<Resource<List<DropOffPointResponse>>> getDropOffPoints() {
        return authRepository.getDropOffPoints();
    }
}
