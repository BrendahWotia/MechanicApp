package com.example.mobilemechanic.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private String mName;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment : " + mName);
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void retrieveName(String input){
        mText = new MutableLiveData<>();
        mText.setValue(input);
//        mName = input;
    }
}