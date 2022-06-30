package com.example.jasiriheart.bluetooth

import android.Manifest
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.jasiriheart.databinding.BluetoothConnectFragBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import com.example.jasiriheart.R


@AndroidEntryPoint
class FragBluetoothConnection: Fragment() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var pairedDevices: ArrayList<BluetoothDevice>
    lateinit var availDevices: ArrayList<BluetoothDevice>
    lateinit var pairedDeviceListAdapter: DevicesListAdapter
    lateinit var availDeviceListAdapter: DevicesListAdapter

    private var pairedDeviceListView = binding.lvPairedDevices
    private var availDeviceListView = binding.lvAvailDevices
    lateinit var connStatus: String
    lateinit var dialog: ProgressDialog

    lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    lateinit var btService: BluetoothService
    lateinit var bTDevice: BluetoothDevice
    val MY_UUID: UUID = UUID.fromString("ADD1B8EE-6773-4B2D-BE72-B4553E3ADE56")

    var retryConnect = false
    var reconnectionHandler: Handler = Handler()

    var reconnectRunnable: Runnable = object : Runnable {
        override fun run() {
            try {
                if (btService.connStatusFlag == false) {
                    connectDevice(bTDevice, MY_UUID)
                    Toast.makeText(
                        activity,
                        "Successfuly reconnected!", Toast.LENGTH_SHORT
                    ).show()
                }
                reconnectionHandler.removeCallbacks(this)
                retryConnect = false
            } catch (e: Exception) {
                Toast.makeText(
                    activity,
                    "Unable to reconnect, trying in 5 seconds",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private var _binding: BluetoothConnectFragBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = BluetoothConnectFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true)

        //get default adapter

        //get default adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        pairedDevices = ArrayList()
        availDevices = ArrayList()

        exitBtn()
        pairedDeviceListView()
        availDeviceListView()
    }

    private fun exitBtn() {
        binding.exitBtn.setOnClickListener{
            val parentFragmgr = parentFragmentManager

            //close bt frag
        }
    }

    private fun pairedDeviceListView() {
        pairedDeviceListView.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                bluetoothAdapter.cancelDiscovery()
                availDeviceListView.adapter = availDeviceListAdapter

                //                String deviceName = pairedDevices.get(i).getName();
                //                String deviceAddress = pairedDevices.get(i).getAddress();
                btService = BluetoothService(requireContext())
                bTDevice = pairedDevices[i]
                if (bTDevice != null) {
                    connectDevice(bTDevice, MY_UUID)
                }
            }
    }

    private fun availDeviceListView() {
        availDeviceListView.onItemClickListener =
            OnItemClickListener { adapterView, view, i, l ->
                bluetoothAdapter.cancelDiscovery()
                pairedDeviceListView.adapter = pairedDeviceListAdapter
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    availDevices[i].createBond()
                    btService = BluetoothService(requireContext())
                    bTDevice = availDevices[i]
                }
                if (bTDevice != null) {
                    connectDevice(bTDevice, MY_UUID)
                }
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun scanNewDevices(menuItem: MenuItem?) {
        availDevices.clear()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                Toast.makeText(
                    requireContext(),
                    "Please turn on Bluetooth",
                    Toast.LENGTH_LONG
                ).show()
            }
            //scan devices
            if (bluetoothAdapter.isDiscovering) {
                bluetoothAdapter.cancelDiscovery()
                checkPermissions()
                bluetoothAdapter.startDiscovery()
                val discoverIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(bluetoothAvailDevicesReceiver, discoverIntent)
            } else if (!bluetoothAdapter.isDiscovering) {
                checkPermissions()
                bluetoothAdapter.startDiscovery()
                val discoverIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
                registerReceiver(bluetoothAvailDevicesReceiver, discoverIntent)
            }
            pairedDevices.clear()
            //get paired devices
            val pairedDevicesList = bluetoothAdapter.bondedDevices
            for (device in pairedDevicesList) {
                pairedDevices.add(device!!)
                pairedDeviceListAdapter = DevicesListAdapter(
                    requireContext(),
                    R.layout.lv_devices_list, pairedDevices
                )
                pairedDeviceListView.adapter = pairedDeviceListAdapter
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            var permissionCheck = 0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionCheck =
                    (this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
                            + this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION"))
            }
            if (permissionCheck != 0) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    1001
                )
            }
        }
    }

    private val bluetoothStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                val state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )
                when (state) {
                    BluetoothAdapter.STATE_OFF -> Log.d(
                        "BluetoothActivity",
                        "bluetoothStateReceiver: STATE OFF"
                    )
                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
                        "BluetoothActivity",
                        "bluetoothStateReceiver: STATE TURNING OFF"
                    )
                    BluetoothAdapter.STATE_ON -> Log.d(
                        "BluetoothActivity",
                        "bluetoothStateReceiver: STATE ON"
                    )
                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(
                        "BluetoothActivity",
                        "bluetoothStateReceiver: STATE TURNING ON"
                    )
                }
            }
        }
    }

    private val bluetoothScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED == action) {
                val mode = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_SCAN_MODE,
                    BluetoothAdapter.ERROR
                )
                when (mode) {
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> Log.d(
                        "BluetoothActivity",
                        "bluetoothScanReceiver: Discoverability Enabled."
                    )
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> Log.d(
                        "BluetoothActivity",
                        "bluetoothScanReceiver: Discoverability Disabled. Able to receive connections."
                    )
                    BluetoothAdapter.SCAN_MODE_NONE -> Log.d(
                        "BluetoothActivity",
                        "bluetoothScanReceiver: Discoverability Disabled. Not able to receive connections."
                    )
                    BluetoothAdapter.STATE_CONNECTING -> Log.d(
                        "BluetoothActivity",
                        "bluetoothScanReceiver: Connecting..."
                    )
                    BluetoothAdapter.STATE_CONNECTED -> Log.d(
                        "BluetoothActivity",
                        "bluetoothScanReceiver: Connected."
                    )
                }
            }
        }
    }

    var bluetoothAvailDevicesReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action

            // finding devices
            if (BluetoothDevice.ACTION_FOUND == action) {

                //get BluetoothDevice object from Intent
                val bluetoothDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                //add name and address to an array adapter
                availDevices.add(bluetoothDevice!!)
                Log.d(
                    "Bluetooth Activity", "onReceive: " +
                            bluetoothDevice.name + " : " +
                            bluetoothDevice.address
                )
                availDeviceListAdapter = DevicesListAdapter(
                    requireContext(),
                    R.layout.lv_devices_list,
                    availDevices
                )
                availDeviceListView.adapter = availDeviceListAdapter
            }
        }
    }

    private val bluetoothBondReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val bluetoothDevice =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (bluetoothDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(
                        requireContext(), "Successfully paired with "
                                + bluetoothDevice.name, Toast.LENGTH_LONG
                    ).show()
                    bTDevice = bluetoothDevice
                }
                if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDING) {
                    Log.e("Bluetooth Bond", "BOND_BONDING")
                }
                if (bluetoothDevice.bondState == BluetoothDevice.BOND_NONE) {
                    Log.e("Bluetooth Bond", "BOND_NONE")
                }
            }
        }
    }

    private val connectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val btDevice = intent.getParcelableExtra<BluetoothDevice>("Device")
            val status = intent.getStringExtra("Status")
            sharedPreferences =
                ApplicationProvider.getApplicationContext<Context>().getSharedPreferences(
                    "Shared Preferences",
                    Context.MODE_PRIVATE
                )
            editor = sharedPreferences.edit()
            if (status == "connected") {
                try {
                    dialog.dismiss()
                } catch (np: NullPointerException) {
                    np.printStackTrace()
                }
                Toast.makeText(
                    requireContext(), "Device now connected to "
                            + btDevice!!.name, Toast.LENGTH_LONG
                ).show()
                editor.putString("connStatus", "Connected to " + btDevice.name)
                binding.tvDeviceStatus.text = "Connected to " + btDevice.name
            } else if (status == "disconnected" && retryConnect == false) {
                Toast.makeText(
                    requireContext(), "Disconnected from "
                            + btDevice!!.name, Toast.LENGTH_LONG
                ).show()
                btService = BluetoothService(requireContext())
                sharedPreferences =
                    ApplicationProvider.getApplicationContext<Context>().getSharedPreferences(
                        "Shared Preferences",
                        Context.MODE_PRIVATE
                    )
                editor = sharedPreferences.edit()
                editor.putString("connStatus", "Disconnected")
                binding.tvDeviceStatus.text = "Disconnected"
                editor.commit()
                try {
                    dialog.show()
                } catch (e: java.lang.Exception) {
                    Log.d("BluetoothActivity", "Dialog Exception: Failed to show dialog")
                }
                retryConnect = true
                reconnectionHandler.postDelayed(reconnectRunnable, 5000)
            }
            editor.commit()
        }
    }

//    public void activateConnection(){
//        connectDevice(bTDevice, MY_UUID);
//    }

    //    public void activateConnection(){
    //        connectDevice(bTDevice, MY_UUID);
    //    }
    fun connectDevice(bluetoothDevice: BluetoothDevice?, uuid: UUID?) {
        if (bluetoothDevice != null && uuid != null) {
            btService.activateClientThread(bluetoothDevice, uuid)
        }
    }


}