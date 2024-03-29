package com.example.jasiribrain.data

interface Constants {
    companion object {
        const val SCAN_DURATION_SEC = 30
        const val MESSAGE_INTERVAL_MS = 1000
        const val MAP_UPDATE_INTERVAL_MS = 1500
        const val BT_RECONNECT_INTERVAL_MS = 5000

        // request codes
        const val REQUEST_ENABLE_BT = 1001
        const val REQUEST_DISCOVER_BT = 1002
        const val REQUEST_PICK_BT_DEVICE = 1003
        const val REQUEST_SETTING = 1004
        const val REQUEST_SPEECH_INPUT = 1005
        const val REQUEST_LOCATION_PERMISSION = 2001

        // intent extra
        const val EXTRA_DEVICE_ADDRESS = "device_address"

        // Message types sent from the BluetoothChatService Handler
        const val MESSAGE_STATE_CHANGE = 1
        const val MESSAGE_READ = 2
        const val MESSAGE_WRITE = 3
        const val MESSAGE_DEVICE_NAME = 4
        const val MESSAGE_TOAST = 5

        // Key names received from the BluetoothChatService Handler
        const val DEVICE_NAME = "device_name"
        const val TOAST = "toast"

        // Study methods
        const val FORCE_START_SEL = 0
        const val POMODORO_SEL = 1
        const val MINUTE_IN_MILLIS = 60000L
        const val FORCE_START_TIME_MS = 2*MINUTE_IN_MILLIS        //  2min
        const val POMODORO_DEFAULT_TIME_MS = 25*MINUTE_IN_MILLIS   //  25min

        //Commands to mBot
        const val DEFAULT = "default"
        const val FWD = "A"
        const val BWD = "B"
        const val RIGHT = "C"
        const val LEFT = "D"
        const val FIDGET = "G"
        const val DANCE = "H"
        const val GAME = "L"
        const val GOOD_COP = "U"
        const val BAD_COP = "V"
    }
}