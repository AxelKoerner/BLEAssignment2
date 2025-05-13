package com.example.bleassignment2.ui.device

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel : ViewModel() {

    private var _currentSelection: MutableLiveData<BluetoothDevice?> = MutableLiveData()
    var currentSelection: LiveData<BluetoothDevice?> = _currentSelection

    private val _temperature = MutableLiveData<Float?>()
    val temperature: LiveData<Float?> = _temperature
    private val _humidity = MutableLiveData<Float?>()
    val humidity: LiveData<Float?> = _humidity
    private val _unknown = MutableLiveData<ByteArray?>()
    val unknown: LiveData<ByteArray?> = _unknown

    fun setActive(device: BluetoothDevice) {
        _currentSelection.value = device
    }

    fun setTemp(temp: Float) {
        _temperature.value = temp
    }
    fun setHum(hum: Float) {
        _humidity.value = hum
    }
    fun setUnkown(unknown: ByteArray) {
        _unknown.value = unknown
    }

    fun clear() {
        _currentSelection.value = null
    }

}