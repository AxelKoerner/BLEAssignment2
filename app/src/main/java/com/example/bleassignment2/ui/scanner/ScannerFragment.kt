package com.example.bleassignment2.ui.scanner

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bleassignment2.databinding.FragmentScannerBinding
import com.example.bleassignment2.ui.device.DeviceAdapter

class ScannerFragment : Fragment() {

    private var _binding: FragmentScannerBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val scannerViewModel =
            ViewModelProvider(this.requireActivity()).get(ScannerViewModel::class.java)

        //inflate the fragment_scanner.xml using View Binding
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button: Button = binding.scannerButton
        val recyclerView = binding.deviceList
        val adapter = DeviceAdapter(emptyList(), { device ->
            scannerViewModel.connectToDevice(device) // This is the declaration of the function stored as `onConnectClick` in a DeviceAdapter, called when Button is clicked
        })

        //assign the adapter to the recycle view so that it knows how many items, definition of items and the data to show
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        scannerViewModel.devices.observe(viewLifecycleOwner) { devices ->
            adapter.updateDevices(devices)
        }


        //backwards compatibility (chooses permission that are required based on the sdk version)
        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.BLUETOOTH_ADMIN
            )
        }


        //request permission launcher
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                // All permissions granted
                scannerViewModel.startScan()
                binding.scannerButton.text = "Cancel Scan"
            } else {
                println("Permission denied")
            }
        }

        //checks if all self permission are granted
        fun hasPermissions(): Boolean {
            return requiredPermissions.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        //logic for the button, start scan if permission is granted (calls middle layer ViewModel method) else launch the permission requester
        button.setOnClickListener {
            if (button.text == "Scan") {
                if (hasPermissions()) {
                    scannerViewModel.startScan()
                    button.text = "Cancel Scan"
                } else {
                    requestPermissionLauncher.launch(requiredPermissions)
                }
            } else {
                if (hasPermissions()) {
                    scannerViewModel.stopScan()
                    button.text = "Scan"
                } else {
                    requestPermissionLauncher.launch(requiredPermissions)
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