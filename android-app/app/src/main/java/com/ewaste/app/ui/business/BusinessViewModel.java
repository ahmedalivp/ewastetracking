package com.ewaste.app.ui.business;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.ewaste.app.data.model.CategoryResponse;
import com.ewaste.app.data.model.ComponentRequestResponse;
import com.ewaste.app.data.model.ComponentResponse;
import com.ewaste.app.data.repository.BusinessRepository;
import com.ewaste.app.data.repository.ConsumerRepository;
import com.ewaste.app.ui.common.Resource;

import java.util.List;

public class BusinessViewModel extends AndroidViewModel {

    private final BusinessRepository businessRepository;
    private final ConsumerRepository consumerRepository;

    public BusinessViewModel(@NonNull Application application) {
        super(application);
        this.businessRepository = new BusinessRepository();
        this.consumerRepository = new ConsumerRepository();
    }

    public LiveData<Resource<List<ComponentResponse>>> getAvailableComponents(String category) {
        return businessRepository.getAvailableComponents(category);
    }

    public LiveData<Resource<ComponentRequestResponse>> requestComponent(Long componentId) {
        return businessRepository.requestComponent(componentId);
    }

    public LiveData<Resource<List<ComponentRequestResponse>>> getMyRequests() {
        return businessRepository.getMyRequests();
    }

    public LiveData<Resource<List<CategoryResponse>>> getCategories() {
        return consumerRepository.getCategories();
    }
}
