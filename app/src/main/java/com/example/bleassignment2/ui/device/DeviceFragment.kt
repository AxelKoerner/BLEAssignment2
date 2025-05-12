package com.example.bleassignment2.ui.device

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bleassignment2.MainActivity
import com.example.bleassignment2.databinding.FragmentDeviceBinding
import com.example.bleassignment2.ui.scanner.ScannerViewModel

class DeviceFragment : Fragment() {

    private var _binding: FragmentDeviceBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val deviceViewModel =
            ViewModelProvider(this.requireActivity()).get(ScannerViewModel::class.java)

        _binding = FragmentDeviceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDevice
        deviceViewModel.currentSelection?.observe(viewLifecycleOwner) @androidx.annotation.RequiresPermission(
            android.Manifest.permission.BLUETOOTH_CONNECT
        ) {
            textView.text = it.name
            println(textView.id)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}