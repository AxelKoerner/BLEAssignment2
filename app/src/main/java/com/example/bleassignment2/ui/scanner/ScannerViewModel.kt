package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val bleManager = BLEManager()
    private val bleConnectionManager = BLEConnectionManager(application.applicationContext)
    private var _isScanning: Boolean = false
    var currentSelection: MutableLiveData<BluetoothDevice> = MutableLiveData()
    val devices: LiveData<List<Pair<BluetoothDevice, Int>>> = bleManager.devices
    //var currentSelection : LiveData<BluetoothDevice> = _currentSelection

    private val _buttonText = MutableLiveData<String>().apply {
        value = "Scan"
    }

    var buttonText: LiveData<String> = _buttonText

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        if (!_isScanning) {
            bleManager.startScan()
            _isScanning = true
            _buttonText.setValue("Cancel Scan")

        }else{
            println("Tried to start scanning while scan already running")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        if (_isScanning) {
            bleManager.stopScan()
            _isScanning = false
            _buttonText.setValue("Scan")
        }else{
            println("Tried to stop scanning while no scan was running")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        println("CALLED CONNCT")
        if (_isScanning) {
            stopScan()
        }
        bleConnectionManager.connectToDevice(device)
        currentSelection.setValue(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isDeviceConnected(device: BluetoothDevice): Boolean {
        println("CALLED ISConnected")
        val result = currentSelection.value == device
        return result;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isScanning(): Boolean {
        println("CALLED IS_Scanning")
        return _isScanning;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        println("CALLED DICONNECT")
        if (_isScanning) {
            stopScan()
        }
        bleConnectionManager.disconnect()
        currentSelection = MutableLiveData()
        val temp = currentSelection.value
    }

}