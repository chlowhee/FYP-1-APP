package com.example.jasiriheart.bluetooth

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.jasiriheart.data.Constants
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*


class BluetoothService {

    private var bluetoothAdapter: BluetoothAdapter? = null
    var context: Context? = null

    val MY_UUID = UUID.fromString("ADD1B8EE-6773-4B2D-BE72-B4553E3ADE56")

    private var acceptThread: AcceptThread? = null

    private var connectThread: ConnectThread? = null
    private var bTDevice: BluetoothDevice? = null
    private val uuidDevice: UUID? = null

    var progressDialog: ProgressDialog? = null
    var connectStatus: Intent? = null

    private var connectedThread: ConnectedThread? = null
    var connStatusFlag = false

    fun BluetoothService(context: Context?) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        this.context = context
        activateAcceptThread()
    }

    private class AcceptThread : Thread() {
        private val bluetoothServerSocket: BluetoothServerSocket?
        override fun run() {
            var socket: BluetoothSocket? = null
            // Keep listening until exception occurs or a socket is returned.
//            while (true) {
            try {
                socket = bluetoothServerSocket!!.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                //                    break;
            }
            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                connectedSocket(socket, socket.remoteDevice)
                //                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                bluetoothServerSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

        init {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            var tmp: BluetoothServerSocket? = null
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                    Constants.APP_NAME,
                    MY_UUID
                )
            } catch (e: IOException) {
                Log.e(TAG, "Socket's listen() method failed", e)
            }
            bluetoothServerSocket = tmp
        }
    }

    private class ConnectThread(bluetoothDevice: BluetoothDevice, uuid: UUID) :
        Thread() {
        private var bluetoothSocket: BluetoothSocket? = null
        override fun run() {
            var bTSocket: BluetoothSocket? = null
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                bTSocket = bTDevice.createRfcommSocketToServiceRecord(uuidDevice)
            } catch (e: IOException) {
                Log.e(TAG, "Socket's create() method failed", e)
            }
            bluetoothSocket = bTSocket

            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery()
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                bluetoothSocket!!.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                connectedSocket(bluetoothSocket, bTDevice)
            } catch (connectException: IOException) {
                // Unable to connect; close the socket and return.
                try {
                    bluetoothSocket!!.close()
                } catch (closeException: IOException) {
                    Log.e(TAG, "Could not close the client socket", closeException)
                }
                try {
                    val bluetoothActivity: BluetoothActivity = context as BluetoothActivity
                    bluetoothActivity.runOnUiThread(Runnable {
                        Toast.makeText(
                            context,
                            "Failed to connect to the Device.",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            try {
                progressDialog.dismiss()
            } catch (np: NullPointerException) {
                np.printStackTrace()
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                bluetoothSocket!!.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }

        init {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            bTDevice = bluetoothDevice
            uuidDevice = uuid
        }
    }

    @Synchronized
    fun activateAcceptThread() {
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }
        if (acceptThread != null) {
            acceptThread = AcceptThread()
            acceptThread!!.start()
        }
    }

    fun activateClientThread(bTDevice: BluetoothDevice, uuid: UUID) {
        try {
            progressDialog = ProgressDialog.show(
                context,
                "Connecting to Bluetooth",
                "Please wait ..", true
            )
        } catch (e: Exception) {
            Log.e("Bluetooth Service", "Client thread dialog failed to display")
        }
        connectThread = ConnectThread(bTDevice, uuid)
        connectThread!!.start()
    }

    private class ConnectedThread(socket: BluetoothSocket) : Thread() {
        private val socket: BluetoothSocket
        private val inputStream: InputStream?
        private val outputStream: OutputStream?
        override fun run() {
            val buffer = ByteArray(1024)
            var noBytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    noBytes = inputStream!!.read(buffer)
                    // Send the obtained bytes to the UI activity.
                    val msg = String(buffer, 0, noBytes)
                    val msgIntent = Intent("incomingMessage")
                    msgIntent.putExtra("receivedMessage", msg)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    connectStatus = Intent("ConnectionStatus")
                    connectStatus.putExtra("Status", "disconnected")
                    connectStatus.putExtra("Device", bTDevice)
                    LocalBroadcastManager.getInstance(context).sendBroadcast(connectStatus)
                    connStatusFlag = false
                    break
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        fun write(bytes: ByteArray?) {
            val text = String(bytes!!, Charset.defaultCharset())
            Log.d(TAG, "Write: Writing to output stream: $text")
            try {
                outputStream!!.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
            }
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

        //        private byte[] buffer; // mmBuffer store for the stream
        init {
            connectStatus = Intent("ConnectionStatus")
            connectStatus.putExtra("Status", "connected")
            connectStatus.putExtra("Device", bTDevice)
            LocalBroadcastManager.getInstance(context).sendBroadcast(connectStatus)
            connStatusFlag = true
            this.socket = socket
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when creating input stream", e)
            }
            try {
                tmpOut = socket.outputStream
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when creating output stream", e)
            }
            inputStream = tmpIn
            outputStream = tmpOut
        }
    }

    private fun connectedSocket(
        bluetoothSocket: BluetoothSocket,
        bluetoothDevice: BluetoothDevice
    ) {
        bTDevice = bluetoothDevice
        if (acceptThread != null) {
            acceptThread!!.cancel()
            acceptThread = null
        }
        connectedThread = ConnectedThread(bluetoothSocket)
        connectedThread!!.start()
    }

    fun write(out: ByteArray?) {
        var thread: ConnectedThread
        connectedThread!!.write(out)
    }

}