package com.example.jasiriheart.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.jasiriheart.R
import java.util.*

class DevicesListAdapter(
    context: Context, resource: Int,
    devicesList: ArrayList<BluetoothDevice>
) : ArrayAdapter<BluetoothDevice?>(context, resource, devicesList as List<BluetoothDevice?>) {
    private val layoutInflater: LayoutInflater
    private val devicesList: ArrayList<BluetoothDevice>
    private val resourceId: Int
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        convertView = layoutInflater.inflate(resourceId, null)
        val bluetoothDevice = devicesList[position]
        if (bluetoothDevice != null) {
            val deviceName = convertView.findViewById<View>(R.id.tvDeviceName) as TextView
            val deviceAdd = convertView.findViewById<View>(R.id.tvDeviceAddress) as TextView
            if (deviceName != null) {
                deviceName.text = bluetoothDevice.name
                deviceAdd.text = bluetoothDevice.address
            }
        }
        return convertView
    }

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        resourceId = resource
        this.devicesList = devicesList
    }
}