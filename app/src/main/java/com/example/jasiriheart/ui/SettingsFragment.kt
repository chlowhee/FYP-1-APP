package com.example.jasiriheart.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.jasiriheart.R
import com.example.jasiriheart.bluetooth.BluetoothActivity
import com.example.jasiriheart.data.DataStoreRepo
import com.example.jasiriheart.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    lateinit var bAdapter:BluetoothAdapter
    private val REQUEST_CODE_ENABLE_BT = 1;


    @Inject lateinit var dataStoreRepo: DataStoreRepo
    private lateinit var bluetoothActivity: BluetoothActivity

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

        onOffBluetooth()
        connectBT()
        setTextGetPairedStatus()
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
                    startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
                    text = "ON"
                } else {
//                turn off BT
                    bAdapter.disable()
                    Toast.makeText(activity, "Bluetooth is turned off", Toast.LENGTH_LONG).show()
                    text = "OFF"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (requestCode == Activity.RESULT_OK) {
                    Toast.makeText(activity, "Bluetooth is on", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Bluetooth is unable to turn on", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun connectBT() {
        binding.bluetoothConnect.setOnClickListener{
            if (bAdapter.isEnabled) {
                startActivity(Intent(activity, BluetoothActivity::class.java))
            } else {
                Toast.makeText(activity, "Please turn on bluetooth first", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     *
     */
}