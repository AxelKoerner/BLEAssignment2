package com.example.bleassignment2.ui.device

import android.content.BroadcastReceiver
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
import java.nio.ByteBuffer

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

        val tv_deviceName: TextView = binding.deviceName
        val tv_deviceAddress: TextView = binding.deviceAddress
        //val textView: TextView = binding.deviceList.
        binding.serviceLayoutReadNotify1.visibility = View.GONE
        binding.serviceLayoutReadNotify2.visibility = View.GONE
        binding.serviceLayoutWrite.visibility = View.GONE
        binding.debugTextField.visibility = View.GONE

        scannerViewModel.currentSelection.observe(viewLifecycleOwner)
        @RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION])
        { selectedBluetoothDevice ->
            if (selectedBluetoothDevice == null) {
                binding.serviceLayoutReadNotify1.visibility = View.GONE
                binding.serviceLayoutReadNotify2.visibility = View.GONE
                binding.serviceLayoutWrite.visibility = View.GONE
                return@observe
            }
            tv_deviceName.text = selectedBluetoothDevice.name ?: "Unknown Name"
            tv_deviceAddress.text = selectedBluetoothDevice.address
        }
        scannerViewModel.temperature.observe(viewLifecycleOwner) {
            if (it == null || scannerViewModel.currentSelection.value == null) {
                binding.serviceLayoutReadNotify1.visibility = View.GONE
                return@observe
            }
            binding.serviceLayoutReadNotify1.visibility = View.VISIBLE
            binding.read1CharacteristicName.text = "temperatur"
            binding.read1CharacteristicUuid.text = "found characteristic"
            binding.read1CharacteristicValue.text = "$it °C"
            binding.read1CharacteristicReadButton.setOnClickListener(
                @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
                    scannerViewModel.initReadCharacteristic("temp")
                })
            binding.read1CharacteristicNotifyButton.setOnClickListener(
                @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
                    scannerViewModel.initNotifyCharacteristic("temp")
                })

        }
        scannerViewModel.humidity.observe(viewLifecycleOwner) {
            if (it == null || scannerViewModel.currentSelection.value == null) {
                binding.serviceLayoutReadNotify2.visibility = View.GONE
                return@observe
            }
            binding.serviceLayoutReadNotify2.visibility = View.VISIBLE
            binding.read2CharacteristicName.text = "humidity"
            binding.read2CharacteristicUuid.text = "found characteristic"
            binding.read2CharacteristicValue.text = "$it %"
            binding.read2CharacteristicReadButton.setOnClickListener(
                @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
                    scannerViewModel.initReadCharacteristic("hum")
                })
            binding.read2CharacteristicNotifyButton.setOnClickListener(
                @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
                    scannerViewModel.initNotifyCharacteristic("hum")
                })

        }
        scannerViewModel.light.observe(viewLifecycleOwner) {
            if (it == null || scannerViewModel.currentSelection.value == null) {
                binding.serviceLayoutWrite.visibility = View.GONE
                return@observe
            }
            binding.serviceLayoutWrite.visibility = View.VISIBLE

            binding.writeCharacteristicWriteButton.setOnClickListener(
                @RequiresPermission(android.Manifest.permission.BLUETOOTH_CONNECT) {
                    scannerViewModel.initWriteCharacteristic(
                        "light",
                        ByteBuffer.allocate(Short.SIZE_BYTES)
                            .putShort(binding.writeCharacteristicValue.progress.toShort()).array()
                    )
                })

            binding.debugTextField.text = it.toString()
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
    }
}