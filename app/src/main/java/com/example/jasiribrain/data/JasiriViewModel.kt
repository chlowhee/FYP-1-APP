package com.example.jasiribrain.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData

class JasiriViewModel: ViewModel() {

    val bluetoothConnectionStatus: LiveData<Boolean> = liveData {
                JasiriDataHolder.bluetoothActiveStatus.collect{ emit(it) }
    }

    val isStudyingStatus: LiveData<Boolean> = liveData {
            JasiriDataHolder.timerActiveStatus.collect{emit(it)}
    }

    val studyMethodStatus: LiveData<Int> = liveData {
        JasiriDataHolder.studyMethodSelect.collect{emit(it)}
    }

    val pomodoroDurationStatus: LiveData<Int> = liveData {
        JasiriDataHolder.pomodoroDuration.collect{emit(it)}
    }

    val breakDurationStatus: LiveData<Int> = liveData {
        JasiriDataHolder.breakDuration.collect{emit(it)}
    }

    val numCyclesStatus: LiveData<Int> = liveData {
        JasiriDataHolder.numCyclesCounter.collect{emit(it)}
    }

    val getjoystickCmdStatus: LiveData<String> = liveData {
        JasiriDataHolder.joystickCmdStatus.collect{emit(it)}
    }

    val getRpiReadyStatus: LiveData<Boolean> = liveData {
        JasiriDataHolder.rpiReadyStatus.collect{emit(it)}
    }

    val checkEyeDetectionStatus: LiveData<Boolean> = liveData {
        JasiriDataHolder.eyeDetectionIsWanted.collect { emit(it) }
    }

    val checkEyeIsSleepyStatus: LiveData<Boolean> = liveData {
        JasiriDataHolder.eyesAreSleepy.collect { emit(it) }
    }

    val checkFacePositionStatus: LiveData<Int> = liveData {
        JasiriDataHolder.facePosition.collect { emit(it) }
    }

    val checkFaceTrackingStatus: LiveData<Boolean> = liveData {
        JasiriDataHolder.faceTrackingIsWanted.collect { emit(it) }
    }

}