package com.example.bleassignment2.ui.device

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
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val deviceViewModel =
            ViewModelProvider(this.requireActivity()).get(DeviceViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val tv_deviceName: TextView = binding.deviceName
        val tv_deviceAddress: TextView = binding.deviceAddress
        //val textView: TextView = binding.deviceList.
        deviceViewModel.currentSelection.observe(viewLifecycleOwner)
        @RequiresPermission(allOf = [android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.ACCESS_FINE_LOCATION])
        { selectedBluetoothDevice ->
            if (selectedBluetoothDevice == null) {
                binding.serviceLayoutReadNotify1.visibility = View.GONE
                binding.serviceLayoutReadNotify2.visibility = View.GONE
                binding.serviceLayoutWrite.visibility = View.GONE
                return@observe
            }
            tv_deviceName.text = selectedBluetoothDevice.name?:"Unknown Name"
            tv_deviceAddress.text = selectedBluetoothDevice.address
        }
        deviceViewModel.broadcastData.observe(viewLifecycleOwner){
            data ->
                binding.debugTextField.text = data.toString()
        }


        val readButton: Button = binding.read1CharacteristicReadButton
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