package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.app.Application
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.UUID
import kotlin.uuid.Uuid

class ScannerViewModel(application: Application) : AndroidViewModel(application) {

    private val bleManager = BLEManager()
    private val bleConnectionManager = BLEConnectionManager(application.applicationContext)
    private var _isScanning: Boolean = false
    private var _currentSelection: MutableLiveData<BluetoothDevice?> = MutableLiveData()
    var currentSelection: LiveData<BluetoothDevice?> = _currentSelection
    val devices: LiveData<List<Pair<BluetoothDevice, Int>>> = bleManager.devices
    //var currentSelection : LiveData<BluetoothDevice> = _currentSelection

    private val _buttonText = MutableLiveData<String>().apply {
        value = "Scan"
    }

    var buttonText: LiveData<String> = _buttonText

    private var _temperatureChar : BluetoothGattCharacteristic? = null
    private var _humidityChar : BluetoothGattCharacteristic? = null
    private var _lightChar : BluetoothGattCharacteristic? = null
    private var _unknownChar : BluetoothGattCharacteristic? = null


    private val _temperature = MutableLiveData<Float?>()
    val temperature: LiveData<Float?> = _temperature

    private val _humidity = MutableLiveData<Float?>()
    val humidity: LiveData<Float?> = _humidity

    private val _light = MutableLiveData<Short?>()
    val light: LiveData<Short?> = _light

    private val _unknown = MutableLiveData<ByteArray?>()
    val unknown: LiveData<ByteArray?> = _unknown

    fun setTemp(temp: Float, tempChar: BluetoothGattCharacteristic?) {
        _temperature.value = temp
        _temperatureChar = tempChar
    }
    fun setHum(hum: Float, tempChar: BluetoothGattCharacteristic?) {
        _humidity.value = hum
        _humidityChar= tempChar
    }
    fun setLight(light: Short, tempChar: BluetoothGattCharacteristic?) {
        _light.value = light
        _lightChar= tempChar
    }
    fun setUnkown(unknown: ByteArray, tempChar: BluetoothGattCharacteristic?) {
        _unknown.value = unknown
        _unknownChar = tempChar
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun initReadCharacteristic(type: String) {
        if (type == "temp") bleConnectionManager.read(_temperatureChar!!)
        else if (type == "hum") bleConnectionManager.read(_humidityChar!!)
        else if (type == "unknown") bleConnectionManager.read(_unknownChar!!)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun initNotifyCharacteristic(type: String) {
        if (type == "temp") bleConnectionManager.requestNotify(_temperatureChar!!)
        else if (type == "hum") bleConnectionManager.requestNotify(_humidityChar!!)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun initWriteCharacteristic(type: String, value: ByteArray) {
        if (type == "light") bleConnectionManager.write(_lightChar!!,value)
    }




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
        _currentSelection.setValue(device)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun isDeviceConnected(device: BluetoothDevice): Boolean {
        println("CALLED ISConnected")
        val result = _currentSelection.value == device
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
        _currentSelection.value = null
    }

}