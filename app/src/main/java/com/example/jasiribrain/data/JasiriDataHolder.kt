package com.example.jasiribrain.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object JasiriDataHolder {

    private val _studyActiveStatus = MutableStateFlow(false)

    val studyActiveStatus: StateFlow<Boolean>
        get() = _studyActiveStatus

    fun setStudyIsActiveStatus(status: Boolean) {
        _studyActiveStatus.value = status
    }

    private val _bluetoothActiveStatus = MutableStateFlow(false)

    val bluetoothActiveStatus: StateFlow<Boolean>
        get() = _bluetoothActiveStatus

    fun setBluetoothIsActiveStatus(status: Boolean) {
        _bluetoothActiveStatus.value = status
    }

}