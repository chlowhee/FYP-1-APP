package com.example.jasiribrain.common;

public interface BluetoothStatusListener {
    void onStateChanges(int state);
    void onCommunicate(String message);

}