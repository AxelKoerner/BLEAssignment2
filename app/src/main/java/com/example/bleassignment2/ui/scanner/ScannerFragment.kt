package com.example.bleassignment2.ui.scanner

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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

    // Any positive integer is possible to request the enable of bluetooth
    companion object {
        private const val REQUEST_ENABLE_BT = 1
    }

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
        val adapter = DeviceAdapter(
            emptyList(),
            { device ->
                scannerViewModel.connectToDevice(device)
            },
            { device ->
                scannerViewModel.disconnect()
            },
            { device ->
                scannerViewModel.isDeviceConnected(device)
            }
        )

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
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
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
            println("Scan_button clicked")
            if (scannerViewModel.isScanning()) {

                println("is-scanning")
                if (hasPermissions()) {
                    println("stopping scan")
                    scannerViewModel.stopScan()
                    //button.text = "Scan"
                } else {
                    requestPermissionLauncher.launch(requiredPermissions)
                }
            } else {
                println("isnt-scanning")
                if (hasPermissions()) {
                    println("starting scan")

                    scannerViewModel.startScan()
                    //button.text = "Cancel Scan"
                } else {
                    requestPermissionLauncher.launch(requiredPermissions)
                }
            }
        }
        scannerViewModel.buttonText.observe(viewLifecycleOwner) @androidx.annotation.RequiresPermission(
            allOf = [android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ]
        ){
            button.text = it
        }

        button.text


        // This part is only used to ask for permission to enable bluetooth, if not enabled already
        val bluetoothManager: BluetoothManager = requireContext().getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}