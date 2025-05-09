package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BLEManager() {

    //Project uses MVVM architecture, BLEManager is the Model (abstraction of the datasource), ScannerFragment is the View, ScannerViewModel is obv. the ViewModel (middle layer that holds live data etc.)

    //Initialize the Adapter and Scanner
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? get() = bluetoothAdapter?.bluetoothLeScanner

    //Internal storage of the devices as livedata list of Pairs of BluetoothDevice and the UUID (Int) with exposed getter function for the devices
    private val _devices = MutableLiveData<List<Pair<BluetoothDevice, Int>>>()
    val devices: LiveData<List<Pair<BluetoothDevice, Int>>> get() = _devices

    //Callback function that gets called from the startScan() / stopScan() method. Here the ScanResult can get processed
    private var scanCallback: ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let { res ->
                val device = res.device
                val rssi = res.rssi
                val current = _devices.value ?: listOf()
                if (current.none { it.first.address == device.address }) {
                    _devices.value = current + Pair(device, rssi)
                }
            }
        }
    }

    //Start Scan with a Filter and Settings (both standard and not explicitly defined)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan() {
        _devices.value = emptyList()
        val scanFilter = ScanFilter.Builder().build()
        val settings = ScanSettings.Builder().build()
        bluetoothLeScanner?.startScan(listOf(scanFilter), settings, scanCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
    }


}