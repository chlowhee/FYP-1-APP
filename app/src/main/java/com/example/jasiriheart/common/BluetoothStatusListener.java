package com.example.jasiriheart.common;

//import app.entity.MDPMessage;

public interface BluetoothStatusListener {
    void onStateChanges(int state);
    void onCommunicate(String message);
//    void onToastMessage(String message);

}