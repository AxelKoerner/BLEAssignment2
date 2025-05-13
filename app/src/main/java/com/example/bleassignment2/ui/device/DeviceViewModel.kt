package com.example.bleassignment2.ui.device

import android.bluetooth.BluetoothDevice
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DeviceViewModel : ViewModel() {

    private var _currentSelection: MutableLiveData<BluetoothDevice?> = MutableLiveData()
    var currentSelection: LiveData<BluetoothDevice?> = _currentSelection

    private val _broadcastData = MutableLiveData<Triple<String?, String?, String?>>()
    val broadcastData: LiveData<Triple<String?, String?, String?>> get() = _broadcastData

    fun setBroadcastData(uuid: String?, serviceUuid: String?, value: String?) {
        println(_broadcastData.value.toString())
        _broadcastData.value = Triple(uuid, serviceUuid, value)
        println(_broadcastData.value.toString())
    }
    fun setActive(device: BluetoothDevice) {
        _currentSelection.value = device
    }

    fun clear() {
        _currentSelection.value = null
    }

}