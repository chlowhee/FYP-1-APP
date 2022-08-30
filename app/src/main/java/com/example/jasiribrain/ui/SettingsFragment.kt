package com.example.jasiribrain.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jasiribrain.R
import com.example.jasiribrain.bluetooth.BluetoothChatService
import com.example.jasiribrain.bluetooth.BluetoothController
import com.example.jasiribrain.common.BluetoothStatusListener
import com.example.jasiribrain.data.Constants
import com.example.jasiribrain.data.DataStoreRepo
import com.example.jasiribrain.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(), BluetoothStatusListener {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var bAdapter:BluetoothAdapter
    @Inject lateinit var dataStoreRepo: DataStoreRepo
    private val controller = BluetoothController(activity)
    private var address = ""
    private var btConnected = false

    private val TAG = "Settings Frag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bAdapter.isEnabled) {dataStoreRepo.setBluetoothIsOn(true)}
        controller.registerListener(this)
        onStateChanges(controller.getState())

        initBTUi()
        onOffBluetooth()
        connectBT()
        initTestBtn()
    }

    /**
     *   BLUETOOTH
     */
    private fun initBTUi() {
        dataStoreRepo.bluetoothIsActive.observe(viewLifecycleOwner, {
            if (it) {binding.bluetoothOnOff.text = getString(R.string.on)}
            else { binding.bluetoothOnOff.text = getString(R.string.off)}
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onOffBluetooth() {
        binding.bluetoothOnOff.run {
            setOnClickListener {
                if (text == "OFF") {
//                turn on BT
                    var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, Constants.REQUEST_ENABLE_BT)
                } else {
//                turn off BT
                    bAdapter.disable()
                    dataStoreRepo.setBluetoothIsOn(false)
                    Toast.makeText(activity, "Bluetooth is turned off", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onStateChanges(state: Int) {
        binding.getPairedStatus.run {
            when (state) {
                BluetoothChatService.STATE_CONNECTED -> {
                    text = "Connected: " //+ controller.connectedDeviceName
                    btConnected = true
                }
                BluetoothChatService.STATE_CONNECTING -> text = getString(R.string.connecting)
                BluetoothChatService.STATE_LISTEN -> text = getString(R.string.waiting)
                BluetoothChatService.STATE_NONE -> {
                    text = getString(R.string.no_devices_connected)
                    btConnected = false
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCommunicate(message: String?) {
        when (message) {
            "K" -> {
                Toast.makeText(activity, "Jasiri connected", Toast.LENGTH_LONG).show()
                binding.getPairedStatus.text = "Connected: " + controller.connectedDeviceName
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Constants.REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    if (bAdapter.isEnabled) {
                        dataStoreRepo.setBluetoothIsOn(true)
                        Toast.makeText(activity, "Bluetooth is on", Toast.LENGTH_LONG).show()
                    }
                }
            Constants.REQUEST_PICK_BT_DEVICE ->
                if (resultCode == Activity.RESULT_OK) {
                    // connect to device
                    address = data!!.extras!!.getString(Constants.EXTRA_DEVICE_ADDRESS).toString()
                    Log.d(TAG, "pick bt device $address")
                    controller.connectDevice(address, true)
                }
            else -> {
                Log.d(TAG, "what request code?")
            }
        }
    }

    private fun reconnectDevice(addr: String) {
        Log.d(TAG, "reconnecting device")
        controller.stopService()
        controller.connectDevice(addr, true)
    }

    private fun connectBT() {
        binding.bluetoothConnect.setOnClickListener{
            if (btConnected) {
                reconnectDevice(address)
            }
            if (bAdapter.isEnabled) {
                val intent = Intent(activity, DynamicActivity::class.java)
                startActivityForResult(intent, Constants.REQUEST_PICK_BT_DEVICE)
            } else {
                Toast.makeText(activity, "Please turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     *
     */
    private fun initTestBtn(){
        binding.testBtn.setOnClickListener{
            controller.sendMessage("A") //fwd cmd in mBot
        }
    }

}