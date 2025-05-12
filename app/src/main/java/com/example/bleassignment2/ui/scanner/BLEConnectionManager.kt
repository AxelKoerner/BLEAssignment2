package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.widget.Toast
import androidx.annotation.RequiresPermission
import java.util.Arrays
import java.util.LinkedList
import java.util.Queue
import java.util.UUID




class BLEConnectionManager(private val context: Context) {
    private var gattServer: BluetoothGatt? = null
    private val characteristicQueue: Queue<BluetoothGattCharacteristic> = LinkedList()
    private val characteristicUUIDs = listOf(
        UUID.fromString("10000001-0000-0000-FDFD-FDFDFDFDFDFD"),  // Intensity for IPVS-Light
        UUID.fromString("00002A6F-0000-1000-8000-00805F9B34FB"),   // Humidity for IPVSWeather
        UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")  //Temperature for IPVSWeather
    )

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
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                println("Service Discovered")
                for (service: BluetoothGattService in gatt.services) {
                   println("Service UUID: ${service.uuid}")
                    for (characteristic: BluetoothGattCharacteristic in service.characteristics) {
                        if (characteristic.uuid in characteristicUUIDs) {
                            characteristicQueue.add(characteristic)
                        }
                    }
                }
                readNextCharacteristics(gatt)
            }
        }

        //here the information about the characteristic value is provided TODO needs to be broadcasted
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

        //TODO changed characteristic value needs to be broadcasted
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = characteristic.value
            println("Characteristic changed: ${value.contentToString()}")
        }
    }

    //reading the characteristics is an async function, the value is provided in onCharacteristicsRead function
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun readNextCharacteristics(gatt: BluetoothGatt) {
        if (characteristicQueue.isNotEmpty()) {
            val uuid = characteristicQueue.poll()
            gatt.readCharacteristic(uuid)
        } else {
            Toast.makeText(context, "Characteristics UUID are all read", Toast.LENGTH_SHORT).show()
        }
    }

    private fun broadcastUpdate() {
        //todo implement broadcasting of information
    }
}
