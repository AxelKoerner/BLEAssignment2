package com.example.bleassignment2.ui.device

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.bleassignment2.R
import com.example.bleassignment2.databinding.FragmentDeviceBinding
import com.example.bleassignment2.ui.scanner.ScannerViewModel

class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null
    private lateinit var broadcastReceiver: BroadcastReceiver

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //@SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scannerViewModel =
            ViewModelProvider(this.requireActivity()).get(ScannerViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewName: TextView = binding.deviceName
        //val textView: TextView = binding.deviceList.
        scannerViewModel.currentSelection.observe(viewLifecycleOwner) @androidx.annotation.RequiresPermission(
            allOf = [android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ]
        ) { selectedBluetoothDevice ->

            textViewName.text = selectedBluetoothDevice.name
            println(textViewName.id)
        }

        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // check if its the correct intent
                println("==============RECIVED BROADCAST UPDATE================")
                if (intent.action == "com.example.bleassignment2.ACTION_CHARACTERISTIC_CHANGED") {
                    println("==============On RECIVE BROADCAST CORRECT INTENT================")
                    val characteristicUuid = intent.getStringExtra("characteristic_uuid")
                    val characteristicValue = intent.getByteArrayExtra("characteristic_value")?.let {
                        String(it)  // convert ByteArray in String
                    }

                    // update dTextView with new characteristic
                    val updatedText = "UUID: $characteristicUuid\nValue: $characteristicValue"
                    binding.deviceName.text = updatedText
                }
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}