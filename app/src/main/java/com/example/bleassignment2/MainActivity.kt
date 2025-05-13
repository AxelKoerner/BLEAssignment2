package com.example.bleassignment2

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

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var deviceViewModel: DeviceViewModel
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

        deviceViewModel =
            ViewModelProvider(this).get(DeviceViewModel::class.java)

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // check if its the correct intent
                println("==============RECIVED BROADCAST UPDATE================")
                if (intent.action == "com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED") {
                    println("==============On RECIVE BROADCAST CORRECT INTENT================")
                    val characteristicUuid = intent.getStringExtra("characteristic_uuid")
                    val characteristicServiceUuid =
                        intent.getStringExtra("characteristic_service_uuid")
                    val characteristicValue =
                        intent.getByteArrayExtra("characteristic_value")?.let {
                            String(it)  // convert ByteArray in String
                        }
                    deviceViewModel.setBroadcastData(
                        characteristicUuid,
                        characteristicServiceUuid,
                        characteristicValue
                    )
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