package com.example.bleassignment2.ui.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is device Fragment"
    }
    val text: LiveData<String> = _text
}