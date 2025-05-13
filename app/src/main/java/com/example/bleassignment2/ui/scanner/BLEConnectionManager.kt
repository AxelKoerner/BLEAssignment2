package com.example.bleassignment2.ui.scanner

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.RequiresPermission
import java.util.LinkedList
import java.util.Queue
import java.util.UUID
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.nio.ByteBuffer
import java.nio.ByteOrder


class BLEConnectionManager(private val context: Context) {
    private var gattServer: BluetoothGatt? = null
    private val characteristicQueue: Queue<BluetoothGattCharacteristic> = LinkedList()
    private val serviceUUIDs = listOf(
        UUID.fromString("00000001-0000-0000-FDFD-FDFDFDFDFDFD"),  // IPVS-Light
        UUID.fromString("00000002-0000-0000-FDFD-FDFDFDFDFDFD"),  // IPVSWeather
        UUID.fromString("000000ff-0000-1000-8000-00805f9b34fb")  // Testing with ESP
    )

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToDevice(device: BluetoothDevice) {
        gattServer = device.connectGatt(context, false, gattCallback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        gattServer?.disconnect()
        //gattServer?.close()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun read(characteristic: BluetoothGattCharacteristic) {
        gattServer?.readCharacteristic(characteristic)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun write(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        writeCharacteristic(characteristic, value)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun requestNotify(characteristic: BluetoothGattCharacteristic) {
        gattServer?.setCharacteristicNotification(characteristic, true)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            println("onConnectionStateChange: status=$status, newState=$newState")
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                println("Connected to GATT Server")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                println("Disconnected from GATT Server")
                gatt.close()
                if (gatt == gattServer) {
                    println("Nulling GATT Server Ref")
                    gattServer = null
                }

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
                        println("Characteristic UUID: ${characteristic.uuid}")
                        if (service.uuid in serviceUUIDs) {
                            characteristicQueue.add(characteristic)
                            broadcastUpdate(characteristic) // Populate the viewmodel with all available characteristics
                            println("Added Characteristic UUID: ${characteristic.uuid} in Service with UUID: ${service.uuid} to queue")
                            //val intensity: Int = 1000  //TODO remove this
                            //val valueToWrite = ByteBuffer.allocate(2).putShort(intensity.toShort()).array() //TODO remove this
                            //writeCharacteristic(characteristic, valueToWrite) //TODO remove this
                            // enableNotifications(characteristic) //TODO remove this
                        }
                    }
                }
                readNextCharacteristics(gatt)
            }
        }

        //here the information about the characteristic value is provided needs to be broadcasted
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = characteristic.value
                println("Characteristic read: ${value.contentToString()}")

                broadcastUpdate(characteristic)
            }
        }

        //changed characteristic value needs to be broadcasted
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val value = characteristic.value
            println("Characteristic changed: ${value.contentToString()}")

            broadcastUpdate(characteristic)
        }
    }

    //reading the characteristics is an async function, the value is provided in onCharacteristicsRead function
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun readNextCharacteristics(gatt: BluetoothGatt) {
        println("reading Chars in loop")
        while (characteristicQueue.isNotEmpty()) {
            val uuid = characteristicQueue.poll()
            gatt.readCharacteristic(uuid)
        } //else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "Characteristics UUID are all read", Toast.LENGTH_SHORT)
                    .show()
            //}
        }
    }

    companion object {
        val TEMPERATURE_UUID: UUID = UUID.fromString("00002a1c-0000-1000-8000-00805f9b34fb")
        val HUMIDITY_UUID: UUID = UUID.fromString("00002a6f-0000-1000-8000-00805f9b34fb")
        val LIGHT_UUID: UUID = UUID.fromString("10000001-0000-0000-FDFD-FDFDFDFDFDFD")
        //val DEBUG_UUID: UUID = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb")
        //val TEMPERATURE_UUID: UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb")
        //val HUMIDITY_UUID: UUID = UUID.fromString("0000ee01-0000-1000-8000-00805f9b34fb")
        //val LIGHT_UUID: UUID = UUID.fromString("0000ee01-0000-1000-8000-00805f9b34fb")
        //val DEBUG_UUID: UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb")
    }

    private fun broadcastUpdate(characteristic: BluetoothGattCharacteristic) {
        val intent = Intent("com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED")
        intent.putExtra("characteristic_uuid", characteristic.uuid.toString())
        intent.putExtra("characteristic_value", characteristic.value)
        intent.putExtra("raw_characteristic", characteristic)

        // Send intent to all registered Broadcast-Receiver
        when (characteristic.uuid) {
            TEMPERATURE_UUID -> {
                val temperature = parseSfloat(characteristic.value ?: ByteArray(4))

                intent.putExtra("type", "temperature")
                intent.putExtra("temperature_celsius", temperature)
                println("Broadcasting Temperature: $temperature °C")
            }
            HUMIDITY_UUID -> {
                val humidity = ByteBuffer.wrap(characteristic.value ?: ByteArray(4)).order(ByteOrder.LITTLE_ENDIAN).short / 100.0f
                intent.putExtra("type", "humidity")
                intent.putExtra("humidity_percent", humidity)
                println("Broadcasting Humidity: $humidity %")
            }

            LIGHT_UUID -> {
                characteristic.value
                val light =
                    ByteBuffer.wrap(characteristic.value?: ByteArray(2)).order(ByteOrder.LITTLE_ENDIAN).short
                intent.putExtra("type", "light")
                intent.putExtra("light_value", light)
                println("Broadcasting Light as read: $light %")
            }

            //DEBUG_UUID -> {
            //    val debug = characteristic.value
            //    intent.putExtra("type", "debug")
            //    intent.putExtra("debug_val", debug)
            //    println("Broadcasting Humidity: $debug")
            //}

            else -> {
                intent.putExtra("type", "unknown")
                intent.putExtra("unknown_value", characteristic.value)
            }
        }
        intent.putExtra("raw_characteristic", characteristic)
        println("==============CALLED BROADCAST UPDATE================")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    private fun parseSfloat(bytes: ByteArray): Float {
        if (bytes.size < 2) {
            throw IllegalArgumentException("sfloat benötigt mindestens 2 Bytes")
        }

        val shortValue = ByteBuffer.wrap(bytes.copyOfRange(1, 3))
            .order(ByteOrder.LITTLE_ENDIAN)
            .short

        return shortValue / 100.0f
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun writeCharacteristic(characteristic: BluetoothGattCharacteristic, value: ByteArray) {
        characteristic.value = value
        val success = gattServer?.writeCharacteristic(characteristic) ?: false
        println("====CALLED WRITE CHARACTERISTIC=====")
        if (success) {
            println("Write successful")
        } else {
            println("Write failed")
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun enableNotifications(characteristic: BluetoothGattCharacteristic) {
        gattServer?.let { gatt ->
            val success = gatt.setCharacteristicNotification(characteristic, true)
            if (success) {
                val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                if (descriptor != null) {
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                    println("Notification enabled for ${characteristic.uuid}")
                } else {
                    println("CCCD Descriptor not found for ${characteristic.uuid}")
                }
            } else {
                println("setCharacteristicNotification failed for ${characteristic.uuid}")
            }
        }
    }




}
