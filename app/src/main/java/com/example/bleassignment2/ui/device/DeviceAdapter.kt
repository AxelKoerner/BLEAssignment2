package com.example.bleassignment2.ui.device

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.recyclerview.widget.RecyclerView
import com.example.bleassignment2.R

class DeviceAdapter(
    private var devices: List<Pair<BluetoothDevice, Int>>,
    private val onConnectClick: (BluetoothDevice) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    inner class DeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.device_name)
        val address: TextView = view.findViewById(R.id.device_address)
        val rssi: TextView = view.findViewById(R.id.device_rssi)
        val isConnectable: TextView = view.findViewById(R.id.device_isConnectable)
        val connectButton: Button = view.findViewById(R.id.connect_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val (device, rssi) = devices[position]
        holder.name.text = device.name ?: "N/A"
        holder.address.text = device.address
        holder.isConnectable.text = if (device.bondState == BluetoothDevice.BOND_NONE) "Not bonded" else "Bonded"
        holder.rssi.text = "$rssi dBm"
        holder.connectButton.setOnClickListener {
            onConnectClick(device)
        }
    }

    override fun getItemCount(): Int = devices.size

    fun updateDevices(newList: List<Pair<BluetoothDevice, Int>>) {
        devices = newList
        notifyDataSetChanged()
    }
}

