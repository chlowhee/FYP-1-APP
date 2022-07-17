package com.example.jasiriheart.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jasiriheart.bluetooth.BTDeviceFragment
import com.example.jasiriheart.bluetooth.BluetoothController
import com.example.jasiriheart.data.Constants
import com.example.jasiriheart.data.DataStoreRepo
import com.example.jasiriheart.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    lateinit var bAdapter:BluetoothAdapter
    @Inject lateinit var dataStoreRepo: DataStoreRepo
    private val btFragment = BTDeviceFragment()
    private val controller = BluetoothController(activity)

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

        initBTUi()
        onOffBluetooth()
        connectBT()
        setTextGetPairedStatus()
        initTestBtn()
    }

    /**
     *   BLUETOOTH
     */
    private fun checkBTIsConnected(): Boolean {
        var isConnected = false
        dataStoreRepo.bluetoothActive.observe(viewLifecycleOwner, {
            isConnected = it
        })
        return isConnected
    }

    private fun initBTUi() {
        if (bAdapter.isEnabled) {
            binding.run {
                getPairedStatus.visibility = View.VISIBLE
                bluetoothOnOff.text = "ON"
            }
        } else {
            binding.run {
                getPairedStatus.visibility = View.INVISIBLE
                bluetoothOnOff.text = "OFF"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTextGetPairedStatus() {
        binding.run {
            if (checkBTIsConnected()) {
                getPairedStatus.text = "Device connected"
            } else {
                getPairedStatus.text = "No devices connected"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onOffBluetooth() {
        binding.bluetoothOnOff.run {
            setOnClickListener {
                if (text == "OFF") {
//                turn on BT
                    var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivityForResult(intent, Constants.REQUEST_ENABLE_BT)
//                    text = "ON"
                } else {
//                turn off BT
                    bAdapter.disable()
                    Toast.makeText(activity, "Bluetooth is turned off", Toast.LENGTH_LONG).show()
                    text = "OFF"
                }
            }
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        super.onOptionsItemSelected(item)
//        val intent = Intent(activity, DynamicActivity::class.java).putExtra(Constants.EXTRA_FRAGMENT, Constants.REQUEST_PICK_BT_DEVICE)
//        startActivityForResult(intent, Constants.REQUEST_PICK_BT_DEVICE)
//        return true
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Constants.REQUEST_ENABLE_BT ->
                if (resultCode == Activity.RESULT_OK) {
                    if (bAdapter.isEnabled) {
                        binding.bluetoothOnOff.text = "ON"
                        Toast.makeText(activity, "Bluetooth is on", Toast.LENGTH_LONG).show()
                    }
                }
            Constants.REQUEST_PICK_BT_DEVICE ->
                if (resultCode == Activity.RESULT_OK) {
                    // connect to device
                    val address = data!!.extras!!.getString(Constants.EXTRA_DEVICE_ADDRESS)
                    Log.d(TAG, "pick bt device $address")
                    if (controller != null) {
                        Log.d(TAG, "isit in here?")
                        controller.connectDevice(address, true)
                    }
                }
            else -> {
                Log.d(TAG, "what request code?")}
        }
    }

    private fun connectBT() {
        binding.bluetoothConnect.setOnClickListener{
            if (bAdapter.isEnabled) {
                val intent = Intent(activity, DynamicActivity::class.java)
//                startActivity(intent)
                startActivityForResult(intent, Constants.REQUEST_PICK_BT_DEVICE)
            } else {
                Toast.makeText(activity, "Please turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initTestBtn(){
        binding.testBtn.setOnClickListener{
            controller.sendMessage("GG")
        }
    }

    /**
     *
     */
}