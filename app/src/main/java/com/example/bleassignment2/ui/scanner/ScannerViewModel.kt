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
    private var _currentSelection : MutableLiveData<BluetoothDevice> = MutableLiveData()
    val devices: LiveData<List<Pair<BluetoothDevice, Int>>> = bleManager.devices
    var currentSelection : LiveData<BluetoothDevice>? = _currentSelection

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
        _currentSelection.setValue(device)
        bleConnectionManager.connectToDevice(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        _currentSelection = MutableLiveData()
        bleConnectionManager.disconnect()
    }
}