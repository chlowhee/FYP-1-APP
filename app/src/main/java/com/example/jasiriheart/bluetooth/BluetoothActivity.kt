package com.example.jasiriheart.bluetooth
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.ProgressDialog
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.content.*
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.os.Handler
//import android.util.Log
//import android.view.View
//import android.widget.AdapterView.OnItemClickListener
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.AppCompatActivity
//import androidx.localbroadcastmanager.content.LocalBroadcastManager
//import com.example.jasiriheart.R
//import com.example.jasiriheart.data.DataStoreRepo
//import com.example.jasiriheart.databinding.ActivityBluetoothBinding
//import dagger.hilt.android.AndroidEntryPoint
//import java.util.*
//import kotlin.collections.ArrayList
//
//
//@AndroidEntryPoint
//class BluetoothActivity: AppCompatActivity() {
//
//    private lateinit var binding: ActivityBluetoothBinding
//
//    lateinit var bluetoothAdapter: BluetoothAdapter
//    lateinit var pairedDevices: ArrayList<BluetoothDevice>
//    lateinit var availDevices: ArrayList<BluetoothDevice>
//    lateinit var pairedDeviceListAdapter: DevicesListAdapter
//    lateinit var availDeviceListAdapter: DevicesListAdapter
//    lateinit var dataStoreRepo: DataStoreRepo
//
//    var connStatus: String? = null
//    lateinit var dialog: ProgressDialog
//
//    lateinit var sharedPreferences: SharedPreferences
//    lateinit var editor: SharedPreferences.Editor
//
//    lateinit var btService: BluetoothService
//    var bTDevice: BluetoothDevice? = null
//    val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//
//    var retryConnect = false
//    var reconnectionHandler: Handler = Handler()
//
//    var reconnectRunnable: Runnable = object : Runnable {
//        override fun run() {
//            try {
//                if (btService.connStatusFlag == false) {
//                    connectDevice(bTDevice, MY_UUID)
//                    Toast.makeText(
//                        applicationContext,
//                        "Successfuly reconnected!", Toast.LENGTH_SHORT
//                    ).show()
//                }
//                reconnectionHandler.removeCallbacks(this)
//                retryConnect = false
//            } catch (e: Exception) {
//                Toast.makeText(
//                    applicationContext,
//                    "Unable to reconnect, trying in 5 seconds",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityBluetoothBinding.inflate(layoutInflater)
//        val view: View = binding.root
//        setContentView(view)
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//
//        pairedDevices = ArrayList()
//        availDevices = ArrayList()
//
//        initExitBtn()
//        scanNewDevices()
//        pairedDeviceListView()
//        availDeviceListView()
//        setConnectedDeviceName()
//    }
//
//    override fun finish() {
//        super.finish()
//        val data = Intent()
//        data.putExtra("BTDevice", bTDevice)
//        data.putExtra("myUUID", MY_UUID)
//        setResult(RESULT_OK, data)
//    }
//
//    @SuppressLint("CommitPrefEdits")
//    override fun onBackPressed() {
//        super.onBackPressed()
//        sharedPreferences = applicationContext.getSharedPreferences(
//            "Shared Preferences",
//            MODE_PRIVATE
//        )
//        if (sharedPreferences.contains("connStatus")) {
//            connStatus = sharedPreferences.getString("connStatus", "")!!
//        }
//
//        binding.tvDeviceStatus.setText(connStatus)
//
//        editor = sharedPreferences.edit()
//        editor.putString("connStatus", binding.tvDeviceStatus.getText().toString())
//        editor.commit()
//        finish()
//    }
//
//    private fun initExitBtn() {
//        binding.exitBtn.setOnClickListener{
//            finish()
//        }
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun setConnectedDeviceName() {
//        if (bTDevice != null) {
//            val device = bTDevice!!.name
//            if (device != null) binding.tvDeviceStatus.text = device
//        } else {
//            binding.tvDeviceStatus.text = "" +
//                    "No device connected"
//        }
//    }
//
//    private fun pairedDeviceListView() {
//        binding.lvPairedDevices.onItemClickListener =
//            OnItemClickListener { adapterView, view, i, l ->
//                bluetoothAdapter.cancelDiscovery()
//                if (this::availDeviceListAdapter.isInitialized) binding.lvAvailDevices.adapter = availDeviceListAdapter
//
//                //                String deviceName = pairedDevices.get(i).getName();
//                //                String deviceAddress = pairedDevices.get(i).getAddress();
//                btService = BluetoothService(this)
//                bTDevice = pairedDevices[i]
//                if (bTDevice != null) {
//                    connectDevice(bTDevice, MY_UUID)
//                    setConnectedDeviceName()
//                }
//            }
//    }
//
//    private fun availDeviceListView() {
//        binding.lvAvailDevices.onItemClickListener =
//            OnItemClickListener { adapterView, view, i, l ->
//                bluetoothAdapter.cancelDiscovery()
//                if (this::pairedDeviceListAdapter.isInitialized) binding.lvPairedDevices.adapter = pairedDeviceListAdapter
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                    availDevices[i].createBond()
//                    btService = BluetoothService(this)
//                    bTDevice = availDevices[i]
//                }
//                if (bTDevice != null) {
//                    connectDevice(bTDevice, MY_UUID)
//                }
//            }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    fun scanNewDevices() {
//        availDevices.clear()
//        //scan devices
//        if (bluetoothAdapter.isDiscovering) {
//            bluetoothAdapter.cancelDiscovery()
//            checkPermissions()
//            if (bluetoothAdapter.startDiscovery()) {
//                val discoverIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
//                registerReceiver(bluetoothAvailDevicesReceiver, discoverIntent)
//            }
//        } else if (!bluetoothAdapter.isDiscovering) {
//            checkPermissions()
//            if (bluetoothAdapter.startDiscovery()) {
//                val discoverIntent = IntentFilter(BluetoothDevice.ACTION_FOUND)
//                registerReceiver(bluetoothAvailDevicesReceiver, discoverIntent)
//            }
//        }
//        pairedDevices.clear()
//        //get paired devices
//        val pairedDevicesList = bluetoothAdapter.bondedDevices
//        for (device in pairedDevicesList) {
//            pairedDevices.add(device!!)
//            pairedDeviceListAdapter = DevicesListAdapter(
//                this,
//                R.layout.lv_devices_list, pairedDevices
//            )
//            binding.lvPairedDevices.adapter = pairedDeviceListAdapter
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private fun checkPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            var permissionCheck = 0
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                permissionCheck =
//                    (this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION")
//                            + this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION"))
//            }
//            if (permissionCheck != 0) {
//                requestPermissions(
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ),
//                    1001
//                )
//            }
//        }
//    }
//
//    private val bluetoothStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.action
//            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
//                val state = intent.getIntExtra(
//                    BluetoothAdapter.EXTRA_STATE,
//                    BluetoothAdapter.ERROR
//                )
//                when (state) {
//                    BluetoothAdapter.STATE_OFF -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothStateReceiver: STATE OFF"
//                    )
//                    BluetoothAdapter.STATE_TURNING_OFF -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothStateReceiver: STATE TURNING OFF"
//                    )
//                    BluetoothAdapter.STATE_ON -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothStateReceiver: STATE ON"
//                    )
//                    BluetoothAdapter.STATE_TURNING_ON -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothStateReceiver: STATE TURNING ON"
//                    )
//                }
//            }
//        }
//    }
//
//    private val bluetoothScanReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.action
//            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED == action) {
//                val mode = intent.getIntExtra(
//                    BluetoothAdapter.EXTRA_SCAN_MODE,
//                    BluetoothAdapter.ERROR
//                )
//                when (mode) {
//                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothScanReceiver: Discoverability Enabled."
//                    )
//                    BluetoothAdapter.SCAN_MODE_CONNECTABLE -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothScanReceiver: Discoverability Disabled. Able to receive connections."
//                    )
//                    BluetoothAdapter.SCAN_MODE_NONE -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothScanReceiver: Discoverability Disabled. Not able to receive connections."
//                    )
//                    BluetoothAdapter.STATE_CONNECTING -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothScanReceiver: Connecting..."
//                    )
//                    BluetoothAdapter.STATE_CONNECTED -> Log.d(
//                        "BluetoothActivity",
//                        "bluetoothScanReceiver: Connected."
//                    )
//                }
//            }
//        }
//    }
//
//    var bluetoothAvailDevicesReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.action
//            // finding devices
//            if (BluetoothDevice.ACTION_FOUND == action) {
//
//                //get BluetoothDevice object from Intent
//                val bluetoothDevice: BluetoothDevice? =
//                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
//
//                //add name and address to an array adapter
//                availDevices.add(bluetoothDevice!!)
//                Log.d(
//                    "Bluetooth Activity", "onReceive: " +
//                            bluetoothDevice.name + " : " +
//                            bluetoothDevice.address
//                )
//                availDeviceListAdapter = DevicesListAdapter(
//                    applicationContext,
//                    R.layout.lv_devices_list,
//                    availDevices
//                )
//                binding.lvAvailDevices.adapter = availDeviceListAdapter
//            }
//        }
//    }
//
//    private val bluetoothBondReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        override fun onReceive(context: Context?, intent: Intent) {
//            val action = intent.action
//            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
//                val bluetoothDevice =
//                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                if (bluetoothDevice!!.bondState == BluetoothDevice.BOND_BONDED) {
//                    Toast.makeText(
//                        applicationContext, "Successfully paired with "
//                                + bluetoothDevice.name, Toast.LENGTH_LONG
//                    ).show()
//                    bTDevice = bluetoothDevice
//                }
//                if (bluetoothDevice.bondState == BluetoothDevice.BOND_BONDING) {
//                    Log.e("Bluetooth Bond", "BOND_BONDING")
//                }
//                if (bluetoothDevice.bondState == BluetoothDevice.BOND_NONE) {
//                    Log.e("Bluetooth Bond", "BOND_NONE")
//                }
//            }
//        }
//    }
//
//    private val connectionReceiver: BroadcastReceiver = object : BroadcastReceiver() {
//        @SuppressLint("SetTextI18n")
//        override fun onReceive(context: Context?, intent: Intent) {
//            val btDevice = intent.getParcelableExtra<BluetoothDevice>("Device")
//            val status = intent.getStringExtra("Status")
//
//            // TODO: change sharedPreferences to DataStore stuff
//            sharedPreferences =
//                applicationContext.getSharedPreferences(
//                    "Shared Preferences",
//                    Context.MODE_PRIVATE
//                )
//            editor = sharedPreferences.edit()
//
//            if (status == "connected") {
//                try {
//                    dialog.dismiss()
//                } catch (np: NullPointerException) {
//                    np.printStackTrace()
//                }
//                Toast.makeText(
//                    applicationContext, "Device now connected to "
//                            + btDevice!!.name, Toast.LENGTH_LONG
//                ).show()
//                editor.putString("connStatus", "Connected to " + btDevice.name)
//                binding.tvDeviceStatus.text = "Connected to " + btDevice.name
//            } else if (status == "disconnected" && retryConnect == false) {
//                Toast.makeText(
//                    applicationContext, "Disconnected from "
//                            + btDevice!!.name, Toast.LENGTH_LONG
//                ).show()
//                btService = BluetoothService(applicationContext)
//
//                sharedPreferences =
//                    getApplicationContext().getSharedPreferences(
//                        "Shared Preferences",
//                        Context.MODE_PRIVATE
//                    )
//                editor = sharedPreferences.edit()
//                editor.putString("connStatus", "Disconnected")
//                binding.tvDeviceStatus.text = "Disconnected"
//                editor.commit()
//                dataStoreRepo.setBluetoothIsConnected(false)
//                try {
//                    dialog.show()
//                } catch (e: java.lang.Exception) {
//                    Log.d("BluetoothActivity", "Dialog Exception: Failed to show dialog")
//                }
//                retryConnect = true
//                reconnectionHandler.postDelayed(reconnectRunnable, 5000)
//            }
//            editor.commit()
//        }
//    }
//
////    public void activateConnection(){
////        connectDevice(bTDevice, MY_UUID);
////    }
//
//    //    public void activateConnection(){
//    //        connectDevice(bTDevice, MY_UUID);
//    //    }
//    fun connectDevice(bluetoothDevice: BluetoothDevice?, uuid: UUID?) {
//        if (bluetoothDevice != null && uuid != null) {
//            btService.activateClientThread(bluetoothDevice, uuid)
//        }
//    }
//
//    fun unregisterBTReceivers() {
//        try {
//            unregisterReceiver(bluetoothBondReceiver)
//            unregisterReceiver(bluetoothScanReceiver)
//            unregisterReceiver(bluetoothStateReceiver)
//            unregisterReceiver(bluetoothAvailDevicesReceiver)
//            LocalBroadcastManager.getInstance(this).unregisterReceiver(connectionReceiver)
//        } catch (exception: IllegalArgumentException) {
//            exception.printStackTrace()
//        }
//    }
//
//}