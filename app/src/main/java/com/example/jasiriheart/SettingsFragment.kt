package com.example.jasiriheart

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bAdapter = BluetoothAdapter.getDefaultAdapter()

        if (bAdapter == null) {
            binding.getPairedDevice.text = "BT is not available"
        } else {
            //put waiting for pairing / device name
        }

        onBluetooth()
    }

    private fun checkBTIsConnected(): Boolean {
        var isConnected = false
        dataStoreRepo.bluetoothActive.observe(viewLifecycleOwner, {
            isConnected = it
        })
        return isConnected
    }

    //set image according to bt stat (datastorerepo)

    @SuppressLint("SetTextI18n")
    private fun onBluetooth() {
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
                    text = "OFF"
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (requestCode == Activity.RESULT_OK) {
                    Toast.makeText(activity, "Bluetooth is on", Toast.LENGTH_LONG).show()
                }
        }
    }

//    private fun discoverBT() {
//        binding.bluetoothConnect.setOnClickListener {
//            if (bAdapter.isEnabled) {
//                val devices = bAdapter.bondedDevices
//                for (device in devices) {
//                    val deviceName =
//                }
//            } else {
//                Toast.makeText(activity, "Turn on bluetooth first", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

}