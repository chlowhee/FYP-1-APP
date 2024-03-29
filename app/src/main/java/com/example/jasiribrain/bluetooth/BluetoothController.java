package com.example.jasiribrain.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

import com.example.jasiribrain.common.BluetoothStatusListener;
import com.example.jasiribrain.data.Constants;

public class BluetoothController {
    BluetoothChatService service;
    BluetoothAdapter btAdapter;
    String connectedDeviceName;
    ArrayList<BluetoothStatusListener> listeners;

    public BluetoothController(Context context){
        this.service = new BluetoothChatService(context, mHandler);
        this.listeners = new ArrayList<>();
        this.btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * return connected device name
     * @return
     */
    public String getConnectedDeviceName(){
        return connectedDeviceName;
    }

    /**
     * register listener to receive bluetooth status updates
     * @param listener
     */
    public void registerListener(BluetoothStatusListener listener){
        this.listeners.add(listener);
    }

    /**
     * Unregister for bluetooth status listener
     * @param listener
     */
    public void unregisterListener(BluetoothStatusListener listener){
        this.listeners.remove(listener);
    }

    @SuppressLint("MissingPermission")
    public void setEnabled(boolean enabling){
        if (enabling){
            btAdapter.enable();
        } else {
            btAdapter.disable();
        }
    }

    public boolean isEnabled(){
        return btAdapter.isEnabled();
    }

    public boolean isSupported(){
        return btAdapter != null;
    }

    public int getState(){
        return service.getState();
    }

    /**
     * checking connection status
     * @return true if app.service is connected with bluetooth device
     */
    public boolean isConnected(){
        return service.getState() == BluetoothChatService.STATE_CONNECTED;
    }

    public boolean shouldReconnect(){
        return isSupported() && (service.getState()==BluetoothChatService.STATE_NONE || service.getState()==BluetoothChatService.STATE_LISTEN);
    }

    /***
     * start a connection with Bluetooth device
     * @param address Bluetooth address
     * @param secure state whether connection is secure or insecure
     */
    public void connectDevice(String address, boolean secure) {
        // Get the BluetoothDevice object
        BluetoothDevice device = btAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        service.connect(device, secure);
    }

    /**
     * Sending message to bluetooth device
     * @param message string to be sent
     */
    public void sendMessage(String message) {
        if (!isConnected()) {
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            service.write(send);
        }
        Log.d("FML", "msg sent");
    }

    /**
     * Stop all threads
     */
    public void stopService(){
        if (service != null) {
            service.stop();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    for (BluetoothStatusListener listener: listeners){
                        listener.onStateChanges(msg.arg1);
                    }
                    break;
                case Constants.MESSAGE_READ:
                    // read message object instead of buffer
                    String strMsg = (String) msg.obj;
                    String inMessage = strMsg;
                    for (BluetoothStatusListener listener: listeners){
                        listener.onCommunicate(inMessage);
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    connectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    break;
            }
        }
    };
}
