package com.example.bleassignment2.ui.device

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
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}