package com.example.jasiribrain.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.collect

class JasiriViewModel: ViewModel() {

    val bluetoothConnectionStatus: LiveData<Boolean> = liveData {
                JasiriDataHolder.bluetoothActiveStatus.collect{ emit(it) }
    }

    val isStudyingStatus: LiveData<Boolean> = liveData {
            JasiriDataHolder.studyActiveStatus.collect{emit(it)}
    }

    val getjoystickCmdStatus: LiveData<String> = liveData {
        JasiriDataHolder.joystickCmdStatus.collect{emit(it)}
    }

    //TODO: RPI READY STATUS

}