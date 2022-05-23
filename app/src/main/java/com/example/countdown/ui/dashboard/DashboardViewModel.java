package com.example.countdown.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
//        mText.setValue("This is dashboard fragment");
    }

    public MutableLiveData<String> getText() {
        return mText;
    }

}