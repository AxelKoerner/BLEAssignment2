package com.example.bleassignment2

import android.bluetooth.BluetoothGattCharacteristic
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bleassignment2.databinding.ActivityMainBinding
import com.example.bleassignment2.ui.device.DeviceViewModel
import com.example.bleassignment2.ui.scanner.ScannerViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var scannerViewModel: ScannerViewModel
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_scanner, R.id.navigation_device
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        scannerViewModel =
            ViewModelProvider(this).get(ScannerViewModel::class.java)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // check if its the correct intent
                if (intent.action == "com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED") {
                    val type = intent.getStringExtra("type")

                    when (type) {
                        "temperature" -> {
                            val temp = intent.getFloatExtra("temperature_celsius", -1f)
                            val tempChar = intent.getParcelableExtra<BluetoothGattCharacteristic>("raw_characteristic")
                            println("Temperature update received: $temp °C")
                            scannerViewModel.setTemp(temp,tempChar!!)
                            //binding.read1CharacteristicValue.text = "$temp °C"
                        }
                        "humidity" -> {
                            val hum = intent.getFloatExtra("humidity_percent", -1f)
                            println("Humidity update received: $hum %")
                            scannerViewModel.setHum(hum)
                        //binding.read2CharacteristicValue.text = "$hum %"
                        }
                        else -> {
                            println("Unknown characteristic update received.")
                            val unknown = intent.getByteArrayExtra("unknown_value")?: ByteArray(1)
                            println("Unknown update received: $unknown %")
                            scannerViewModel.setUnkown(unknown)

                        }
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(
            broadcastReceiver,
            IntentFilter("com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED")
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }


}