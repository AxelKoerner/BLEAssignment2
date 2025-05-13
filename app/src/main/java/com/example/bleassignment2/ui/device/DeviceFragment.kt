package com.example.bleassignment2.ui.device

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bleassignment2.databinding.FragmentDeviceBinding
import com.example.bleassignment2.ui.scanner.ScannerViewModel

class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null
    private lateinit var broadcastReceiver: BroadcastReceiver

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //@SuppressLint("MissingPermission")
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scannerViewModel =
            ViewModelProvider(this.requireActivity()).get(ScannerViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tv_deviceName: TextView = binding.deviceName
        val tv_deviceAddress: TextView = binding.deviceAddress
        //val textView: TextView = binding.deviceList.
        scannerViewModel.currentSelection.observe(viewLifecycleOwner)
        @RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION])
        { selectedBluetoothDevice ->

            tv_deviceName.text = selectedBluetoothDevice.name?:"Unknown Name"
            tv_deviceAddress.text = selectedBluetoothDevice.address
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // check if its the correct intent
                if (intent.action == "com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED") {
                    val type = intent.getStringExtra("type")

                    when (type) {
                        "temperature" -> {
                            val temp = intent.getFloatExtra("temperature_celsius", -1f)
                            println("Temperature update received: $temp °C")
                            binding.read1CharacteristicName.text = "Temperature"
                            binding.read1CharacteristicValue.text = "$temp °C"
                        }
                        "humidity" -> {
                            val hum = intent.getFloatExtra("humidity_percent", -1f)
                            println("Humidity update received: $hum %")
                            binding.read2CharacteristicName.text = "Humidity"
                            binding.read2CharacteristicValue.text = "$hum %"
                        }
                        else -> {
                            println("Unknown characteristic update received.")
                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                broadcastReceiver,
                IntentFilter("com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            requireContext().registerReceiver(
                broadcastReceiver,
                IntentFilter("com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED")
            )
        }

        val readButton: Button = binding.writeCharacteristicWriteButton
        readButton.setOnClickListener {
                println("====BUTTON CLICKED======")
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireContext().unregisterReceiver(broadcastReceiver)
    }
}