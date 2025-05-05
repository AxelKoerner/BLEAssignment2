package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel : ViewModel() {

    private val bleManager = BLEManager()
    val devices: LiveData<List<BluetoothDevice>> = bleManager.devices

    private val _buttonText = MutableLiveData<String>().apply {
        value = "Scan"
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        bleManager.startScan()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        bleManager.stopScan()
    }
}