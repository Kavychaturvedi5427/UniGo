package com.kavya.unigo.ui.features.Assignment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kavya.unigo.data.model.AssignmentResult;
import com.kavya.unigo.data.repository.AssignmentRepository;

public class AddAssignmentViewModel extends ViewModel {
    private MutableLiveData<AssignmentState> State = new MutableLiveData<>();

    public LiveData<AssignmentState> getAssignmentState() {
        return State;
    }

    private final AssignmentRepository repository;

    public AddAssignmentViewModel() {
        this.repository = new AssignmentRepository();
    }

    public void StoreAssignment(String title, String subj, String desc, long DuedateMillis) {
        // validating the data...
        if (title.isEmpty() || subj.isEmpty() || desc.isEmpty() || DuedateMillis <= 0) {
            State.setValue(new AssignmentState.AssignmentError("Please fill all fields."));
            return;
        }
        State.setValue(new AssignmentState.AssignmentLoading());
        repository.StoreAssign(title, subj, desc, DuedateMillis, new AssignmentRepository.AssignCallback() {
            @Override
            public void onResult(AssignmentResult result) {
                if (result instanceof AssignmentResult.AssignSuccess) {
                    State.postValue(new AssignmentState.AssignmentSuccess());
                } else if (result instanceof AssignmentResult.AssignError) {
                    String errorMes = ((AssignmentResult.AssignError) result).message;
                    State.postValue(new AssignmentState.AssignmentError(errorMes));
                }
            }
        });

    }
}
