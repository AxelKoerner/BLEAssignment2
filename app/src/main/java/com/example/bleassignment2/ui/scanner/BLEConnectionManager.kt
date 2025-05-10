package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import androidx.annotation.RequiresPermission

class BLEConnectionManager(private val context: Context) {
    private var gattServer: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        gattServer = device.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        gattServer?.disconnect()
        gattServer?.close()
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                println("Connected to GATT Server")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                println("Disconnected from GATT Server")
            }
        }

        //Service UUID for the IPVSWeather: 00000002-0000-0000-FDFD-FDFDFDFDFDFD
        //Service UUID for the IPVS-Light: 00000001-0000-0000-FDFD-FDFDFDFDFDFD
        //TODO for the Service UUID and extract the necessary information through the Characteristic UUID
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                println("Service Discovered")
                for (service: BluetoothGattService in gatt.services) {
                   println("Service UUID: ${service.uuid}")
                    for (characteristic: BluetoothGattCharacteristic in service.characteristics) {
                        println("Characteristic UUID: ${characteristic.uuid}")
                    }
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic.value
                println("Characteristic read: ${value.contentToString()}")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = characteristic.value
            println("Characteristic changed: ${value.contentToString()}")
        }
    }
}