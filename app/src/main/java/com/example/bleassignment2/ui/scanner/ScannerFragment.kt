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
import com.example.bleassignment2.databinding.FragmentScannerBinding

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
            ViewModelProvider(this).get(ScannerViewModel::class.java)

        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val button: Button = binding.scannerButton

        //backwards compatibility
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

        fun hasPermissions(): Boolean {
            return requiredPermissions.all {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

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