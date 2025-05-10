package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val bleManager = BLEManager()
    private val bleConnectionManager = BLEConnectionManager(application.applicationContext)
    val devices: LiveData<List<Pair<BluetoothDevice, Int>>> = bleManager.devices

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

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        bleConnectionManager.connectToDevice(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        bleConnectionManager.disconnect()
    }
}